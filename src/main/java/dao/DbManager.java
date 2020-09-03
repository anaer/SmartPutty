package dao;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.eclipse.jface.dialogs.MessageDialog;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.collection.CollectionUtil;
import enums.ProtocolEnum;
import lombok.extern.slf4j.Slf4j;
import model.ConfigSession;
import ui.MainFrame;

/**
 * 数据库管理.
 *
 * @author lvcn
 * @version $Id: DBManager.java, v 1.0 Jul 22, 2019 3:45:12 PM lvcn Exp $
 */
@Slf4j
public class DbManager {
    private static DbManager    manager;
    private Connection          conn;
    private boolean             existCSessionTable;

    private DbManager() {
        try {
            String dbPath = MainFrame.configuration.getDatabasePath();
            if(StringUtils.isBlank(dbPath)){
                dbPath = "config\\sessiondb";
            }
            String driver = "org.hsqldb.jdbcDriver";
            String protocol = "jdbc:hsqldb:";
            Class.forName(driver).newInstance();
            String user = "sa";
            String password = "";
            Properties props = new Properties();
            props.put("user", user);
            props.put("password", password);
            props.put("jdbc.strict_md", "false");
            props.put("jdbc.get_column_name", "false");
            props.put("shutdown", "true");
            conn = DriverManager.getConnection(protocol + dbPath, props);
            log.debug("Connect to Database");
        } catch (Exception e) {
            log.error("初始化数据库连接异常.", e);
            MessageDialog.openError(null, "错误", "数据库连接异常.");
        }

        try (Statement state = conn.createStatement();) {
            String[] str = { "TABLE" };
            DatabaseMetaData meta = conn.getMetaData();
            ResultSet result = meta.getTables(null, null, null, str);
            while (result.next()) {
                log.debug("Database Meta: {}, {}, {}, {}, {}", result.getString("TABLE_CAT"),
                    result.getString("TABLE_SCHEM"), result.getString("TABLE_NAME"),
                    result.getString("TABLE_TYPE"), result.getString("REMARKS"));

                if ("CSESSION".equals(result.getString("TABLE_NAME"))) {
                    existCSessionTable = true;
                }
            }
            result.close();
            if (!existCSessionTable) {
                String sql = "CREATE TABLE CSession(Name varchar(100), Host varchar(50), Port varchar(10), Username varchar(50), Protocol varchar(10), Key varchar(100), Password varchar(50), Session varchar(50), PRIMARY KEY (Host,Port,Username,Protocol))";
                state.execute(sql);
            }

        } catch (Exception e) {
            log.error("初始化表结构异常.", e);
        }
    }

    public static DbManager getDBManagerInstance() {
        if (manager == null) {
            manager = new DbManager();
        }
        return manager;
    }

    public void insertCSession(ConfigSession configSession) {
        String name = configSession.getName();
        String host = configSession.getHost();
        String port = configSession.getPort();
        String user = configSession.getUser();
        String protocol = configSession.getProtocol().name();
        String session = configSession.getSession();
        String key = configSession.getKey();
        String password = Base64.encode(configSession.getPassword());
        if (host.isEmpty() || port.isEmpty() || user.isEmpty() || protocol.isEmpty()) {
            MessageDialog.openWarning(MainFrame.SHELL, "Warning",
                "host,port,user,protocol should not be set to null");
            return;
        }
        if (isCSessionExist(configSession)) {
            log.info("session已存在, 先删除当前session.");
            deleteCSession(configSession);
        }

        String sql = "INSERT INTO CSession VALUES('" + name + "','" + host + "','" + port + "','"
                + user + "','" + protocol + "','" + key + "','" + password + "','" + session + "')";

        try (Statement state = conn.createStatement();) {
            state.execute(sql);
        } catch (SQLException e) {
            log.error("执行insertCSession异常.", e);
        }
    }

    /**
     * 删除会话.
     *
     * @param csession
     */
    public void deleteCSession(ConfigSession csession) {
        if (!isCSessionExist(csession)) {
            return;
        }

        try (Statement state = conn.createStatement()) {
            String sql = "DELETE FROM CSESSION WHERE host='" + csession.getHost() + "' AND port='"
                    + csession.getPort() + "' AND username='" + csession.getUser()
                    + "' AND protocol='" + csession.getProtocol() + "'";
            state.execute(sql);
        } catch (SQLException e) {
            log.error("删除会话失败.", e);
        }
    }

    public List<ConfigSession> getAllCSessions() {
        String sql = "SELECT *  FROM CSESSION order by name";
        return qryCSession(sql);
    }

    public List<ConfigSession> queryCSessionByHost(String host) {
        String sql = "SELECT *  FROM CSESSION WHERE host='" + host + "'";
        return qryCSession(sql);
    }

    public List<ConfigSession> queryCSessionByHostUser(String host, String user) {
        String sql = "SELECT * FROM CSESSION WHERE HOST='" + host + "' AND USERname='" + user + "'";
        return qryCSession(sql);
    }

    /**
     * 查询列表.
     *
     * @param sql
     * @return
     */
    private List<ConfigSession> qryCSession(String sql) {
        List<ConfigSession> result = new ArrayList<>();
        try (Statement state = conn.createStatement(); ResultSet rs = state.executeQuery(sql);) {
            while (rs.next()) {
                String proc = rs.getString("Protocol");
                ProtocolEnum protocol = EnumUtils.getEnum(ProtocolEnum.class, proc);
                // 过滤协议不存在的数据
                if (Objects.nonNull(protocol)) {
                    ConfigSession confSession = new ConfigSession(rs.getString("Name"),
                        rs.getString("Host"), rs.getString("Port"), rs.getString("Username"),
                        protocol, rs.getString("Key"), Base64.decodeStr(rs.getString("Password")),
                        rs.getString("Session"));
                    result.add(confSession);
                } else {
                    log.error("{}协议不支持.", proc);
                }
            }

        } catch (SQLException e) {
            log.error("执行qryCSession异常.", e);
        }
        return result;
    }

    /**
     * 获取第一个匹配项.
     *
     * @param sql
     * @return
     */
    public ConfigSession getCSession(String sql) {
        List<ConfigSession> list = qryCSession(sql);
        return CollectionUtil.getFirst(list);

    }

    public ConfigSession queryCSessionByHostUserProtocol(String host, String user,
                                                         String protocol) {
        String sql = String.format(
            "SELECT *  FROM CSESSION WHERE HOST='%s' AND USERNAME='%s' AND PROTOCOL='%s'", host,
            user, protocol);
        // String sql = "SELECT * FROM CSESSION WHERE host='121.196.192.154' AND NAME='yds-sit' AND PROTOCOL='SSH2' ";

        return getCSession(sql);
    }

    public ConfigSession queryCSessionBySession(ConfigSession session) {
        String host = session.getHost();
        String user = session.getUser();
        String protocol = session.getProtocol().name();
        return queryCSessionByHostUserProtocol(host, user, protocol);
    }

    private boolean isCSessionExist(ConfigSession csession) {
        boolean isExist = false;
        String sql = "SELECT * FROM CSESSION WHERE host='" + csession.getHost() + "' AND port='"
                + csession.getPort() + "' AND username='" + csession.getUser() + "' AND protocol='"
                + csession.getProtocol() + "'";

        try (Statement state = conn.createStatement(); ResultSet rs = state.executeQuery(sql);) {
            if (rs.next()) {
                isExist = true;
            }
        } catch (SQLException e) {
            log.error("执行isCSessionExist异常.", e);
        }
        return isExist;
    }

    public void closeDB() {
        try {
            conn.close();
            log.debug("Committed transaction and closed connection");
        } catch (SQLException e) {
            log.error(ExceptionUtils.getStackTrace(e));
        }
    }
}
