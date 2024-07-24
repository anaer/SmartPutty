package dao;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.text.csv.CsvReader;
import cn.hutool.core.text.csv.CsvUtil;
import cn.hutool.core.text.csv.CsvWriter;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
        CsvWriter writer = CsvUtil.getWriter(databasePath, CharsetUtil.CHARSET_UTF_8);

        writer.writeLine( //
                "name", //
                "host", //
                "intranet", //
                "port", //
                "user", //
                "protocol", //
                "key", //
                "password", //
                "session"//
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

    public int insertSession(ConfigSession session) {
        ConfigSession existSession = querySessionByHostUserProtocol(session.getHost(), session.getPort(), session.getUser(), session.getProtocol());
        if(Objects.nonNull(existSession)){
            list.remove(existSession);
        }
        list.add(session);
        saveCsv(list);

        return list.indexOf(session);
    }

    /**
     * 删除会话.
     *
     * @param session
     */
    public void deleteSession(ConfigSession session) {
        list.remove(session);
        saveCsv(list);
    }

    /**
     * 上移.
     * @param session
     */
    public int up(ConfigSession session) {
        int index = list.indexOf(session);
        int targetIndex = index;

        if (index > 0) {
            targetIndex = index - 1;
            ListUtil.swapTo(list, session, targetIndex);
            saveCsv(list);
        }
        return targetIndex;
    }

    /**
     * 下移.
     * @param session
     */
    public int down(ConfigSession session) {
        int index = list.indexOf(session);
        int targetIndex = index;

        if (index < list.size() - 1) {
            targetIndex = index + 1;
            ListUtil.swapTo(list, session, targetIndex);
            saveCsv(list);
        }

        return targetIndex;
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
