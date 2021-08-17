package ui;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabFolder2Listener;
import org.eclipse.swt.custom.CTabFolderEvent;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import cn.hutool.core.swing.clipboard.ClipboardUtil;
import cn.hutool.core.util.RuntimeUtil;
import cn.hutool.core.util.StrUtil;
import constants.ButtonImage;
import constants.ConfigConstant;
import constants.ConstantValue;
import constants.FieldConstants;
import constants.MessageConstants;
import control.Configuration;
import control.InvokeProgram;
import dao.SessionManager;
import dialog.NewSessionDialog;
import dialog.OpenSessionDialog;
import dialog.ProgramsLocationsDialog;
import enums.ProgramEnum;
import listener.DragListener;
import lombok.extern.slf4j.Slf4j;
import model.ConfigSession;
import utils.RegistryUtils;
import widgets.BorderData;
import widgets.BorderLayout;

/**
 *
 * @author lvcn
 * @date 2018/10/24
 */
@Slf4j
public class MainFrame
        implements SelectionListener, CTabFolder2Listener, MouseListener, ShellListener {

    /**
     * windows 路径分隔符.
     */
    private static final String WIN_PATH_DELIMITED = "\\";
    public static final Display display = new Display();
    public static final Shell SHELL = new Shell(display);
    public static final Configuration configuration = new Configuration();
    private MenuItem openItem;
    private MenuItem newItem;
    private MenuItem captureItem;
    private MenuItem remoteDesktopItem;
    private MenuItem exitItem;
    private MenuItem aboutItem;
    private MenuItem welcomeMenuItem;
    private MenuItem copyTabNamePopItem;
    private MenuItem reloadPopItem;
    private MenuItem clonePopItem;
    private MenuItem transferPopItem;
    private MenuItem scpMenuItem;
    private MenuItem ftpMenuItem;
    private MenuItem sftpMenuItem;
    private MenuItem vncPopItem;
    private MenuItem openPuttyItem;
    private MenuItem configProgramsLocationsItem;
    private MenuItem utilitiesBarMenuItem;
    private MenuItem connectionBarMenuItem;
    private MenuItem bottomQuickBarMenuItem;

    /**
     * 重新加载所有标签.
     */
    private MenuItem reloadAllItem;

    /**
     * 关闭其他标签.
     */
    private MenuItem closeOtherTabsItem;

    /**
     * 关闭所有标签.
     */
    private MenuItem closeAllTabsItem;

    private MenuItem tabNextItem;

    /**
     * 关闭所有putty进程.
     * */
    private MenuItem killAllPuttyItem;

    private Menu popupMenu;
    private ToolItem itemNew;
    private ToolItem itemOpen;
    private ToolItem itemRemoteDesk;
    private ToolItem itemCapture;
    private ToolItem itemCalculator;
    private ToolItem itemVnc;
    private ToolItem itemNotePad;
    private ToolItem itemKenGen;
    private ToolItem itemHelp;
    private CTabFolder folder;
    private CTabItem welcomeItem;
    private CTabItem dictItem;
    private ToolBar utilitiesToolbar;
    private Group connectGroup;
    private Group quickBottomGroup;

    /** Connect bar components. */
    private Button connectButton;
    private Text hostItem;
    private Text portItem;
    private Text usernameItem;
    private Text passwordItem;
    private Combo sessionCombo;

    /** Bottom util bar components. */
    private Text pathItem;
    private Text dictText;
    private Button win2UnixButton;
    private Button unix2WinButton;
    private Button openPathButton;
    private Button dictButton;

    public MainFrame(ProgressBar bar) {
        bar.setSelection(1);
        RegistryUtils.createPuttyKeys();

        // Load configuration:
        bar.setSelection(2);
        loadConfiguration();

        SHELL.setLayout(new BorderLayout());
        SHELL.setImage(ButtonImage.MAIN_IMAGE);
        SHELL.setText(ConstantValue.MAIN_WINDOW_TITLE + " [" + configuration.getSmartPuttyVersion()
                + "]");
        SHELL.setBounds(configuration.getWindowPositionSize());
        SHELL.addShellListener(this);

        bar.setSelection(3);
        // Main menu:
        createMainMenu(SHELL);
        bar.setSelection(4);

        // Create upper connection toolbar:
        createConnectionBar(SHELL);

        // Create upper utilities toolbar:
        createUtilitiesToolbar(SHELL);

        // Create bottom utilities toolbar:
        createBottomUtilitiesToolBar(SHELL);

        // Create lower tabs zone:
        createTabs(SHELL);
        // Right button menu
        createTabPopupMenu(SHELL);

        // Show/Hide toolbars based on configuration file values:
        setVisibleComponents();
        if (configuration.getWelcomePageVisible()) {
            showWelcomeTab(ConstantValue.HOME_URL);
        }
        applyFeatureToggle();
        bar.setSelection(5);
        SHELL.open();

        SessionManager.getInstance();
    }

    /** Main menu. */
    private void createMainMenu(Shell shell) {
        Menu menu = new Menu(shell, SWT.BAR);
        shell.setMenuBar(menu);

        // Menu: File
        MenuItem file = new MenuItem(menu, SWT.CASCADE);
        file.setText("File");
        Menu fileMenu = new Menu(shell, SWT.DROP_DOWN);
        file.setMenu(fileMenu);

        newItem = new MenuItem(fileMenu, SWT.PUSH);
        newItem.setText("New\tCtrl+N");
        newItem.setImage(ButtonImage.NEW_IMAGE);
        newItem.setAccelerator(SWT.CTRL + 'N');
        newItem.addSelectionListener(this);

        openItem = new MenuItem(fileMenu, SWT.PUSH);
        openItem.setText("Open\tCtrl+O");
        openItem.setImage(ButtonImage.OPEN_IMAGE);
        openItem.setAccelerator(SWT.CTRL + 'O');
        openItem.addSelectionListener(this);

        tabNextItem = new MenuItem(fileMenu, SWT.PUSH);
        tabNextItem.setText("Next Tab\tAlt+#");
        tabNextItem.setImage(ButtonImage.DICT_IMAGE);
        tabNextItem.setAccelerator(SWT.ALT + SWT.SHIFT + '3');
        tabNextItem.addSelectionListener(this);

        killAllPuttyItem = new MenuItem(fileMenu, SWT.PUSH);
        killAllPuttyItem.setText("Kill All Putty");
        killAllPuttyItem.setImage(ButtonImage.RELOAD_IMAGE);
        killAllPuttyItem.addSelectionListener(this);

        // Separator:
        new MenuItem(fileMenu, SWT.SEPARATOR);

        exitItem = new MenuItem(fileMenu, SWT.PUSH);
        exitItem.setText("Exit\tCtrl+X");
        exitItem.setImage(null);
        exitItem.setAccelerator(SWT.CTRL + 'X');
        exitItem.addSelectionListener(this);

        // Menu: View
        MenuItem view = new MenuItem(menu, SWT.CASCADE);
        view.setText("View");
        Menu viewMenu = new Menu(shell, SWT.DROP_DOWN);
        view.setMenu(viewMenu);

        utilitiesBarMenuItem = new MenuItem(viewMenu, SWT.CHECK);
        utilitiesBarMenuItem.setText("Utilities bar");
        utilitiesBarMenuItem.setSelection(true);
        utilitiesBarMenuItem.addSelectionListener(this);

        connectionBarMenuItem = new MenuItem(viewMenu, SWT.CHECK);
        connectionBarMenuItem.setText("Connection bar");
        connectionBarMenuItem.setSelection(true);
        connectionBarMenuItem.addSelectionListener(this);

        bottomQuickBarMenuItem = new MenuItem(viewMenu, SWT.CHECK);
        bottomQuickBarMenuItem.setText("Bottom Quick bar");
        bottomQuickBarMenuItem.setSelection(true);
        bottomQuickBarMenuItem.addSelectionListener(this);

        // Menu: Options
        MenuItem configurationMenuItem = new MenuItem(menu, SWT.CASCADE);
        configurationMenuItem.setText("Configuration");
        Menu optionsMenu = new Menu(shell, SWT.DROP_DOWN);
        configurationMenuItem.setMenu(optionsMenu);

        configProgramsLocationsItem = new MenuItem(optionsMenu, SWT.PUSH);
        configProgramsLocationsItem.setText("Programs locations");
        configProgramsLocationsItem.setImage(ButtonImage.REMOTE_DESK_IMAGE);
        configProgramsLocationsItem.setAccelerator(SWT.CTRL + 'R');

        configProgramsLocationsItem.addSelectionListener(this);

        // Menu: Application
        MenuItem application = new MenuItem(menu, SWT.CASCADE);
        application.setText("Application");
        Menu applicationMenu = new Menu(shell, SWT.DROP_DOWN);
        application.setMenu(applicationMenu);
        List<HashMap<String, String>> listMenuItems = configuration.getMenuConfig();
        for (HashMap<String, String> menuHashMap : listMenuItems) {
            String type = menuHashMap.get("type");
            if (type == null || "separator".equals(type)) {
                new MenuItem(applicationMenu, SWT.SEPARATOR);
                continue;
            }
            String path = StrUtil.blankToDefault(menuHashMap.get("path"), "N/A");
            String argument = StrUtil.trimToEmpty(menuHashMap.get(FieldConstants.ARGUMENT));
            String description = StrUtil.blankToDefault(menuHashMap.get("description"), "N/A");
            MenuItem menuItem = new MenuItem(applicationMenu, SWT.PUSH);
            menuItem.setText(description);
            menuItem.setData("path", path);
            menuItem.setData(FieldConstants.ARGUMENT, argument);
            menuItem.setData("description", description);
            menuItem.setData("type", "dynamicApplication");
            menuItem.addSelectionListener(this);
        }

        // Menu: Help
        MenuItem help = new MenuItem(menu, SWT.CASCADE);
        help.setText("Help");
        Menu helpMenu = new Menu(shell, SWT.DROP_DOWN);
        help.setMenu(helpMenu);

        welcomeMenuItem = new MenuItem(helpMenu, SWT.PUSH);
        welcomeMenuItem.setText("Welcome Page");
        welcomeMenuItem.addSelectionListener(this);

        aboutItem = new MenuItem(helpMenu, SWT.PUSH);
        aboutItem.setText("About");
        aboutItem.addSelectionListener(this);
    }

    /** Create bottom connection bar. */
    private void createConnectionBar(Shell shell) {
        connectGroup = new Group(shell, SWT.NONE);

        RowLayout layout = new RowLayout();
        layout.marginTop = 3;
        layout.marginBottom = 3;
        layout.marginLeft = 1;
        layout.marginRight = 1;
        layout.spacing = 5;
        layout.wrap = false;
        layout.center = true;

        connectGroup.setLayout(layout);
        connectGroup.setLayoutData(new BorderData(SWT.TOP));
        connectGroup.setText("Quick Connect");

        // Host:
        new Label(connectGroup, SWT.RIGHT).setText("Host");
        hostItem = new Text(connectGroup, SWT.BORDER);
        hostItem.setText("");
        hostItem.setLayoutData(new RowData(120, 20));

        // Port:
        new Label(connectGroup, SWT.RIGHT).setText("Port");
        portItem = new Text(connectGroup, SWT.BORDER);
        portItem.setText("22");
        portItem.setLayoutData(new RowData(20, 20));

        // Username:
        new Label(connectGroup, SWT.RIGHT).setText("Username");
        usernameItem = new Text(connectGroup, SWT.BORDER);
        usernameItem.setText(configuration.getDefaultPuttyUsername());
        usernameItem.setLayoutData(new RowData(100, 20));

        // Password
        new Label(connectGroup, SWT.RIGHT).setText("Password");
        passwordItem = new Text(connectGroup, SWT.PASSWORD | SWT.BORDER);
        passwordItem.setLayoutData(new RowData(80, 20));

        // Session:
        new Label(connectGroup, SWT.RIGHT).setText(FieldConstants.SESSION);
        sessionCombo = new Combo(connectGroup, SWT.READ_ONLY);
        sessionCombo.setLayoutData(new RowData());
        sessionCombo.setToolTipText("Session to use");
        // Empty entry to use none.
        sessionCombo.add("");
        // Get all "Putty" sessions:
        List<String> sessions = RegistryUtils.getAllPuttySessions();
        for (String session : sessions) {
            sessionCombo.add(session);
        }

        // Connect button:
        connectButton = new Button(connectGroup, SWT.PUSH);
        connectButton.setText("Connect");
        connectButton.setImage(ButtonImage.PUTTY_IMAGE);
        connectButton.setLayoutData(new RowData());
        connectButton.setToolTipText("Connect to host");
        connectButton.addSelectionListener(this);
        connectGroup.pack();
    }

    private void createBottomUtilitiesToolBar(Shell shell) {
        quickBottomGroup = new Group(shell, SWT.BAR);
        RowLayout layout1 = new RowLayout();
        layout1.marginTop = 3;
        layout1.marginBottom = 3;
        layout1.marginLeft = 1;
        layout1.marginRight = 1;
        layout1.spacing = 5;
        layout1.wrap = false;
        layout1.center = true;
        quickBottomGroup.setLayout(layout1);
        quickBottomGroup.setLayoutData(new BorderData(SWT.BOTTOM));
        // Path:
        new Label(quickBottomGroup, SWT.RIGHT).setText("Path");
        pathItem = new Text(quickBottomGroup, SWT.BORDER);
        pathItem.setText("/");
        pathItem.setLayoutData(new RowData(250, 20));

        win2UnixButton = new Button(quickBottomGroup, SWT.PUSH);
        win2UnixButton.setText("->Linux");
        win2UnixButton.setImage(ButtonImage.LINUX_IMAGE);
        win2UnixButton.setToolTipText("Convert Windows Path to Linux");
        win2UnixButton.setLayoutData(new RowData());
        win2UnixButton.addSelectionListener(this);

        unix2WinButton = new Button(quickBottomGroup, SWT.PUSH);
        unix2WinButton.setText("->Windows");
        unix2WinButton.setToolTipText("Convert Linux path to Windows");
        unix2WinButton.setImage(ButtonImage.WINDOWS_IMAGE);
        unix2WinButton.setLayoutData(new RowData());
        unix2WinButton.addSelectionListener(this);

        openPathButton = new Button(quickBottomGroup, SWT.PUSH);
        openPathButton.setText("Open");
        openPathButton.setImage(ButtonImage.FOLDER_IMAGE);
        openPathButton.setToolTipText("Open directory/file");
        openPathButton.addSelectionListener(this);

        // Dictionary
        new Label(quickBottomGroup, SWT.RIGHT).setText("Dictionary");
        dictText = new Text(quickBottomGroup, SWT.BORDER);
        dictText.setLayoutData(new RowData(100, 20));
        dictText.addListener(SWT.Traverse, event -> {
            if (event.detail == SWT.TRAVERSE_RETURN) {
                String keyword = dictText.getText().trim();
                openDictTab(keyword);
            }
        });

        dictButton = new Button(quickBottomGroup, SWT.PUSH);
        dictButton.setText("Search");
        dictButton.setToolTipText("Search Keyword in Dictionary");
        dictButton.setImage(ButtonImage.DICT_IMAGE);
        dictButton.addSelectionListener(this);
        quickBottomGroup.pack();
    }

    /** Create utilities toolbar. */
    private void createUtilitiesToolbar(Shell shell) {
        utilitiesToolbar = new ToolBar(shell, SWT.VERTICAL);
        utilitiesToolbar.setLayoutData(new BorderData(SWT.LEFT));

        itemNew = new ToolItem(utilitiesToolbar, SWT.PUSH);
        itemNew.setText("New  ");
        itemNew.setToolTipText("create a new session");
        itemNew.setImage(ButtonImage.NEW_IMAGE);
        itemNew.addSelectionListener(this);

        itemOpen = new ToolItem(utilitiesToolbar, SWT.PUSH);
        itemOpen.setText("Open  ");
        itemOpen.setToolTipText("open existing sessions");
        itemOpen.setImage(ButtonImage.OPEN_IMAGE);
        itemOpen.addSelectionListener(this);

        itemRemoteDesk = new ToolItem(utilitiesToolbar, SWT.PUSH);
        itemRemoteDesk.setText("RemoteDesk");
        itemRemoteDesk.setToolTipText("open system remote desk tool");
        itemRemoteDesk.setImage(ButtonImage.REMOTE_DESK_IMAGE);
        itemRemoteDesk.addSelectionListener(this);

        // 工具栏 隐藏Capture, 直接调用会报错
        itemCapture = new ToolItem(utilitiesToolbar, SWT.PUSH);
        itemCapture.setText("Capture");
        itemCapture.setToolTipText("Open FastStone Capture");
        itemCapture.setImage(ButtonImage.CAPTURE_IMAGE);
        itemCapture.addSelectionListener(this);

        itemCalculator = new ToolItem(utilitiesToolbar, SWT.PUSH);
        itemCalculator.setText("Calculator");
        itemCalculator.setToolTipText("open microsoft calculator");
        itemCalculator.setImage(ButtonImage.CALC_IMAGE);
        itemCalculator.addSelectionListener(this);

        itemVnc = new ToolItem(utilitiesToolbar, SWT.PUSH);
        itemVnc.setText("VNC");
        itemVnc.setToolTipText("open VNC");
        itemVnc.setImage(ButtonImage.VPC_IMAGE);
        itemVnc.addSelectionListener(this);

        itemNotePad = new ToolItem(utilitiesToolbar, SWT.PUSH);
        itemNotePad.setText("NotePad");
        itemNotePad.setToolTipText("open NotePad");
        itemNotePad.setImage(ButtonImage.NOTEPAD_IMAGE);
        itemNotePad.addSelectionListener(this);

        itemKenGen = new ToolItem(utilitiesToolbar, SWT.PUSH);
        itemKenGen.setText("KenGen");
        itemKenGen.setToolTipText("Convert SSH Key");
        itemKenGen.setImage(ButtonImage.KEY_IMAGE);
        itemKenGen.addSelectionListener(this);

        itemHelp = new ToolItem(utilitiesToolbar, SWT.PUSH);
        itemHelp.setText("Help");
        itemHelp.setToolTipText("help document");
        itemHelp.setImage(ButtonImage.HELP_IMAGE);
        itemHelp.addSelectionListener(this);

        utilitiesToolbar.pack();
    }

    /** Create tabs zone. */
    private void createTabs(Shell shell) {
        folder = new CTabFolder(shell, SWT.BORDER);

        folder.setLayoutData(new BorderData());
        folder.setSimple(false);
        folder.setUnselectedCloseVisible(false);
        folder.addCTabFolder2Listener(this);
        folder.addMouseListener(this);
        folder.addSelectionListener(this);

        Listener listener = new DragListener(display, folder);

        folder.addListener(SWT.DragDetect, listener);
        folder.addListener(SWT.MouseUp, listener);
        folder.addListener(SWT.MouseMove, listener);
        folder.addListener(SWT.MouseExit, listener);
        folder.addListener(SWT.MouseEnter, listener);
        folder.addListener(SWT.MouseDoubleClick, e -> {
            CTabItem item = folder.getSelection();
            closeTab(item);
        });
    }

    /**
     * 关闭标签.
     */
    private void closeTab(CTabItem item) {
        if (Objects.nonNull(item)) {
            if (item.getData("hwnd") != null) {
                int hwnd = Integer.parseInt(String.valueOf(item.getData("hwnd")));
                InvokeProgram.killProcess(hwnd);
            }
            item.dispose();
        }
    }

    /** Tab popup menu. */
    private void createTabPopupMenu(Shell shell) {
        popupMenu = new Menu(shell, SWT.POP_UP);

        reloadPopItem = new MenuItem(popupMenu, SWT.PUSH);
        reloadPopItem.setText("reload session");
        reloadPopItem.setImage(ButtonImage.RELOAD_IMAGE);
        reloadPopItem.addSelectionListener(this);

        reloadAllItem = new MenuItem(popupMenu, SWT.PUSH);
        reloadAllItem.setText("reload all session");
        reloadAllItem.setImage(ButtonImage.RELOAD_IMAGE);
        reloadAllItem.addSelectionListener(this);

        clonePopItem = new MenuItem(popupMenu, SWT.PUSH);
        clonePopItem.setText("clone session");
        clonePopItem.setImage(ButtonImage.CLONE_IMAGE);
        clonePopItem.addSelectionListener(this);

        /**
         * 文件传输菜单 start
         */
        transferPopItem = new MenuItem(popupMenu, SWT.CASCADE);
        transferPopItem.setText("transfer file");
        transferPopItem.setImage(ButtonImage.TRANSFER_IMAGE);

        Menu subMenu = new Menu(popupMenu);
        ftpMenuItem = new MenuItem(subMenu, SWT.PUSH);
        ftpMenuItem.setText("FTP");
        ftpMenuItem.addSelectionListener(this);

        scpMenuItem = new MenuItem(subMenu, SWT.PUSH);
        scpMenuItem.setText("SCP");
        scpMenuItem.addSelectionListener(this);

        sftpMenuItem = new MenuItem(subMenu, SWT.PUSH);
        sftpMenuItem.setText("SFTP");
        sftpMenuItem.addSelectionListener(this);

        transferPopItem.setMenu(subMenu);
        // 文件传输菜单 end

        vncPopItem = new MenuItem(popupMenu, SWT.PUSH);
        vncPopItem.setText("VNC");
        vncPopItem.setImage(ButtonImage.VPC_IMAGE);
        vncPopItem.addSelectionListener(this);

        // 拷贝标签名
        copyTabNamePopItem = new MenuItem(popupMenu, SWT.PUSH);
        copyTabNamePopItem.setText("copy Tab Name");
        copyTabNamePopItem.setImage(ButtonImage.EDIT_IMAGE);
        copyTabNamePopItem.addSelectionListener(this);

        closeOtherTabsItem = new MenuItem(popupMenu, SWT.PUSH);
        closeOtherTabsItem.setText("close other tabs");
        closeOtherTabsItem.setImage(ButtonImage.TRASH_IMAGE);
        closeOtherTabsItem.addSelectionListener(this);

        closeAllTabsItem = new MenuItem(popupMenu, SWT.PUSH);
        closeAllTabsItem.setText("close all tabs");
        closeAllTabsItem.setImage(ButtonImage.TRASH_FULL_IMAGE);
        closeAllTabsItem.addSelectionListener(this);
    }

    private void loadConfiguration() {
        InvokeProgram.killPuttyWarningsAndErrs();
    }

    private void showWelcomeTab(String url) {
        if (welcomeItem == null || welcomeItem.isDisposed()) {
            welcomeItem = new CTabItem(folder, SWT.CLOSE);
            Browser browser = new Browser(folder, SWT.NONE);
            browser.setUrl(url);
            welcomeItem.setControl(browser);
            folder.setSelection(welcomeItem);
            welcomeItem.setText("Welcome Page");
        } else {
            folder.setSelection(welcomeItem);
        }
    }

    /**
     * 打开字典标签.
     *
     * @param keyword
     */
    private void openDictTab(String keyword) {
        if (dictItem == null || dictItem.isDisposed()) {
            dictItem = new CTabItem(folder, SWT.CLOSE);
            dictItem.setImage(ButtonImage.DICT_IMAGE);
            Browser browser = new Browser(folder, SWT.NONE);
            browser.setUrl(configuration.getDictionaryBaseUrl() + keyword);
            dictItem.setControl(browser);
            folder.setSelection(dictItem);
            dictItem.setText("Dictionary");
        } else {
            folder.setSelection(dictItem);
            ((Browser) dictItem.getControl())
                    .setUrl(configuration.getDictionaryBaseUrl() + keyword);
        }
    }

    /** Show/Hide toolbars based on configuration file values. */
    private void setVisibleComponents() {
        Event event = new Event();

        utilitiesBarMenuItem.setSelection(configuration.getUtilitiesBarVisible());
        utilitiesBarMenuItem.notifyListeners(SWT.Selection, event);
        connectionBarMenuItem.setSelection(configuration.getConnectionBarVisible());
        connectionBarMenuItem.notifyListeners(SWT.Selection, event);
        bottomQuickBarMenuItem.setSelection(configuration.getBottomQuickBarVisible());
        bottomQuickBarMenuItem.notifyListeners(SWT.Selection, event);
    }

    /**
     * Open a new session in a new tab.
     *
     * @param item
     * @param session
     */
    public void addSession(CTabItem item, ConfigSession session) {
        if (item == null) {
            item = new CTabItem(folder, SWT.CLOSE);
        }

        Composite composite = new Composite(folder, SWT.EMBEDDED);
        item.setControl(composite);
        item.setData("TYPE", FieldConstants.SESSION);
        folder.setSelection(item);
        item.setText("connecting");
        item.setImage(ButtonImage.PUTTY_IMAGE);
        Thread t = new InvokeProgram(composite, item, session);
        t.start();
    }

    /**
     * 关闭其他标签.
     */
    private void closeOtherTabs() {
        int selectionIndex = folder.getSelectionIndex();
        int itemCount = folder.getItemCount();

        for (int index = 0; index < itemCount; index++) {
            if (index != selectionIndex) {
                CTabItem item = folder.getItem(index);
                closeTab(item);
            }
        }

    }

    /**
     * 关闭所有标签.
     */
    private void closeAllTabs() {
        int itemCount = folder.getItemCount();
        // 因为调用了dispose方法后, item数量有变化, 所以倒序关闭
        for (int index = itemCount - 1; index >= 0; index--) {
            CTabItem item = folder.getItem(index);
            closeTab(item);
        }
    }

    /**
     * 重新加载会话.
     */
    private void reloadSession() {
        CTabItem item = folder.getSelection();
        reloadTab(item);
    }

    /**
     * 重载标签.
     */
    private void reloadTab(CTabItem item) {
        if (Objects.nonNull(item)) {
            if (item.getData("hwnd") == null) {
                return;
            }
            int hWnd = Integer.parseInt(String.valueOf(item.getData("hwnd")));
            InvokeProgram.killProcess(hWnd);
            addSession(item, (ConfigSession) item.getData(FieldConstants.SESSION));
        }
    }

    /**
     * 重载所有会话.
     */
    private void reloadAllSession() {
        int itemCount = folder.getItemCount();

        for (int index = itemCount - 1; index >= 0; index--) {
            CTabItem tabItem = folder.getItem(index);
            reloadTab(tabItem);
        }
    }

    /**
     * 拷贝标签名称到剪贴板.
     */
    private void copyTabName() {
        CTabItem tabItem = folder.getSelection();
        if (tabItem.getData("hwnd") == null) {
            return;
        }

        String tabName = tabItem.getText();
        ClipboardUtil.setStr(tabName);
    }

    private void cloneSession() {
        CTabItem tabItem = folder.getSelection();
        if (tabItem.getData(FieldConstants.SESSION) == null) {
            return;
        }
        ConfigSession session = (ConfigSession) tabItem.getData(FieldConstants.SESSION);
        addSession(null, session);
    }

    private void openWinscp(String protocol) {
        if (folder.getSelection().getData(FieldConstants.SESSION) == null) {
            return;
        }
        ConfigSession session = (ConfigSession) folder.getSelection()
                .getData(FieldConstants.SESSION);
        String arg = protocol + "://" + session.getUser() + ":" + session.getPassword() + "@"
                + session.getHost() + ":" + session.getPort();

        InvokeProgram.runProgram(ProgramEnum.WINSCP, arg);
    }

    private void openPutty() {
        CTabItem tabItem = folder.getSelection();
        if (tabItem.getData(FieldConstants.SESSION) == null) {
            return;
        }
        ConfigSession session = (ConfigSession) tabItem.getData(FieldConstants.SESSION);
        InvokeProgram.invokeSinglePutty(session);
    }

    private void openVncSession() {
        CTabItem item = folder.getSelection();
        ConfigSession session = (ConfigSession) item.getData(FieldConstants.SESSION);
        if (session != null) {
            String host = session.getHost();
            InputDialog inputDialog = new InputDialog(SHELL, "Input VNC Server Host",
                    "Example:    xx.swg.usma.ibm.com:1", host + ":1", null);
            if (InputDialog.OK == inputDialog.open()) {
                InvokeProgram.runProgram(ProgramEnum.VNC, inputDialog.getValue());
            }
        }
    }

    /**
     * 关闭程序.
     */
    public void disposeApp() {
        CTabItem[] items = folder.getItems();
        for (CTabItem item : items) {
            closeTab(item);
        }

        // 关闭程序前, 保存窗口位置.
        configuration.saveBeforeClose();
    }

    /**
     * Show or hide a group of components.
     *
     * @param visible
     */
    private void setCompositeVisible(Composite composite, Shell shell, boolean visible) {
        // Show/Hide all composite children:
        for (Control control : composite.getChildren()) {
            control.setVisible(visible);
            control.setBounds(composite.getClientArea());
            control.getParent().layout();
        }

        // Show/Hide composite:
        composite.setVisible(visible);

        // Re-layout main screen to maximize tabs zone:
        composite.layout(true, true);
        shell.layout(true, true);
    }

    /** Check the feature toggle, dispose the features who equals to "false". */
    private void applyFeatureToggle() {
        // 是否显示vnc
        boolean bVnc = configuration.getFeatureToggle(ConfigConstant.Feature.VNC);
        if (!bVnc) {
            this.vncPopItem.dispose();
            this.itemVnc.dispose();
        }

        // 是否显示transfer
        boolean bTransfer = configuration.getFeatureToggle(ConfigConstant.Feature.TRANSFER);
        if (!bTransfer) {
            this.transferPopItem.dispose();
        }
    }

    public static void main(String[] args) {
        final Shell splash = new Shell(SWT.ON_TOP);
        final ProgressBar bar = new ProgressBar(splash, SWT.NONE);
        bar.setMaximum(5);
        Label label = new Label(splash, SWT.NONE);
        FormLayout layout = new FormLayout();
        splash.setLayout(layout);
        FormData labelData = new FormData();
        labelData.right = new FormAttachment(100, 0);
        labelData.bottom = new FormAttachment(100, 0);
        label.setLayoutData(labelData);
        FormData progressData = new FormData();
        // 设置启动滚动条的 边框
        bar.setLayoutData(progressData);
        splash.pack();
        Rectangle splashRect = splash.getBounds();

        Rectangle displayRect = display.getBounds();
        int width = displayRect.width;
        int height = displayRect.height;

        // add by lvcn 2018.8.29 多屏处理 获取主屏的分辨率
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gds = ge.getScreenDevices();
        if (gds.length > 1) {
            java.awt.Rectangle bounds = gds[0].getDefaultConfiguration().getBounds();
            width = bounds.width;
            height = bounds.height;
        }

        int x = (width - splashRect.width) / 2;
        int y = (height - splashRect.height) / 2;
        splash.setLocation(x, y);
        splash.open();
        display.asyncExec(() -> {
            new MainFrame(bar);
            splash.close();
            ButtonImage.SPLASH_IMAGE.dispose();
        });
        while (!SHELL.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
        display.dispose();
    }

    @Override
    public void widgetDefaultSelected(SelectionEvent e) {
        // do nothing
    }

    @Override
    public void widgetSelected(SelectionEvent e) {
        if (e.getSource() == newItem || e.getSource() == itemNew) {
            // 新增
            new NewSessionDialog(this, null, "add");
        } else if (e.getSource() == itemOpen || e.getSource() == openItem) {
            // 打开
            new OpenSessionDialog(this, SHELL);
        } else if (e.getSource() == itemRemoteDesk || e.getSource() == remoteDesktopItem) {
            InvokeProgram.runProgram(ProgramEnum.REMOTE_DESK, null);
        } else if (e.getSource() == exitItem) {
            disposeApp();
            System.exit(0);
        } else if (e.getSource() == itemCapture || e.getSource() == captureItem) {
            InvokeProgram.runProgram(ProgramEnum.CAPTURE, null);
            SHELL.setMinimized(true);
        } else if (e.getSource() == itemCalculator) {
            InvokeProgram.runProgram(ProgramEnum.CALCULATOR, null);
        } else if (e.getSource() == itemVnc) {
            InvokeProgram.runProgram(ProgramEnum.VNC, null);
        } else if (e.getSource() == itemNotePad) {
            InvokeProgram.runProgram(ProgramEnum.NOTEPAD, null);
        } else if (e.getSource() == tabNextItem) {
            switchTab();
        } else if (e.getSource() == killAllPuttyItem) {
            killAllPutty();
        } else if (e.getSource() == itemKenGen) {
            InvokeProgram.exec(configuration.getProgramPath(ProgramEnum.KEYGEN), null);
        } else if (e.getSource() == itemHelp || e.getSource() == welcomeMenuItem) {
            showWelcomeTab(ConstantValue.HOME_URL);
        } else if (e.getSource() == aboutItem) {
            MessageDialog.openInformation(SHELL, "About",
                    "SmartPutty-" + configuration.getSmartPuttyVersion());
        } else if (e.getSource() == utilitiesBarMenuItem) {
            Boolean visible = utilitiesBarMenuItem.getSelection();
            setCompositeVisible(utilitiesToolbar, SHELL, visible);
            configuration.setUtilitiesBarVisible(String.valueOf(visible));
        } else if (e.getSource() == connectionBarMenuItem) {
            Boolean visible = connectionBarMenuItem.getSelection();
            setCompositeVisible(connectGroup, SHELL, visible);
            configuration.setConnectionBarVisible(String.valueOf(visible));
        } else if (e.getSource() == bottomQuickBarMenuItem) {
            Boolean visible = bottomQuickBarMenuItem.getSelection();
            setCompositeVisible(quickBottomGroup, SHELL, visible);
            configuration.setBottomQuickBarVisible(String.valueOf(visible));
        } else if (e.getSource() == configProgramsLocationsItem) {
            new ProgramsLocationsDialog(SHELL);
            // menuItem
        } else if (e.getSource() == copyTabNamePopItem) {
            copyTabName();
        } else if (e.getSource() == reloadPopItem) {
            reloadSession();
        } else if (e.getSource() == reloadAllItem) {
            // 重载所有标签
            reloadAllSession();
        } else if (e.getSource() == closeOtherTabsItem) {
            // 关闭其他标签
            closeOtherTabs();
        } else if (e.getSource() == closeAllTabsItem) {
            // 关闭所有标签
            closeAllTabs();
        } else if (e.getSource() == openPuttyItem) {
            openPutty();
        } else if (e.getSource() == clonePopItem) {
            cloneSession();
        } else if (e.getSource() == ftpMenuItem) {
            openWinscp("ftp");
        } else if (e.getSource() == scpMenuItem) {
            openWinscp("scp");
        } else if (e.getSource() == sftpMenuItem) {
            openWinscp("sftp");
        } else if (e.getSource() == vncPopItem) {
            openVncSession();
            // folder
        } else if (e.getSource() == folder) {
            if (folder.getSelection().getData("hwnd") != null) {
                Number hWnd = (Number) folder.getSelection().getData("hwnd");
                InvokeProgram.setWindowFocus(hWnd);
            }
        } else if (StrUtil.endWith(e.getSource().getClass().toString(), "MenuItem")
                && "dynamicApplication".equals(((MenuItem) e.getSource()).getData("type"))) {
            String path = ((MenuItem) e.getSource()).getData("path").toString();
            String argument = ((MenuItem) e.getSource()).getData("argument").toString();
            InvokeProgram.exec(path, argument);
        } else if (e.getSource() == connectButton) {
            String host = hostItem.getText();
            String name = host;
            String port = portItem.getText();
            String user = usernameItem.getText();
            String password = passwordItem.getText();
            String session = sessionCombo.getText();
            if (StrUtil.isBlank(session)) {
                MessageDialog.openInformation(SHELL, "Information",
                        "please select a putty session first!");
                return;
            }
            ConfigSession configSession = new ConfigSession(name, host, "", port, user, password,
                    session);
            addSession(null, configSession);
        } else if (e.getSource() == win2UnixButton) {
            String path = pathItem.getText().trim();
            if (StrUtil.isBlank(path)) {
                MessageDialog.openInformation(SHELL, "Info",
                        MessageConstants.PLEASE_INPUT_CORRECT_PATH);
                return;
            }
            path = StrUtil.strip(path, "/\\" + configuration.getWinPathBaseDrive());
            pathItem.setText("/" + StrUtil.replace(path, WIN_PATH_DELIMITED, "/"));
        } else if (e.getSource() == unix2WinButton) {
            String path = pathItem.getText().trim();
            if (StrUtil.isBlank(path)) {
                MessageDialog.openInformation(SHELL, "Info",
                        MessageConstants.PLEASE_INPUT_CORRECT_PATH);
                return;
            }
            path = StrUtil.strip(path, "/\\" + configuration.getWinPathBaseDrive());
            pathItem.setText(configuration.getWinPathBaseDrive() + WIN_PATH_DELIMITED
                    + StrUtil.replace(path, "/", WIN_PATH_DELIMITED));
        } else if (e.getSource() == openPathButton) {
            String path = pathItem.getText().trim();
            if (StrUtil.isBlank(path)) {
                MessageDialog.openInformation(SHELL, "Info",
                        MessageConstants.PLEASE_INPUT_CORRECT_PATH);
                return;
            }
            path = StrUtil.strip(path, "/\\" + configuration.getWinPathBaseDrive());
            path = configuration.getWinPathBaseDrive() + WIN_PATH_DELIMITED
                    + StrUtil.replace(path, "/", WIN_PATH_DELIMITED);
            pathItem.setText(path);
            if (!InvokeProgram.openFolder(path)) {
                MessageDialog.openError(SHELL, "Error", "Path not exist!");
            }
        } else if (e.getSource() == dictButton) {
            String keyword = dictText.getText().trim();
            openDictTab(keyword);
        }
    }

    /**
     * 标签切换.
     */
    private void switchTab() {
        int select = folder.getSelectionIndex();
        int count = folder.getItemCount();

        if (count > 0) {
            int index = select < count - 1 ? select + 1 : 0;
            CTabItem item = folder.getItem(index);
            folder.setSelection(item);
        }
    }

    /**
     * 关闭所有putty进程.
     */
    private void killAllPutty() {
        try {
            RuntimeUtil.exec("taskkill /F /IM Putty.exe");
        } catch (Exception ex) {
            log.error("关闭Putty异常.");
        }
    }

    /**
     * 关闭标签.
     */
    @Override
    public void close(CTabFolderEvent e) {
        if (e.item == folder.getSelection()) {
            if ((ConfigSession) e.item.getData(FieldConstants.SESSION) != null) {
                MessageBox msgBox = new MessageBox(SHELL, SWT.ICON_QUESTION | SWT.YES | SWT.NO);
                msgBox.setText("Confirm Exit");
                msgBox.setMessage("Are you sure to exit session: "
                        + ((ConfigSession) e.item.getData(FieldConstants.SESSION)).getHost());
                if (msgBox.open() == SWT.YES) {
                    closeTab((CTabItem) e.item);
                    e.doit = true;
                } else {
                    e.doit = false;
                }
            }
        } else {
            e.item.dispose();
            e.doit = true;
            SHELL.setFocus();
        }
    }

    @Override
    public void maximize(CTabFolderEvent e) {
        // do nothing
    }

    @Override
    public void minimize(CTabFolderEvent e) {
        // do nothing
    }

    @Override
    public void restore(CTabFolderEvent e) {
        // do nothing
    }

    @Override
    public void showList(CTabFolderEvent e) {
        // do nothing
    }

    @Override
    public void mouseDoubleClick(MouseEvent e) {
        // do nothing
    }

    @Override
    public void mouseDown(MouseEvent e) {
        if (e.button == 3) {
            CTabItem selectItem = folder.getItem(new Point(e.x, e.y));
            if (selectItem != null && StrUtil.equalsIgnoreCase(
                    String.valueOf(folder.getSelection().getData("TYPE")),
                    FieldConstants.SESSION)) {
                folder.setSelection(selectItem);
                popupMenu.setVisible(true);
            } else {
                popupMenu.setVisible(false);
            }
        }
    }

    @Override
    public void mouseUp(MouseEvent e) {
        // do nothing
    }

    @Override
    public void shellActivated(ShellEvent e) {
        if (folder.getSelection() != null) {
            Object hWnd = folder.getSelection().getData("hwnd");
            if (hWnd != null) {
                InvokeProgram.setWindowFocus(Integer.parseInt(hWnd.toString()));
            }
        }
    }

    @Override
    public void shellClosed(ShellEvent e) {
        disposeApp();
        System.exit(0);
    }

    @Override
    public void shellDeactivated(ShellEvent arg0) {
        // do nothing
    }

    @Override
    public void shellDeiconified(ShellEvent arg0) {
        // do nothing
    }

    @Override
    public void shellIconified(ShellEvent arg0) {
        // do nothing
    }

    public void keyPressed(KeyEvent e) {
        // do nothing
    }

    public void keyReleased(KeyEvent arg0) {
        // do nothing
    }
}
