package model;

import enums.ProtocolEnum;
import enums.PuttySessionEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Session variables to establish a communication with a SSH client.
 *
 * @author SS
 */
@Getter
@Setter
@ToString
public class ConfigSession {
    private PuttySessionEnum configSessionType = PuttySessionEnum.SMART_PUTTY_SESSION;
    private String           name              = "";
    private String           host              = "";

    private String           intranet          = "";
    /**
     *  Can be a number or a device name (COM1, COM2, ...).
     */
    private String           port              = "";
    private String           user              = "";
    private ProtocolEnum     protocol;
    private String           key               = "";
    private String           password          = "";
    private String           session           = "";

    public ConfigSession(String name, String host, String intranet, String port, String user,
                         String password, String session) {
        this.name = name;
        this.host = host;
        this.intranet = intranet;
        this.user = user;
        this.password = password;
        this.port = port;
        this.session = session;
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
    public ConfigSession(String name, String host, String intranet, String port, String user,
                         ProtocolEnum protocol, String key, String password, String session) {
        this.name = name;
        this.host = host;
        this.intranet = intranet;
        this.port = port;
        this.user = user;
        this.protocol = protocol;
        this.key = key;
        this.password = password;
        this.session = session;
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
    public ConfigSession(String name, String host, String intranet, String port, String user,
                         ProtocolEnum protocol, String key, String password) {
        this.name = name;
        this.host = host;
        this.intranet = intranet;
        this.port = port;
        this.user = user;
        this.protocol = protocol;
        this.key = key;
        this.password = password;
        configSessionType = PuttySessionEnum.SMART_PUTTY_SESSION;
    }

}
