package model;

import enums.ProtocolEnum;
import enums.PuttySessionEnum;

/**
 * Session variables to establish a communication with a SSH client.
 *
 * @author SS
 */
public class ConfigSession {
    private PuttySessionEnum configSessionType = PuttySessionEnum.SMART_PUTTY_SESSION;
    private String           name              = "";
    private String           host              = "";
    /**
     *  Can be a number or a device name (COM1, COM2, ...).
     */
    private String           port              = "";
    private String           user              = "";
    private ProtocolEnum     protocol;
    private String           file              = "";
    private String           password          = "";
    private String           puttySession      = "";

    public ConfigSession(String name, String host, String port, String user, String password,
                         String puttySession) {
        this.name = name;
        this.host = host;
        this.user = user;
        this.password = password;
        this.port = port;
        this.puttySession = puttySession;
        this.configSessionType = PuttySessionEnum.PURE_PUTTY_SESSION;
    }

    /**
     * SmartPutty Session constructor
     *
     * @param host
     * @param port
     * @param user
     * @param protocol
     * @param file
     * @param password
     */
    public ConfigSession(String name, String host, String port, String user, ProtocolEnum protocol,
                         String file, String password, String puttySession) {
        this.name = name;
        this.host = host;
        this.port = port;
        this.user = user;
        this.protocol = protocol;
        this.file = file;
        this.password = password;
        this.puttySession = puttySession;
        configSessionType = PuttySessionEnum.PURE_PUTTY_SESSION;
    }

    /**
     * SmartPutty Session constructor
     *
     * @param host
     * @param port
     * @param user
     * @param protocol
     * @param file
     * @param password
     */
    public ConfigSession(String name, String host, String port, String user, ProtocolEnum protocol,
                         String file, String password) {
        this.name = name;
        this.host = host;
        this.port = port;
        this.user = user;
        this.protocol = protocol;
        this.file = file;
        this.password = password;
        configSessionType = PuttySessionEnum.SMART_PUTTY_SESSION;
    }

    public PuttySessionEnum getConfigSessionType() {
        return configSessionType;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public ProtocolEnum getProtocol() {
        return protocol;
    }

    public void setProtocol(ProtocolEnum protocol) {
        this.protocol = protocol;
    }

    public String getKey() {
        return file;
    }

    public void setKey(String file) {
        this.file = file;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSession() {
        return puttySession;
    }

    public void setSession(String session) {
        this.puttySession = session;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return String.format(
            "ConfigSession [ConfigSessionType=%s, name=%s, host=%s, port=%s, user=%s, protocol=%s, file=%s, password=%s, puttySession=%s]",
            configSessionType, name, host, port, user, protocol, file, password, puttySession);
    }

}
