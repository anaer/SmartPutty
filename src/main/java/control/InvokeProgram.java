package control;

import java.awt.Desktop;
import java.io.File;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.internal.win32.OS;
import org.eclipse.swt.internal.win32.SHELLEXECUTEINFO;
import org.eclipse.swt.internal.win32.TCHAR;
import org.eclipse.swt.widgets.Composite;

import cn.hutool.core.thread.ThreadUtil;
import constants.ConstantValue;
import enums.ProgramEnum;
import enums.ProtocolEnum;
import enums.PuttySessionEnum;
import lombok.extern.slf4j.Slf4j;
import model.ConfigSession;
import ui.MainFrame;

/**
 * 调用程序.
 *
 * @author lvcn
 * @version $Id: InvokeProgram.java, v 1.0 Jul 22, 2019 3:45:00 PM lvcn Exp $
 */
@Slf4j
public class InvokeProgram extends Thread {

    private Composite      composite;
    private ConfigSession  session;
    private CTabItem       tabItem;
    /** 是否64位. */
    private static boolean is64 = true;

    /** Constructor: */
    public InvokeProgram(Composite composite, CTabItem tabItem, ConfigSession session) {
        this.composite = composite;
        this.tabItem = tabItem;
        this.session = session;
    }

    @Override
    public void run() {
        MainFrame.display.syncExec(() -> {
            if (session != null) {
                if (ProtocolEnum.MINTTY == session.getProtocol()) {
                    invokeMintty(session);
                } else {
                    invokePutty(session);
                }
            }
        });
    }

    public static void setWindowFocus(Number hWnd) {
        if (is64) {
            // 64位
            OS.SendMessage(hWnd.intValue(), OS.WM_SETFOCUS, 0, 0);
            OS.SetCapture(hWnd.intValue());
        } else {
            // 32位
            OS.SetForegroundWindow(hWnd.intValue());
            OS.SetCursor(hWnd.intValue());
        }
    }

    public static void setMainWindowFocus() {
        Number hWndMainWindow = isMainWindowRunning();
        setWindowFocus(hWndMainWindow);
    }

    public static Number isMainWindowRunning() {
        return OS.FindWindow(null, new TCHAR(0, ConstantValue.MAIN_WINDOW_TITLE, true));
    }

    /**
     * Helper to mount Putty command-line parameters.
     *
     * @return
     */
    private static String setPuttyParameters(ConfigSession session) {
        String args = "";

        String host = session.getHost();
        String port = session.getPort();
        String user = session.getUser();
        String password = session.getPassword();
        String file = session.getKey();
        String protocol = session.getProtocol() == null ? "-ssh -2"
            : session.getProtocol().getParameter();
        String puttySession = session.getSession();

        if (session.getConfigSessionType() == PuttySessionEnum.PURE_PUTTY_SESSION) {
            // Putty session must the very first parameter to work well.
            if (StringUtils.isNotBlank(puttySession)) {
                args += " -load \"" + puttySession + "\"";
            }
            if (!user.isEmpty()) {
                args += String.format(" -l \"%s\"", user);
            }
            if (!password.isEmpty()) {
                args += String.format(" -pw \"%s\"", password);
            }
            if (!port.isEmpty()) {
                args += String.format(" -P %s ", port);
            }
            if (!host.isEmpty()) {
                args += String.format(" %s", host);
            }
        } else {
            args = String.format(" %s %s ", protocol, host);

            if (!port.isEmpty()) {
                args += String.format(" -P %s ", port);
            }

            if (!user.isEmpty()) {
                args += String.format(" -l \"%s\"", user);
            }

            if (!password.isEmpty()) {
                args += String.format(" -pw \"%s\"", password);
            }
            // private key
            if (!file.isEmpty()) {
                args += String.format(" -i \"%s\"", file);
            }
        }
        log.debug("Putty parameters: putty {}", args);

        return args;
    }

    private static String setMinttyParameters(ConfigSession session) {
        String args = " --dir ~";
        log.debug("Mintty parameters: {}", args);
        return args;
    }

    /**
     * Start Putty in a tab.
     *
     * @param session
     */
    public void invokePutty(ConfigSession session) {
        String args = setPuttyParameters(session);

        // tab标签展示名称
        String tabDisplayName = String.format("%s@%s/%s", session.getName(), session.getHost(),
            session.getIntranet());

        String path = MainFrame.configuration.getProgramPath(ProgramEnum.PUTTY);
        String name = getFileNameWithoutSuffix(path);

        Number hHeap = OS.GetProcessHeap();
        TCHAR buffer = new TCHAR(0, path, true);
        int byteCount = buffer.length() * TCHAR.sizeof;
        Number lpFile = OS.HeapAlloc(hHeap.intValue(), OS.HEAP_ZERO_MEMORY, byteCount);
        TCHAR buffer1 = new TCHAR(0, args, true);
        int byteCount1 = buffer1.length() * TCHAR.sizeof;
        Number lpParameters = OS.HeapAlloc(hHeap.intValue(), OS.HEAP_ZERO_MEMORY, byteCount1);

        OS.MoveMemory(lpFile.intValue(), buffer, byteCount);
        OS.MoveMemory(lpParameters.intValue(), buffer1, byteCount1);

        SHELLEXECUTEINFO info = new SHELLEXECUTEINFO();
        info.cbSize = SHELLEXECUTEINFO.sizeof;
        info.lpFile = lpFile.intValue();
        info.lpParameters = lpParameters.intValue();

        // info.nShow = OS.SW_HIDE;

        boolean result = OS.ShellExecuteEx(info);

        if (lpFile.intValue() != 0) {
            OS.HeapFree(hHeap.intValue(), 0, lpFile.intValue());
        }

        if (lpParameters.intValue() != 0) {
            OS.HeapFree(hHeap.intValue(), 0, lpParameters.intValue());
        }

        if (!result) {
            MessageDialog.openInformation(MainFrame.SHELL, "OPEN " + name + "ERROR",
                String.format("Failed cmd: %s %s", path, args));
            return;
        }

        // 如果有安全警告窗口 等待人工处理
        Number hWndAlert = OS.FindWindow(null,
            new TCHAR(0, ConstantValue.PUTTY_SECURITY_ALERT, true));
        if (hWndAlert.intValue() != 0) {
            int waitingForOperation = 10000;
            while (waitingForOperation > 0) {
                if (OS.FindWindow(null,
                    new TCHAR(0, ConstantValue.PUTTY_SECURITY_ALERT, true)) == 0) {
                    break;
                }
                ThreadUtil.safeSleep(500);
                waitingForOperation -= 500;
            }
        }

        int count = 15;
        Number hWnd = 0;

        while (count > 0
                && (hWnd = OS.FindWindow(new TCHAR(0, name, true), null)).intValue() == 0) {
            int waitingTime = Integer.parseInt(MainFrame.configuration.getWaitForInitTime());
            ThreadUtil.safeSleep(waitingTime);
            count--;
        }
        if (count == 0) {
            MessageDialog.openError(MainFrame.SHELL, "OPEN " + name + " ERROR",
                String.format("Failed cmd: %s %s", path, args));
        }
        Number oldStyle = OS.GetWindowLong(hWnd.intValue(), OS.GWL_STYLE);
        // 隐藏标题栏
        OS.SetWindowLong(hWnd.intValue(), OS.GWL_STYLE,
            oldStyle.intValue() & ~OS.WS_CAPTION & ~OS.WS_BORDER);

        OS.SetParent(hWnd.intValue(), composite.handle);
        OS.SendMessage(hWnd.intValue(), OS.WM_SYSCOMMAND, OS.SC_MAXIMIZE, 0);

        if (hWnd.intValue() != 0) {
            tabItem.setText(tabDisplayName);
            tabItem.setData("hwnd", hWnd);
            tabItem.setData("session", session);
            setWindowFocus(hWnd);
        } else {
            tabItem.dispose();
        }
    }

    /** 获取不带后缀名的文件名. */
    public static String getFileNameWithoutSuffix(File file) {
        String fileName = file.getName();
        return fileName.substring(0, fileName.lastIndexOf('.'));
    }

    /** 获取不带后缀名的文件名. */
    public static String getFileNameWithoutSuffix(String path) {
        File file = new File(path);
        return getFileNameWithoutSuffix(file);
    }

    /**
     * Start Putty in a tab.
     *
     * @param session
     */
    public void invokeMintty(ConfigSession session) {
        String args = setMinttyParameters(session);

        // Mount command-line Putty parameters:
        String tabDisplayName = "Cygwin";

        Number hHeap = OS.GetProcessHeap();
        String programPath = MainFrame.configuration.getProgramPath(ProgramEnum.MINTTY);
        TCHAR buffer = new TCHAR(0, programPath, true);
        int byteCount = buffer.length() * TCHAR.sizeof;
        Number lpFile = OS.HeapAlloc(hHeap.intValue(), OS.HEAP_ZERO_MEMORY, byteCount);
        TCHAR buffer1 = new TCHAR(0, args, true);
        int byteCount1 = buffer1.length() * TCHAR.sizeof;
        Number lpParameters = OS.HeapAlloc(hHeap.intValue(), OS.HEAP_ZERO_MEMORY, byteCount1);

        OS.MoveMemory(lpFile.intValue(), buffer, byteCount);
        OS.MoveMemory(lpParameters.intValue(), buffer1, byteCount1);

        SHELLEXECUTEINFO info = new SHELLEXECUTEINFO();
        info.cbSize = SHELLEXECUTEINFO.sizeof;
        info.lpFile = lpFile.intValue();
        info.lpParameters = lpParameters.intValue();

        info.nShow = OS.SW_SHOW;

        boolean result = OS.ShellExecuteEx(info);

        if (lpFile.intValue() != 0) {
            OS.HeapFree(hHeap.intValue(), 0, lpFile.intValue());
        }

        if (lpParameters.intValue() != 0) {
            OS.HeapFree(hHeap.intValue(), 0, lpParameters.intValue());
        }

        if (!result) {
            log.info("启动失败:{} {}", programPath, result);
            MessageDialog.openInformation(MainFrame.SHELL, "OPEN MINTTY ERROR",
                String.format("Failed cmd: %s %s",
                    MainFrame.configuration.getProgramPath(ProgramEnum.PUTTY), args));
            return;
        }

        int count = 15;
        Number hWnd = 0;
        while (count > 0
                && (hWnd = OS.FindWindow(null, new TCHAR(0, "bash", true))).intValue() == 0) {
            int waitingTime = Integer.parseInt(MainFrame.configuration.getWaitForInitTime());

            ThreadUtil.safeSleep(waitingTime);
            count--;
        }
        if (count == 0) {
            MessageDialog.openError(MainFrame.SHELL, "OPEN MINTTY ERROR",
                String.format("Failed cmd: %s %s", programPath, args));
        }
        Number oldStyle = OS.GetWindowLong(hWnd.intValue(), OS.GWL_STYLE);
        OS.SetWindowLong(hWnd.intValue(), OS.GWL_STYLE,
            oldStyle.intValue() & ~OS.WS_CAPTION & ~OS.WS_BORDER);

        OS.SetParent(hWnd.intValue(), composite.handle);
        OS.SendMessage(hWnd.intValue(), OS.WM_SYSCOMMAND, OS.SC_MAXIMIZE, 0);

        if (hWnd.intValue() != 0) {
            tabItem.setText(tabDisplayName);
            tabItem.setData("hwnd", hWnd);
            tabItem.setData("session", session);
            setWindowFocus(hWnd);
        } else {
            tabItem.dispose();
        }
    }

    public static void killProcess(int hWnd) {
        OS.SendMessage(hWnd, OS.WM_CLOSE, null, 0);
    }

    public static void killPuttyWarningsAndErrs() {
        Number hWndAlert = OS.FindWindow(null,
            new TCHAR(0, ConstantValue.PUTTY_SECURITY_ALERT, true));
        if (hWndAlert.intValue() != 0) {
            killProcess(hWndAlert.intValue());
        }

        Number hWndError = OS.FindWindow(null, new TCHAR(0, "PuTTY Error", true));
        if (hWndError.intValue() != 0) {
            killProcess(hWndError.intValue());
        }

        hWndError = OS.FindWindow(null, new TCHAR(0, "PuTTY Fatal Error", true));
        if (hWndError.intValue() != 0) {
            killProcess(hWndError.intValue());
        }
    }

    /**
     * Execute an utility from left bar.
     *
     * @param program
     * @param arg
     */
    public static void runProgram(ProgramEnum program, String arg) {
        // 1. 获取应用程序执行路径
        String path = MainFrame.configuration.getProgramPath(program);
        if (StringUtils.isNotBlank(path)) {
            // 2. 如果路径不为空, 执行应用程序
            exec(path, arg);
        }
    }

    /**
     * Execute an utility from left bar.
     *
     * @param program
     * @param arg
     */
    public static void exec(String program, String arg) {
        if (StringUtils.isBlank(program)) {
            log.warn("程序路径为空. 过滤执行.");
            return;
        }

        String cmd;

        if (StringUtils.isNotBlank(arg)) {
            cmd = program + " " + arg;
        } else {
            cmd = program;
        }

        try {
            Runtime.getRuntime().exec(cmd);
        } catch (Exception ex) {
            MessageDialog.openInformation(null, "错误", program + " " + ex.getMessage());
            log.error(ExceptionUtils.getStackTrace(ex));
        }
    }

    public static void invokeProxy(String host, String user, String password, String port) {
        String cmd = "cmd /c start " + MainFrame.configuration.getProgramPath(ProgramEnum.PLINK)
                + " -D " + port + " -pw " + password + " -N " + user + "@" + host;
        try {
            Runtime.getRuntime().exec(cmd);
        } catch (Exception e) {
            log.error("cmd启动程序失败:{}", e);
        }
    }

    /**
     * Open path in windows explorer.
     *
     * @param path
     */
    public static boolean openFolder(String path) {
        Desktop desktop = Desktop.getDesktop();
        File dirToOpen = null;
        try {
            dirToOpen = new File(path);
            desktop.open(dirToOpen);
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
            return false;
        }
        return true;
    }

    /**
     * Start Putty in a single window.
     *
     * @param session
     */
    public static void invokeSinglePutty(ConfigSession session) {
        // Mount command-line Putty parameters:
        String args = setPuttyParameters(session);
        String commandString = MainFrame.configuration.getProgramPath(ProgramEnum.PUTTY) + args;

        try {
            if (StringUtils.isNotBlank(commandString)) {
                Runtime.getRuntime().exec(commandString);
            }
        } catch (Exception e) {
            log.error("执行程序失败:{}", e);
        }
    }

    public static void startProxy(String arg) {
        // do nothing
    }
}
