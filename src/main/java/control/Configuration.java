package control;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.InvalidPropertiesFormatException;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.graphics.Rectangle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static final String                 VERSION             = "Version";

    /** 日志. */
    private static final Logger                 logger              = LoggerFactory
        .getLogger(Configuration.class);

    private final Properties                    prop;
    private final Properties                    featureToggleProps;

    private final List<HashMap<String, String>> batchConfigListMap;

    /** Constructor: */
    public Configuration() {
        this.prop = new Properties();
        this.featureToggleProps = new Properties();
        loadConfiguration();
        loadFeatureToggle();
        this.batchConfigListMap = ReadXmlFile.parse(new File(ConstantValue.CONFIG_BATCH_FILE));
    }

    public void loadFeatureToggle() {
        try (FileInputStream fis = new FileInputStream(ConstantValue.CONFIG_FEATURE_TOGGLE_FILE)) {
            featureToggleProps.load(fis);
        } catch (IOException e) {
            logger.error("加载特性开关配置失败.", e);
        }
    }

    /** Save program configuration. */
    public void saveConfiguration() {
        try (FileOutputStream fos = new FileOutputStream(ConstantValue.CONFIG_FILE)) {
            prop.setProperty(WAIT_FOR_INIT_TIME, prop.getProperty(WAIT_FOR_INIT_TIME));
            // Main window viewable toolbars:
            prop.setProperty(VIEW_UTILITIES_BAR, String.valueOf(getUtilitiesBarVisible()));
            prop.setProperty(VIEW_CONNECTION_BAR, String.valueOf(getConnectionBarVisible()));
            // Putty and Plink paths:
            prop.setProperty("PuttyExecutable", getProgramPath(ProgramEnum.PUTTY));
            prop.setProperty("PlinkExecutable", getProgramPath(ProgramEnum.PLINK));
            prop.setProperty("KeyGeneratorExecutable", getProgramPath(ProgramEnum.KEYGEN));
            // Main windows position and size:
            prop.setProperty("WindowPositionSize", getWindowPositionSizeString());

            prop.storeToXML(fos, "SmartPutty configuration file");
        } catch (IOException e) {
            logger.error("保存应用程序配置失败.", e);
        }
    }

    /** Load program configuration. */
    private void loadConfiguration() {
        try (FileInputStream fis = new FileInputStream(ConstantValue.CONFIG_FILE)) {
            prop.loadFromXML(fis);
        } catch (InvalidPropertiesFormatException e) {
            logger.error("错误的配置文件格式.", e);
        } catch (IOException e) {
            logger.error("加载配置文件失败.", e);
        }

        // prop.list(System.out); //DEBUG
    }

    /** Get methods: */
    public List<HashMap<String, String>> getBatchConfig() {
        return this.batchConfigListMap;
    }

    public String getWaitForInitTime() {
        return (String) prop.get(WAIT_FOR_INIT_TIME);
    }

    public String getSmartPuttyVersion() {
        return (String) prop.get(VERSION);
    }

    /**
     * Utilities bar must be visible?
     *
     * @return
     */
    public Boolean getUtilitiesBarVisible() {
        String value = (String) prop.get(VIEW_UTILITIES_BAR);
        return StringUtils.isEmpty(value) || BooleanUtils.toBoolean(value);
    }

    /**
     * Connection bar must be visible?
     *
     * @return
     */
    public Boolean getConnectionBarVisible() {
        String value = (String) prop.get(VIEW_CONNECTION_BAR);
        return StringUtils.isEmpty(value) || BooleanUtils.toBoolean(value);
    }

    /**
     * Bottom Quick Bar be Visible ?
     *
     * @return
     */
    public Boolean getBottomQuickBarVisible() {
        String value = (String) prop.get("ViewBottomQuickBar");
        return StringUtils.isEmpty(value) || BooleanUtils.toBoolean(value);
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
            value = (String) prop.get(property);
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
        String value = (String) prop.get("Dictionary");
        return StringUtils.isEmpty(value) ? "http://dict.youdao.com/w/eng/" : value;
    }

    /**
     * User can customize his username, in most case user may using his own username to login multiple linux, so provide a centralized username entry for user.
     *
     * @return
     */
    public String getDefaultPuttyUsername() {
        String value = (String) prop.get("DefaultPuttyUsername");
        return StringUtils.isEmpty(value) ? "" : value;
    }

    /**
     * Get feature toggle config, we can config to enable/disable features by editing config/FeatureToggle.properties
     *
     * @return
     */
    public Properties getFeatureToggleProps() {
        return featureToggleProps;
    }

    /**
     * Customize win path base prefix when converting path from linux and windows.
     *
     * @return
     */
    public String getWinPathBaseDrive() {
        String value = (String) prop.get("WindowsBaseDrive");
        return StringUtils.isEmpty(value) ? "C:/" : value;
    }

    /**
     * Get welcome visible config.
     *
     * @return
     */
    public Boolean getWelcomePageVisible() {
        String value = (String) prop.get("ShowWelcomePage");
        return StringUtils.isEmpty(value) || BooleanUtils.toBoolean(value);
    }

    /**
     * Get main window position and size.
     *
     * @return
     */
    public Rectangle getWindowPositionSize() {
        // Split comma-separated values by x, y, width, height:
        String[] array = ((String) prop.get("WindowPositionSize")).split(",");

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
        prop.setProperty(VIEW_UTILITIES_BAR, visible);
    }

    /**
     * Set connection bar visible status.
     *
     * @param visible
     */
    public void setConnectionBarVisible(String visible) {
        prop.setProperty(VIEW_CONNECTION_BAR, visible);
    }

    public void setBottomQuickBarVisible(String visible) {
        prop.setProperty("ViewBottomQuickBar", visible);
    }

    /**
     * Set if "Welcome Page" should must be visible on program startup.
     *
     * @param visible
     */
    public void setWelcomePageVisible(String visible) {
        prop.setProperty("ShowWelcomePage", visible);
    }

    public void setProperty(String key, String value) {
        prop.setProperty(key, value);
    }

    public String getProperty(String key, String defaultValue) {
        String value = (String) prop.get(key);
        return StringUtils.isEmpty(value) ? defaultValue : value;
    }
}
