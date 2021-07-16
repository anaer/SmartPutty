package dao;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.text.csv.CsvReader;
import cn.hutool.core.text.csv.CsvUtil;
import cn.hutool.core.text.csv.CsvWriter;
import cn.hutool.core.util.StrUtil;
import control.Configuration;
import model.ConfigSession;

/**
 * 数据库管理.
 */
public class SessionManager {

    private static SessionManager manager;
    private List<ConfigSession> list;
    private String databasePath;

    private SessionManager() {
        Configuration configuration = new Configuration();
        databasePath = configuration.getDatabasePath();
        list = readCsv();
    }

    public static SessionManager getInstance() {
        if (manager == null) {
            manager = new SessionManager();
        }
        return manager;
    }

    /**
     * 读取csv文件.
     */
    private List<ConfigSession> readCsv() {
        List<ConfigSession> configList = null;
        if (FileUtil.isFile(databasePath)) {
            CsvReader reader = CsvUtil.getReader();
            configList = reader.read(ResourceUtil.getUtf8Reader(databasePath), ConfigSession.class);
        }

        if(Objects.isNull(configList)){
            configList = new ArrayList<>();
        }
        return configList;
    }

    /**
     * 保存csv文件.
     */
    private void saveCsv(List<ConfigSession> list) {
        CsvWriter writer = CsvUtil.getWriter(databasePath, Charset.defaultCharset());

        writer.writeLine( //
                "name", //
                "host", //
                "intranet", // 
                "port", //
                "user", //
                "protocol", //
                "key", //
                "password", //
                "session" //
        );
        for (ConfigSession session : list) {
            writer.writeLine(//
                    session.getName(), //
                    session.getHost(), //
                    session.getIntranet(), //
                    session.getPort(), //
                    session.getUser(), //
                    session.getProtocol(), //
                    session.getKey(), //
                    session.getPassword(), //
                    session.getSession() //
            );
        }
        writer.flush();
    }

    public void insertSession(ConfigSession configSession) {
        list.add(configSession);
        saveCsv(list);
    }

    /**
     * 删除会话.
     *
     * @param csession
     */
    public void deleteSession(ConfigSession csession) {
        list.remove(csession);
        saveCsv(list);
    }

    public List<ConfigSession> getAllSessions() {
        return list;
    }

    public List<ConfigSession> querySessionByHost(String host) {
        return list.stream().filter(p -> StrUtil.equals(p.getHost(), host)).collect(Collectors.toList());
    }

    public List<ConfigSession> querySessionByHostUser(String host, String user) {
        return list.stream().filter(p -> StrUtil.equals(p.getHost(), host) && StrUtil.equals(p.getUser(), user))
                .collect(Collectors.toList());
    }

    public ConfigSession querySessionByHostUserProtocol(String host, String user, String protocol) {
        return list.stream().filter(p -> StrUtil.equals(p.getHost(), host) && StrUtil.equals(p.getUser(), user)
                && StrUtil.equals(p.getProtocol(), protocol)).findFirst().orElse(null);
    }

    public ConfigSession querySessionByHostUserProtocol(String host, String port, String user, String protocol) {
        return list.stream()
                .filter(p -> StrUtil.equals(p.getHost(), host) && StrUtil.equals(p.getUser(), user)
                        && StrUtil.equals(p.getProtocol(), protocol) && StrUtil.equals(p.getPort(), port))
                .findFirst().orElse(null);
    }

    public ConfigSession querySessionBySession(ConfigSession session) {
        String host = session.getHost();
        String user = session.getUser();
        String port = session.getPort();
        String protocol = session.getProtocol();
        return querySessionByHostUserProtocol(host, port, user, protocol);
    }

}
