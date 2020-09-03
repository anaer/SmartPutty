package constants;

import java.awt.Toolkit;
import java.nio.file.Paths;

/**
 * 常量配置.
 *
 * @author lvcn
 * @version $Id: ConstantValue.java, v 1.0 Jul 22, 2019 3:44:21 PM lvcn Exp $
 */
public class ConstantValue {
    private ConstantValue() {
    }

    /**
     * 应用标题.
     */
    public static final String MAIN_WINDOW_TITLE          = "Smart Putty";

    /**
     * 首页链接.
     */
    public static final String HOME_URL                   = Paths
        .get(System.getProperty("user.dir"), "doc", "index.htm").toString();

    /**
     * 默认协议.
     */
    public static final String DEFAULT_PROTOCOL           = "ssh";

    /**
     *  Screen sizes:
     */
    public static final int    SCREEN_HEIGHT              = Toolkit.getDefaultToolkit()
        .getScreenSize().height;
    public static final int    SCREEN_WIDTH               = Toolkit.getDefaultToolkit()
        .getScreenSize().width;

    /**
     * 自定义菜单配置.
     */
    public static final String CONFIG_BATCH_FILE          = Paths.get("config", "BatchConfig.xml")
        .toString();

    public static final String APP_CONFIG_FILE            = Paths.get("config", "app.setting")
        .toString();
}
