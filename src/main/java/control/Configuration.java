package control;

import java.io.File;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;

import org.eclipse.swt.graphics.Rectangle;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.Setting;
import constants.ConfigConstant;
import constants.ConstantValue;
import enums.ProgramEnum;
import ui.MainFrame;
import utils.ReadXmlFile;

/**
 * 配置控制类.
 *
 * @author lvcn
 * @version $Id: Configuration.java, v 1.0 Jul 22, 2019 3:44:47 PM lvcn Exp $
 */
public class Configuration {

    private final Setting setting;

    private final List<HashMap<String, String>> menuConfigMapList;

    /**
     * 获取自定义菜单配置.
    */
    public List<HashMap<String, String>> getMenuConfig() {
        return this.menuConfigMapList;
    }

    /**
     * 构造函数.
     * 初始化配置信息.
     */
    public Configuration() {
        this.menuConfigMapList = ReadXmlFile.parse(new File(ConstantValue.MENU_CONFIG_FILE));
        this.setting = new Setting(new File(ConstantValue.APP_CONFIG_FILE),
                Charset.defaultCharset(), true);
        this.setting.autoLoad(true);
    }

    /**
     * 获取功能开关.
     * @param feature 功能名
     * @return true/false
     */
    public boolean getFeatureToggle(String feature) {
        return getFeature(feature, false);
    }

    /**
     * 获取初始化 等待时间.
     */
    public int getWaitForInitTime() {
        return getConfiguration(ConfigConstant.Configuration.WAIT_FOR_INIT_TIME, 0);
    }

    /**
     * 获取SmartPutty版本号配置.
     */
    public String getSmartPuttyVersion() {
        return getConfiguration(ConfigConstant.Configuration.VERSION, "");
    }

    /**
     * Utilities bar must be visible?
     *
     * @return
     */
    public boolean getUtilitiesBarVisible() {
        return getConfiguration(ConfigConstant.Configuration.VIEW_UTILITIES_BAR, false);
    }

    /**
     * Connection bar must be visible?
     *
     * @return
     */
    public boolean getConnectionBarVisible() {
        return getConfiguration(ConfigConstant.Configuration.VIEW_CONNECTION_BAR, false);
    }

    /**
     * Bottom Quick Bar be Visible ?
     *
     * @return
     */
    public boolean getBottomQuickBarVisible() {
        return getConfiguration(ConfigConstant.Configuration.VIEW_BOTTOM_QUICK_BAR, false);
    }

    /**
     * 获取应用程序执行路径.
     *
     * @param program 应用程序
     * @return 执行路径
     */
    public String getProgramPath(ProgramEnum program) {
        String value = program.getPath();
        // 1. 应用程序配置属性
        String property = program.getProperty();
        if (StrUtil.isNotBlank(property)) {
            // 2. 如果配置属性不为空, 从配置文件中获取配置项
            value = getProgram(property, program.getPath());
        }
        return value;
    }

    /**
     * Get dictionary baseUrl, I put dict.youdao.com as a chines-english dictionary. User can customize it as to his own dict url
     *
     * @return
     */
    public String getDictionaryBaseUrl() {
        return getConfiguration(ConfigConstant.Configuration.DICTIONARY,
                "http://dict.youdao.com/w/eng/");
    }

    /**
     * User can customize his username, in most case user may using his own username to login multiple linux, so provide a centralized username entry for user.
     *
     * @return
     */
    public String getDefaultPuttyUsername() {
        return getConfiguration(ConfigConstant.Configuration.DEFAULT_PUTTY_USERNAME, "");
    }

    /**
     * 获取数据库路径.
     */
    public String getDatabasePath() {
        return getConfiguration(ConfigConstant.Configuration.DATABASE_PATH, "config/ssh.csv");
    }

    /**
     * Customize win path base prefix when converting path from linux and windows.
     *
     * @return
     */
    public String getWinPathBaseDrive() {
        return getConfiguration(ConfigConstant.Configuration.WINDOWS_BASE_DRIVE, "C:/");
    }

    /**
     * Get welcome visible config.
     *
     * @return
     */
    public boolean getWelcomePageVisible() {
        return getConfiguration(ConfigConstant.Configuration.SHOW_WELCOME_PAGE, false);
    }

    /**
     * Get main window position and size.
     *
     * @return
     */
    public Rectangle getWindowPositionSize() {
        // Split comma-separated values by x, y, width, height:
        String windowPositionSize = getConfiguration(
                ConfigConstant.Configuration.WINDOW_POSITION_SIZE, "");

        int[] array = StrUtil.splitToInt(windowPositionSize, ",");

        // 配置不满4位, 根据屏幕宽高 重新设置
        if (array.length < 4) {
            array = new int[4];
            array[0] = ConstantValue.SCREEN_WIDTH / 6;
            array[1] = ConstantValue.SCREEN_HEIGHT / 6;
            array[2] = 2 * ConstantValue.SCREEN_WIDTH / 3;
            array[3] = 2 * ConstantValue.SCREEN_HEIGHT / 3;
        }

        return new Rectangle(array[0], array[1], array[2], array[3]);
    }

    /**
     * Get main window position and size in String format.
     *
     * 获取窗口坐标位置
     *
     * @return
     */
    public String getWindowPositionSizeString() {
        int x = MainFrame.SHELL.getBounds().x;
        int y = MainFrame.SHELL.getBounds().y;
        int width = MainFrame.SHELL.getBounds().width;
        int height = MainFrame.SHELL.getBounds().height;
        return StrUtil.join(",", x, y, width, height);
    }

    /**
     * 设置窗口坐标位置.
     */
    public void setWindowPositionSizeString() {
        String position = getWindowPositionSizeString();
        setConfiguration(ConfigConstant.Configuration.WINDOW_POSITION_SIZE, position);
    }

    /**
     * Set utilities bar visible status.
     *
     * @param visible
     */
    public void setUtilitiesBarVisible(String visible) {
        setConfiguration(ConfigConstant.Configuration.VIEW_UTILITIES_BAR, visible);
    }

    /**
     * Set connection bar visible status.
     *
     * @param visible
     */
    public void setConnectionBarVisible(String visible) {
        setConfiguration(ConfigConstant.Configuration.VIEW_CONNECTION_BAR, visible);
    }

    public void setBottomQuickBarVisible(String visible) {
        setConfiguration(ConfigConstant.Configuration.VIEW_BOTTOM_QUICK_BAR, visible);
    }

    /**
     * Set if "Welcome Page" should must be visible on program startup.
     *
     * @param visible
     */
    public void setWelcomePageVisible(String visible) {
        setConfiguration(ConfigConstant.Configuration.SHOW_WELCOME_PAGE, visible);
    }

    /**
     * 查询配置.
     */
    public <T> T getConfiguration(String key, T defaultValue) {
        return getByGroup(key, ConfigConstant.Group.CONFIGURATION, defaultValue);
    }

    /**
     * 设置configuration配置.
     */
    public void setConfiguration(String key, String value) {
        setting.setByGroup(key, ConfigConstant.Group.CONFIGURATION, value);
    }

    /**
     * 查询program配置.
     */
    public <T> T getProgram(String key, T defaultValue) {
        return getByGroup(key, ConfigConstant.Group.PROGRAM, defaultValue);
    }

    public void setProgram(String key, String value) {
        setting.setByGroup(key, ConfigConstant.Group.PROGRAM, value);
    }

    /**
     * 查询特性配置.
     */
    public <T> T getFeature(String key, T defaultValue) {
        return getByGroup(key, ConfigConstant.Group.FEATURE, defaultValue);
    }

    /**
     * 根据defaultValue类型 返回.
     */
    @SuppressWarnings("unchecked")
    public <T> T getByGroup(String key, String group, T defaultValue) {
        if (StrUtil.isBlank(key)) {
            return defaultValue;
        }

        if (null != defaultValue) {
            final Class<T> type = (Class<T>) defaultValue.getClass();
            String value = setting.getByGroup(key, group);
            return Convert.convertWithCheck(type, value, defaultValue, true);
        }

        return (T) setting.getByGroup(key, group);
    }

    /**
     * 保存配置文件.
     */
    public void saveSetting() {
        setting.store(new File(ConstantValue.APP_CONFIG_FILE));
    }

    /**
     * 关闭前 保存配置文件.
     */
    public void saveBeforeClose() {
        setWindowPositionSizeString();
        // 关闭前, 先关闭自动加载
        this.setting.autoLoad(false);
        saveSetting();
    }
}
