package control;

import java.io.File;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.graphics.Rectangle;

import cn.hutool.setting.Setting;
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
    private static final String                 WAIT_FOR_INIT_TIME  = "WaitForInitTime";

    private static final String                 VIEW_UTILITIES_BAR  = "ViewUtilitiesBar";

    private static final String                 VIEW_CONNECTION_BAR = "ViewConnectionBar";

    private static final String VIEW_BOTTOM_QUICK_BAR = "ViewBottomQuickBar";


    private static final String GROUP_CONFIGURATION = "configuration";

    private static final String GROUP_FEATURE = "feature";

    private static final String GROUP_PROGRAM = "program";

    private static final String                 VERSION             = "Version";

    private final Setting setting;

    private final List<HashMap<String, String>> batchConfigListMap;

    /** Constructor: */
    public Configuration() {
        this.batchConfigListMap = ReadXmlFile.parse(new File(ConstantValue.CONFIG_BATCH_FILE));
        this.setting = new Setting(new File(ConstantValue.APP_CONFIG_FILE), Charset.defaultCharset(), true);
        this.setting.autoLoad(true);
    }

    public Boolean getFeatureToggle(String feature){
        return setting.getBool(feature, GROUP_FEATURE, false);
    }

    /** Get methods: */
    public List<HashMap<String, String>> getBatchConfig() {
        return this.batchConfigListMap;
    }

    public String getWaitForInitTime() {
        return setting.get(GROUP_CONFIGURATION, WAIT_FOR_INIT_TIME);
    }

    public String getSmartPuttyVersion() {
        return setting.get(GROUP_CONFIGURATION, VERSION);
    }

    /**
     * Utilities bar must be visible?
     *
     * @return
     */
    public Boolean getUtilitiesBarVisible() {
        return setting.getBool(VIEW_UTILITIES_BAR, GROUP_CONFIGURATION, false);
    }

    /**
     * Connection bar must be visible?
     *
     * @return
     */
    public Boolean getConnectionBarVisible() {
        return setting.getBool(VIEW_CONNECTION_BAR, GROUP_CONFIGURATION, false);
    }

    /**
     * Bottom Quick Bar be Visible ?
     *
     * @return
     */
    public Boolean getBottomQuickBarVisible() {
        return setting.getBool(VIEW_BOTTOM_QUICK_BAR, GROUP_CONFIGURATION, false);
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
            value = setting.get(GROUP_PROGRAM, property);
        }
        // 3. 如果配置值为空, 取默认的path
        return StringUtils.isEmpty(value) ? program.getPath() : value;
    }

    /**
     * Get dictionary baseUrl, I put dict.youdao.com as a chines-english dictionary. User can customize it as to his own dict url
     *
     * @return
     */
    public String getDictionaryBaseUrl() {
        return getProperty("Dictionary", "http://dict.youdao.com/w/eng/");
    }

    /**
     * User can customize his username, in most case user may using his own username to login multiple linux, so provide a centralized username entry for user.
     *
     * @return
     */
    public String getDefaultPuttyUsername() {
        return getProperty("DefaultPuttyUsername", "");
    }

    public String getDatabasePath() {
        return getProperty("DatabasePath", "config/sessiondb");
    }


    /**
     * Customize win path base prefix when converting path from linux and windows.
     *
     * @return
     */
    public String getWinPathBaseDrive() {
        return getProperty("WindowsBaseDrive", "C:/");
    }

    /**
     * Get welcome visible config.
     *
     * @return
     */
    public Boolean getWelcomePageVisible() {
        return setting.getBool("ShowWelcomePage", GROUP_CONFIGURATION, false);
    }

    /**
     * Get main window position and size.
     *
     * @return
     */
    public Rectangle getWindowPositionSize() {
        // Split comma-separated values by x, y, width, height:
        String[] array = getProperty("WindowPositionSize", "").split(",");

        // If there aren't enough pieces of information...
        if (array.length < 4) {
            array = new String[4];
            // Set default safety values:
            array[0] = String.valueOf(ConstantValue.SCREEN_WIDTH / 6);
            array[1] = String.valueOf(ConstantValue.SCREEN_HEIGHT / 6);
            array[2] = String.valueOf(2 * ConstantValue.SCREEN_WIDTH / 3);
            array[3] = String.valueOf(2 * ConstantValue.SCREEN_HEIGHT / 3);
        }

        return new Rectangle(Integer.parseInt(array[0]), Integer.parseInt(array[1]),
            Integer.parseInt(array[2]), Integer.parseInt(array[3]));
    }

    /**
     * Get main window position and size in String format.
     *
     * @return
     */
    public String getWindowPositionSizeString() {
        String x = String.valueOf(MainFrame.SHELL.getBounds().x);
        String y = String.valueOf(MainFrame.SHELL.getBounds().y);
        String width = String.valueOf(MainFrame.SHELL.getBounds().width);
        String height = String.valueOf(MainFrame.SHELL.getBounds().height);

        return x + "," + y + "," + width + "," + height;
    }

    /**
     * Set utilities bar visible status.
     *
     * @param visible
     */
    public void setUtilitiesBarVisible(String visible) {
        setting.set(GROUP_CONFIGURATION, VIEW_UTILITIES_BAR, visible);
    }

    /**
     * Set connection bar visible status.
     *
     * @param visible
     */
    public void setConnectionBarVisible(String visible) {
        setting.set(GROUP_CONFIGURATION, VIEW_CONNECTION_BAR, visible);
    }

    public void setBottomQuickBarVisible(String visible) {
        setting.set(GROUP_CONFIGURATION, VIEW_BOTTOM_QUICK_BAR, visible);
    }

    /**
     * Set if "Welcome Page" should must be visible on program startup.
     *
     * @param visible
     */
    public void setWelcomePageVisible(String visible) {
        setting.set(GROUP_CONFIGURATION, "ShowWelcomePage", visible);
    }

    public void setProperty(String key, String value) {
        setting.set(GROUP_CONFIGURATION, key, value);
    }

    public String getProperty(String key, String defaultValue) {
        return setting.getStr(key, GROUP_CONFIGURATION, defaultValue);
    }

    public void setProgramProperty(String key, String value) {
        setting.set(GROUP_PROGRAM, key, value);
    }

    public String getProgramProperty(String key, String defaultValue) {
        return setting.getStr(key, GROUP_PROGRAM, defaultValue);
    }

    public void saveSetting(){
        setting.store(setting.getSettingPath());
    }
}
