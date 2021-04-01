package control;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.google.common.base.Joiner;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.graphics.Rectangle;

import cn.hutool.core.convert.Convert;
import cn.hutool.setting.Setting;
import constants.ConfigConstant;
import constants.ConstantValue;
import enums.ProgramEnum;
import ui.MainFrame;
import utils.ReadXmlFile;

/**
 * 配置类.
 *
 * @author lvcn
 * @version $Id: Configuration.java, v 1.0 Jul 22, 2019 3:44:47 PM lvcn Exp $
 */
public class Configuration {

    private final Setting                       setting;

    private final List<HashMap<String, String>> batchConfigListMap;

    /** Constructor: */
    public Configuration() {
        this.batchConfigListMap = ReadXmlFile.parse(new File(ConstantValue.CONFIG_BATCH_FILE));
        this.setting = new Setting(new File(ConstantValue.APP_CONFIG_FILE),
            Charset.defaultCharset(), true);
        this.setting.autoLoad(true);
    }

    /**
     * 获取功能开关.
     * @param feature 功能名
     * @return true/false
     */
    public Boolean getFeatureToggle(String feature) {
        return setting.getBool(feature, ConfigConstant.GROUP_FEATURE, false);
    }

    /** 
     * 获取自定义菜单配置.
    */
    public List<HashMap<String, String>> getBatchConfig() {
        return this.batchConfigListMap;
    }

    public String getWaitForInitTime() {
        return setting.get(ConfigConstant.GROUP_CONFIGURATION, ConfigConstant.WAIT_FOR_INIT_TIME);
    }

    public String getSmartPuttyVersion() {
        return setting.get(ConfigConstant.GROUP_CONFIGURATION, ConfigConstant.VERSION);
    }

    /**
     * Utilities bar must be visible?
     *
     * @return
     */
    public Boolean getUtilitiesBarVisible() {
        return setting.getBool(ConfigConstant.VIEW_UTILITIES_BAR,
            ConfigConstant.GROUP_CONFIGURATION, false);
    }

    /**
     * Connection bar must be visible?
     *
     * @return
     */
    public Boolean getConnectionBarVisible() {
        return setting.getBool(ConfigConstant.VIEW_CONNECTION_BAR,
            ConfigConstant.GROUP_CONFIGURATION, false);
    }

    /**
     * Bottom Quick Bar be Visible ?
     *
     * @return
     */
    public Boolean getBottomQuickBarVisible() {
        return setting.getBool(ConfigConstant.VIEW_BOTTOM_QUICK_BAR,
            ConfigConstant.GROUP_CONFIGURATION, false);
    }

    /**
     * 获取应用程序执行路径.
     *
     * @param program 应用程序
     * @return 执行路径
     */
    public String getProgramPath(ProgramEnum program) {
        String value = null;
        // 1. 应用程序配置属性
        String property = program.getProperty();
        if (StringUtils.isNotBlank(property)) {
            // 2. 如果配置属性不为空, 从配置文件中获取配置项
            value = setting.get(ConfigConstant.GROUP_PROGRAM, property);
        }
        // 3. 如果配置值为空, 取默认的path
        return StringUtils.defaultIfEmpty(value, program.getPath());
    }

    /**
     * Get dictionary baseUrl, I put dict.youdao.com as a chines-english dictionary. User can customize it as to his own dict url
     *
     * @return
     */
    public String getDictionaryBaseUrl() {
        return getProperty(ConfigConstant.DICTIONARY, "http://dict.youdao.com/w/eng/");
    }

    /**
     * User can customize his username, in most case user may using his own username to login multiple linux, so provide a centralized username entry for user.
     *
     * @return
     */
    public String getDefaultPuttyUsername() {
        return getProperty(ConfigConstant.DEFAULT_PUTTY_USERNAME, "");
    }

    public String getDatabasePath() {
        return getProperty(ConfigConstant.DATABASE_PATH, "config/sessiondb");
    }

    /**
     * Customize win path base prefix when converting path from linux and windows.
     *
     * @return
     */
    public String getWinPathBaseDrive() {
        return getProperty(ConfigConstant.WINDOWS_BASE_DRIVE, "C:/");
    }

    /**
     * Get welcome visible config.
     *
     * @return
     */
    public Boolean getWelcomePageVisible() {
        return setting.getBool(ConfigConstant.SHOW_WELCOME_PAGE, ConfigConstant.GROUP_CONFIGURATION,
            false);
    }

    /**
     * Get main window position and size.
     *
     * @return
     */
    public Rectangle getWindowPositionSize() {
        // Split comma-separated values by x, y, width, height:
        String windowPositionSize = getProperty(ConfigConstant.WINDOW_POSITION_SIZE, "");

        int[] array = Arrays.stream(windowPositionSize.split(","))
            .mapToInt(str -> Convert.toInt(str, 0)).toArray();

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
        String x = String.valueOf(MainFrame.SHELL.getBounds().x);
        String y = String.valueOf(MainFrame.SHELL.getBounds().y);
        String width = String.valueOf(MainFrame.SHELL.getBounds().width);
        String height = String.valueOf(MainFrame.SHELL.getBounds().height);

        return Joiner.on(",").join(x, y, width, height);
    }

    public void setWindowPosisionSizeString() {
        String position = getWindowPositionSizeString();
        setProperty(ConfigConstant.WINDOW_POSITION_SIZE, position);
    }

    /**
     * Set utilities bar visible status.
     *
     * @param visible
     */
    public void setUtilitiesBarVisible(String visible) {
        setting.setByGroup(ConfigConstant.VIEW_UTILITIES_BAR, ConfigConstant.GROUP_CONFIGURATION,
            visible);
    }

    /**
     * Set connection bar visible status.
     *
     * @param visible
     */
    public void setConnectionBarVisible(String visible) {
        setting.setByGroup(ConfigConstant.VIEW_CONNECTION_BAR, ConfigConstant.GROUP_CONFIGURATION,
            visible);
    }

    public void setBottomQuickBarVisible(String visible) {
        setting.setByGroup(ConfigConstant.VIEW_BOTTOM_QUICK_BAR, ConfigConstant.GROUP_CONFIGURATION,
            visible);
    }

    /**
     * Set if "Welcome Page" should must be visible on program startup.
     *
     * @param visible
     */
    public void setWelcomePageVisible(String visible) {
        setting.setByGroup(ConfigConstant.SHOW_WELCOME_PAGE, ConfigConstant.GROUP_CONFIGURATION,
            visible);
    }

    public void setProperty(String key, String value) {
        setting.setByGroup(key, ConfigConstant.GROUP_CONFIGURATION, value);
    }

    public String getProperty(String key, String defaultValue) {
        return setting.getStr(key, ConfigConstant.GROUP_CONFIGURATION, defaultValue);
    }

    public void setProgramProperty(String key, String value) {
        setting.setByGroup(key, ConfigConstant.GROUP_PROGRAM, value);
    }

    public String getProgramProperty(String key, String defaultValue) {
        return setting.getStr(key, ConfigConstant.GROUP_PROGRAM, defaultValue);
    }

    public void saveSetting() {
        setting.store(new File(ConstantValue.APP_CONFIG_FILE));
    }

    public void saveBeforeClose() {
        setWindowPosisionSizeString();
        // 关闭前, 先关闭自动加载
        this.setting.autoLoad(false);
        saveSetting();
    }
}
