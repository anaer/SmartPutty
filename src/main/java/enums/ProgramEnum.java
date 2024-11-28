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
    PUTTY("Putty", "", "PuttyExecutable", "*tty.exe", "Putty (*tty.exe)"),

    /**
     * plink程序.
     */
    PLINK("Plink", "", "PlinkExecutable", "*link.exe", "Plink (*link.exe)"),

    /**
     * keygen程序.
     */
    KEYGEN("Key generator", "", "KeyGeneratorExecutable", "*ttygen.exe", "Key generator (*ttygen.exe)"),

    /**
     * mintty程序.
     */
    MINTTY("Mintty", "", "MinttyExecutable", "mintty.exe", "Mintty (mintty.exe)"),

    /**
     * winscp程序.
     */
    WINSCP("Winscp", "", "ScpExecutable", "winscp.exe", "scp(winscp.exe)");

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
