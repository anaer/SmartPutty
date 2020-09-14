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
    private boolean             existSessionTable;

    private DbManager() {
        try {
            String dbPath = MainFrame.configuration.getDatabasePath();
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
            log.debug("Connect to Database:{}", dbPath);
        } catch (Exception e) {
            log.error("初始化数据库连接异常.", e);
            MessageDialog.openError(null, "错误", "数据库连接异常.");
        }

        try (Statement state = conn.createStatement();) {
            String[] str = { "TABLE" };
            DatabaseMetaData meta = conn.getMetaData();
            ResultSet result = meta.getTables(null, null, null, str);
            // 检查是否存在CSESSION表
            while (result.next()) {
                if ("CSESSION".equals(result.getString("TABLE_NAME"))) {
                    existSessionTable = true;
                }
            }
            result.close();
            if (!existSessionTable) {
                String sql = "CREATE TABLE CSession(Name varchar(100), Host varchar(50), Port varchar(10), Username varchar(50), Protocol varchar(10), Key varchar(100), Password varchar(50), Session varchar(50), PRIMARY KEY (Host,Port,Username,Protocol))";
                state.execute(sql);
            }

        } catch (Exception e) {
            log.error("初始化表结构异常.", e);
        }
    }

    public static DbManager getDbManagerInstance() {
        if (manager == null) {
            manager = new DbManager();
        }
        return manager;
    }

    public void insertSession(ConfigSession configSession) {
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
        if (isSessionExist(configSession)) {
            log.info("session已存在, 先删除当前session.");
            deleteSession(configSession);
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
    public void deleteSession(ConfigSession csession) {
        if (!isSessionExist(csession)) {
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

    public List<ConfigSession> getAllSessions() {
        String sql = "SELECT *  FROM CSESSION order by name";
        return qrySession(sql);
    }

    public List<ConfigSession> querySessionByHost(String host) {
        String sql = "SELECT *  FROM CSESSION WHERE host='" + host + "'";
        return qrySession(sql);
    }

    public List<ConfigSession> querySessionByHostUser(String host, String user) {
        String sql = "SELECT * FROM CSESSION WHERE HOST='" + host + "' AND USERname='" + user + "'";
        return qrySession(sql);
    }

    /**
     * 查询列表.
     *
     * @param sql
     * @return
     */
    private List<ConfigSession> qrySession(String sql) {
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
    public ConfigSession getSession(String sql) {
        List<ConfigSession> list = qrySession(sql);
        return CollectionUtil.getFirst(list);

    }

    public ConfigSession querySessionByHostUserProtocol(String host, String user,
                                                         String protocol) {
        String sql = String.format( "SELECT *  FROM CSESSION WHERE HOST='%s' AND USERNAME='%s' AND PROTOCOL='%s'", host, user, protocol);

        return getSession(sql);
    }

    public ConfigSession querySessionByHostUserProtocol(String host, String port, String user,
                                                         String protocol) {
        String sql = String.format( "SELECT *  FROM CSESSION WHERE HOST='%s' AND PORT = '%s' AND USERNAME='%s' AND PROTOCOL='%s'", host, port, user, protocol);

        return getSession(sql);
    }
    

    public ConfigSession querySessionBySession(ConfigSession session) {
        String host = session.getHost();
        String user = session.getUser();
        String port = session.getPort();
        String protocol = session.getProtocol().name();
        return querySessionByHostUserProtocol(host, port, user, protocol);
    }

    private boolean isSessionExist(ConfigSession csession) {
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

    public void closeDb() {
        try {
            conn.close();
            log.debug("Committed transaction and closed connection");
        } catch (SQLException e) {
            log.error(ExceptionUtils.getStackTrace(e));
        }
    }
}
