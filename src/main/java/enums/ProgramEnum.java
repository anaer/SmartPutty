package enums;

import lombok.Getter;

/**
 * External programs used by "SmartPutty". Some of them can replaceable by another (Putty/KiTTY, Plink/Klink,...), but another not.
 * @author anaer
 */
@Getter
public enum ProgramEnum {
    /**
     * putty程序.
     */
    PUTTY("Putty", "", "PuttyExecutable", "putty.exe", "Putty (putty.exe)"),

    /**
     * mintty程序.
     */
    MINTTY("Mintty", "", "MinttyExecutable", "mintty.exe", "Mintty (mintty.exe)"),

    /**
     * plink程序.
     */
    PLINK("Plink", "", "PlinkExecutable", "plink.exe", "Plink (plink.exe)"),

    /**
     * keygen程序.
     */
    KEYGEN("Key generator", "", "KeyGeneratorExecutable", "puttygen.exe", "Key generator (puttygen.exe)"),

    /**
     * winscp程序.
     */
    WINSCP("Winscp", "", "ScpExecutable", "winscp.exe", "scp(winscp.exe)"),

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
    private final String name;

    /**
     * 默认路径.
     */
    private final String path;

    /**
     * 如果需要保存到配置文件中, 需要配置配置项<br/>
     * 下面三个属性配套
     *
     */
    private final String property;

    /**
     * 过滤扩展名.
     */
    private final String[] filterExtensions;

    /**
     * 过滤显示名称.
     */
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
        this.filterExtensions = new String[]{};
        this.filterNames = new String[]{};
    }

    private ProgramEnum(String name, String path, String property, String[] filterExtensions, String[] filterNames) {
        this.name = name;
        this.path = path;
        this.property = property;
        this.filterExtensions = filterExtensions;
        this.filterNames = filterNames;
    }

    private ProgramEnum(String name, String path, String property, String filterExtension, String filterName) {
        this.name = name;
        this.path = path;
        this.property = property;
        this.filterExtensions = new String[] { filterExtension };
        this.filterNames = new String[] { filterName };
    }

}
