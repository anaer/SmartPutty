package constants;

import java.awt.Toolkit;
import java.nio.file.Paths;

/**
 * 常量配置.
 *
 * @author anaer
 * @version $Id: ConstantValue.java, v 1.0 Jul 22, 2019 3:44:21 PM anaer Exp $
 */
public class ConstantValue {
    private ConstantValue() {
    }

    /**
     * 应用标题.
     */
    public static final String MAIN_WINDOW_TITLE = "Smart Putty";

    /**
     * Putty安全提示.
     */
    public static final String PUTTY_SECURITY_ALERT = "PuTTY Security Alert";

    /**
     * 首页链接.
     */
    public static final String HOME_URL = Paths.get(System.getProperty("user.dir"), "doc", "index.htm").toString();

    /**
     * 默认协议.
     */
    public static final String DEFAULT_PROTOCOL = "ssh";

    /**
     *  Screen sizes:
     * 屏幕宽高.
     */
    public static final int SCREEN_HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().height;
    public static final int SCREEN_WIDTH = Toolkit.getDefaultToolkit().getScreenSize().width;

    /**
     * 配置文件.
     */
    public static final String CONFIG_FILE = Paths.get("config", "config.yaml").toString();

}
