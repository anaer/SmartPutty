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

    private final List<HashMap<String, String>> batchConfigListMap;

    /** 
     * 获取自定义菜单配置.
    */
    public List<HashMap<String, String>> getBatchConfig() {
        return this.batchConfigListMap;
    }

    /** 
     * 构造函数.
     * 初始化配置信息.
     */
    public Configuration() {
        this.batchConfigListMap = ReadXmlFile.parse(new File(ConstantValue.CONFIG_BATCH_FILE));
        this.setting = new Setting(new File(ConstantValue.APP_CONFIG_FILE), Charset.defaultCharset(), true);
        this.setting.autoLoad(true);
    }

    /**
     * 获取功能开关.
     * @param feature 功能名
     * @return true/false
     */
    public Boolean getFeatureToggle(String feature) {
        return getFeature(feature, false);
    }

    /**
     * 获取初始化 等待时间.
     */
    public int getWaitForInitTime() {
        return getConfiguration(ConfigConstant.WAIT_FOR_INIT_TIME, 0);
    }

    /**
     * 获取SmartPutty版本号配置.
     */
    public String getSmartPuttyVersion() {
        return getConfiguration(ConfigConstant.VERSION, "");
    }

    /**
     * Utilities bar must be visible?
     *
     * @return
     */
    public Boolean getUtilitiesBarVisible() {
        return getConfiguration(ConfigConstant.VIEW_UTILITIES_BAR, false);
    }

    /**
     * Connection bar must be visible?
     *
     * @return
     */
    public Boolean getConnectionBarVisible() {
        return getConfiguration(ConfigConstant.VIEW_CONNECTION_BAR, false);
    }

    /**
     * Bottom Quick Bar be Visible ?
     *
     * @return
     */
    public Boolean getBottomQuickBarVisible() {
        return getConfiguration(ConfigConstant.VIEW_BOTTOM_QUICK_BAR, false);
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
        if (StringUtils.isNotBlank(property)) {
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
        return getConfiguration(ConfigConstant.DICTIONARY, "http://dict.youdao.com/w/eng/");
    }

    /**
     * User can customize his username, in most case user may using his own username to login multiple linux, so provide a centralized username entry for user.
     *
     * @return
     */
    public String getDefaultPuttyUsername() {
        return getConfiguration(ConfigConstant.DEFAULT_PUTTY_USERNAME, "");
    }

    /**
     * 获取数据库路径.
     */
    public String getDatabasePath() {
        return getConfiguration(ConfigConstant.DATABASE_PATH, "config/ssh.csv");
    }

    /**
     * Customize win path base prefix when converting path from linux and windows.
     *
     * @return
     */
    public String getWinPathBaseDrive() {
        return getConfiguration(ConfigConstant.WINDOWS_BASE_DRIVE, "C:/");
    }

    /**
     * Get welcome visible config.
     *
     * @return
     */
    public Boolean getWelcomePageVisible() {
        return getConfiguration(ConfigConstant.SHOW_WELCOME_PAGE, false);
    }

    /**
     * Get main window position and size.
     *
     * @return
     */
    public Rectangle getWindowPositionSize() {
        // Split comma-separated values by x, y, width, height:
        String windowPositionSize = getConfiguration(ConfigConstant.WINDOW_POSITION_SIZE, "");

        int[] array = Arrays.stream(windowPositionSize.split(",")).mapToInt(str -> Convert.toInt(str, 0)).toArray();

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
        String x = String.valueOf(MainFrame.SHELL.getBounds().x);
        String y = String.valueOf(MainFrame.SHELL.getBounds().y);
        String width = String.valueOf(MainFrame.SHELL.getBounds().width);
        String height = String.valueOf(MainFrame.SHELL.getBounds().height);

        return Joiner.on(",").join(x, y, width, height);
    }

    /**
     * 设置窗口坐标位置.
     */
    public void setWindowPosisionSizeString() {
        String position = getWindowPositionSizeString();
        setConfiguratioin(ConfigConstant.WINDOW_POSITION_SIZE, position);
    }

    /**
     * Set utilities bar visible status.
     *
     * @param visible
     */
    public void setUtilitiesBarVisible(String visible) {
        setConfiguratioin(ConfigConstant.VIEW_UTILITIES_BAR, visible);
    }

    /**
     * Set connection bar visible status.
     *
     * @param visible
     */
    public void setConnectionBarVisible(String visible) {
        setConfiguratioin(ConfigConstant.VIEW_CONNECTION_BAR, visible);
    }

    public void setBottomQuickBarVisible(String visible) {
        setConfiguratioin(ConfigConstant.VIEW_BOTTOM_QUICK_BAR, visible);
    }

    /**
     * Set if "Welcome Page" should must be visible on program startup.
     *
     * @param visible
     */
    public void setWelcomePageVisible(String visible) {
        setConfiguratioin(ConfigConstant.SHOW_WELCOME_PAGE, visible);
    }

    /**
     * 查询配置.
     */
    public <T> T getConfiguration(String key, T defaultValue) {
        return getByGroup(key, ConfigConstant.GROUP_CONFIGURATION, defaultValue);
    }

    /**
     * 设置configuration配置.
     */
    public void setConfiguratioin(String key, String value) {
        setting.setByGroup(key, ConfigConstant.GROUP_CONFIGURATION, value);
    }

    /**
     * 查询program配置.
     */
    public <T> T getProgram(String key, T defaultValue) {
        return getByGroup(key, ConfigConstant.GROUP_PROGRAM, defaultValue);
    }

    public void setProgram(String key, String value) {
        setting.setByGroup(key, ConfigConstant.GROUP_PROGRAM, value);
    }

    /**
     * 查询特性配置.
     */
    public <T> T getFeature(String key, T defaultValue) {
        return getByGroup(key, ConfigConstant.GROUP_FEATURE, defaultValue);
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
        setWindowPosisionSizeString();
        // 关闭前, 先关闭自动加载
        this.setting.autoLoad(false);
        saveSetting();
    }
}
