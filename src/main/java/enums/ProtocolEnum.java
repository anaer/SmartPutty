package enums;

import cn.hutool.core.util.StrUtil;

/**
 * Protocols allowed by "Putty" or "KiTTY".
 *
 * @author Carlos SS
 */
public enum ProtocolEnum {
    // Following order is also used on lists to be showed:
    SSH2("SSH2", "-ssh -2"), // 
    SSH("SSH", "-ssh -1"), //
    TELNET("Telnet", "-telnet"), //
    RLOGIN("Rlogin", "-rlogin"), //
    RAW("Raw", "-raw"), //
    SERIAL("Serial", "-serial"), //
    MINTTY("Mintty", "");

    /**
     *  Visible name:
     */
    private final String name;
    /**
     *  Command-line parameter to be passed to "Putty" or "KiTTY":
     */
    private final String parameter;

    /**
     *  Constructor:
     * @param name
     * @param parameter
     */
    private ProtocolEnum(String name, String parameter) {
        this.name = name;
        this.parameter = parameter;
    }

    /**
     * Get name to be showed in lists.
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Get parameter.
     *
     * @return
     */
    public String getParameter() {
        return parameter;
    }

    /**
     * 按name查枚举
     */
    public static ProtocolEnum find(String name) {
        for (ProtocolEnum e : ProtocolEnum.values()) {
            if (StrUtil.equals(e.getName(), name)) {
                return e;
            }
        }
        return null;
    }
}
