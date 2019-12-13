package enums;

import org.apache.commons.lang3.ArrayUtils;

/**
 * External programs used by "SmartPutty". Some of them can replaceable by another (Putty/KiTTY, Plink/Klink,...), but another not.
 * @author lvcn
 */
public enum ProgramEnum {
    /**
     * putty程序.
     */
    PUTTY("Putty", "", "PuttyExecutable", new String[] { "putty.exe" },
          new String[] { "Putty (putty.exe)" }),

    /**
     * mintty程序.
     */
    MINTTY("Mintty", "", "MinttyExecutable", new String[] { "mintty.exe" },
           new String[] { "Mintty (mintty.exe)" }),

    /**
     * plink程序.
     */
    PLINK("Plink", "", "PlinkExecutable", new String[] { "plink.exe" },
          new String[] { "Plink (plink.exe)" }),

    /**
     * keygen程序.
     */
    KEYGEN("Key generator", "", "KeyGeneratorExecutable", new String[] { "*.exe" },
           new String[] { "Key generator (*.exe)" }),

    /**
     * winscp程序.
     */
    WINSCP("Winscp", "", "ScpExecutable", new String[] { "*.exe" }, new String[] { "scp(*.exe)" }),

    /**
     * vpc程序.
     */
    VNC("vnc", ""),

    /**
     * notepad程序.
     */
    NOTEPAD("notepad", "notepad.exe"),

    /**
     * capture程序.
     */
    CAPTURE("capture", "snippingtool.exe"),

    /**
     * calculator程序.
     */
    CALCULATOR("calculator", "calc.exe"),

    /**
     * 远程桌面.
     */
    REMOTE_DESK("remote_desk", "mstsc.exe");

    /**
     * 程序名称.
     */
    private final String   name;

    /**
     * 默认路径.
     */
    private final String   path;

    /**
     * 如果需要保存到配置文件中, 需要配置配置项<br/>
     * 下面三个属性配套
     *
     */
    private final String   property;

    private final String[] filterExtensions;

    private final String[] filterNames;

    /**
     *  Constructor:
     * @param name
     * @param path
     */
    private ProgramEnum(String name, String path) {
        this.name = name;
        this.path = path;
        this.property = "";
        this.filterExtensions = ArrayUtils.EMPTY_STRING_ARRAY;
        this.filterNames = ArrayUtils.EMPTY_STRING_ARRAY;
    }

    private ProgramEnum(String name, String path, String property, String[] filterExtensions,
                        String[] filterNames) {
        this.name = name;
        this.path = path;
        this.property = property;
        this.filterExtensions = filterExtensions;
        this.filterNames = filterNames;
    }

    public String getName() {
        return name;
    }

    /**
     * Get path to execute.
     *
     * @return
     */
    public String getPath() {
        return path;
    }

    public String getProperty() {
        return property;
    }

    public String[] getFilterExtensions() {
        return filterExtensions;
    }

    public String[] getFilterNames() {
        return filterNames;
    }

}
