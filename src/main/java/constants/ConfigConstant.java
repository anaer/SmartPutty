package constants;

/**
 * 配置常量.
 */
public class ConfigConstant {
    private ConfigConstant() {
    }

    /**
     * 配置组 ----------------------------------------------------------------
     */

    public static class Group {
        private Group() {
        }

        public static final String CONFIGURATION = "configuration";

        public static final String FEATURE = "feature";

        public static final String PROGRAM = "program";
    }

    /**
     * configuration配置项-----------------------------------------------------
     */

    public static class Configuration {
        private Configuration() {
        }

        /**
         * 初始化等待时间.
         */
        public static final String WAIT_FOR_INIT_TIME = "WaitForInitTime";

        /**
         * 是否展示工具栏.
         */
        public static final String VIEW_UTILITIES_BAR = "ViewUtilitiesBar";

        /**
         * 是否展示连接栏.
         */
        public static final String VIEW_CONNECTION_BAR = "ViewConnectionBar";

        /**
         * 是否展示底部快捷工具栏.
         */
        public static final String VIEW_BOTTOM_QUICK_BAR = "ViewBottomQuickBar";

        /**
         * 版本号.
         */
        public static final String VERSION = "Version";

        /**
         * 字典.
         */
        public static final String DICTIONARY = "Dictionary";

        /**
         * 默认Putty用户名.
         */
        public static final String DEFAULT_PUTTY_USERNAME = "DefaultPuttyUsername";

        /**
         * 数据库路径.
         */
        public static final String DATABASE_PATH = "DatabasePath";

        /**
         * Windows系统盘路径.
         */
        public static final String WINDOWS_BASE_DRIVE = "WindowsBaseDrive";

        /**
         * 是否显示欢迎页.
         */
        public static final String SHOW_WELCOME_PAGE = "ShowWelcomePage";

        /**
         * 窗口坐标位置.
         */
        public static final String WINDOW_POSITION_SIZE = "WindowPositionSize";

        public static final String NOTE_POSITION_SIZE = "NotePositionSize";
    }

    /**
     * feature配置项.---------------------------------------------------------
     */
    public static class Feature {
        private Feature() {
        }

        public static final String VNC = "vnc";

        public static final String TRANSFER = "transfer";

        public static final String CALCULATOR = "calculator";

    }

    /**
     * program配置项.---------------------------------------------------------
     */
}
