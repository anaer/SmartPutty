package control;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;

import java.awt.Desktop;
import java.io.File;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.internal.win32.OS;
import org.eclipse.swt.internal.win32.SHELLEXECUTEINFO;
import org.eclipse.swt.internal.win32.TCHAR;
import org.eclipse.swt.widgets.Composite;

import constants.ConstantValue;
import constants.FieldConstants;
import constants.MessageConstants;
import enums.ProgramEnum;
import enums.ProtocolEnum;
import enums.PuttySessionEnum;
import lombok.extern.slf4j.Slf4j;
import model.ConfigSession;
import ui.MainFrame;

/**
 * 调用程序.
 *
 * @author anaer
 * @version $Id: InvokeProgram.java, v 1.0 Jul 22, 2019 3:45:00 PM anaer Exp $
 */
@Slf4j
public class InvokeProgram extends Thread {

    private Composite composite;
    private ConfigSession session;
    private CTabItem tabItem;
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
        MainFrame.DISPLAY.syncExec(() -> {
            if (session != null) {
                if (ProtocolEnum.MINTTY == ProtocolEnum.find(session.getProtocol())) {
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
        String host = session.getHost();
        String port = session.getPort();
        String user = session.getUser();
        String password = session.getPassword();
        String keyFile = session.getKey();
        String protocol = session.getProtocol() == null ? "-ssh -2"
                : ProtocolEnum.find(session.getProtocol()).getParameter();
        String puttySession = session.getSession();

        StringBuilder sb = new StringBuilder();
        if (session.getConfigSessionType() == PuttySessionEnum.PURE_PUTTY_SESSION) {
            appendIfNotBlank(sb, " -load ", puttySession, true);
        } else {
            appendIfNotBlank(sb, " ", protocol);
            appendIfNotBlank(sb, " -i ", keyFile, true);
        }

        appendIfNotBlank(sb, " -l ", user, true);
        appendIfNotBlank(sb, " -pw ", password, true);
        appendIfNotBlank(sb, " -P ", port);
        appendIfNotBlank(sb, " ", host);

        log.debug("Putty command: putty{}", sb);

        return sb.toString();
    }

    /**
     * 判断参数不为空时, 追加参数.
     * @param sb
     * @param param
     * @param value
     */
    private static void appendIfNotBlank(StringBuilder sb, String param, String value) {
        appendIfNotBlank(sb, param, value, false);
    }

    /**
     * 判断参数不为空时, 追加参数.
     * @param sb
     * @param param
     * @param value
     * @param isWrap 是否使用双引号包起来
     */
    private static void appendIfNotBlank(StringBuilder sb, String param, String value,
            boolean isWrap) {
        if (StrUtil.isNotBlank(value)) {
            String str = isWrap ? StrUtil.wrap(value, "\"") : value;
            sb.append(param).append(str);
        }
    }

    /**
     * Start Putty in a tab.
     *
     * @param session
     */
    public void invokePutty(ConfigSession session) {
        String args = setPuttyParameters(session);

        // tab标签展示名称
        StringBuilder tabName = new StringBuilder();
        tabName.append(session.getName()).append("@").append(session.getHost());

        // 可能未配置内网ip
        if (StrUtil.isNotBlank(session.getIntranet())) {
            tabName.append("(").append(session.getIntranet()).append(")");
        }

        String path = MainFrame.CONFIGURATION.getProgramPath(ProgramEnum.PUTTY);
        String name = FileNameUtil.mainName(path);

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

        boolean result = OS.ShellExecuteEx(info);

        if (lpFile.intValue() != 0) {
            OS.HeapFree(hHeap.intValue(), 0, lpFile.intValue());
        }

        if (lpParameters.intValue() != 0) {
            OS.HeapFree(hHeap.intValue(), 0, lpParameters.intValue());
        }

        if (!result) {
            MessageDialog.openInformation(MainFrame.SHELL, "OPEN " + name + "ERROR",
                    String.format(MessageConstants.FORMAT_FAILED_CMD_2_ARG, path, args));
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

        int waitingTime = MainFrame.CONFIGURATION.getWaitForInitTime();
        while (count > 0
                && (hWnd = OS.FindWindow(new TCHAR(0, name, true), null)).intValue() == 0) {
            ThreadUtil.safeSleep(waitingTime);
            count--;
        }
        if (count == 0) {
            MessageDialog.openError(MainFrame.SHELL, "OPEN " + name + " ERROR",
                    String.format(MessageConstants.FORMAT_FAILED_CMD_2_ARG, path, args));
        }
        Number oldStyle = OS.GetWindowLong(hWnd.intValue(), OS.GWL_STYLE);
        // 隐藏标题栏
        OS.SetWindowLong(hWnd.intValue(), OS.GWL_STYLE,
                oldStyle.intValue() & ~OS.WS_CAPTION & ~OS.WS_BORDER);

        OS.SetParent(hWnd.intValue(), composite.handle);
        OS.SendMessage(hWnd.intValue(), OS.WM_SYSCOMMAND, OS.SC_MAXIMIZE, 0);

        if (hWnd.intValue() != 0) {
            tabItem.setText(tabName.toString());
            tabItem.setData("hwnd", hWnd);
            tabItem.setData(FieldConstants.SESSION, session);
            setWindowFocus(hWnd);
        } else {
            tabItem.dispose();
        }
    }

    /**
     * Start Putty in a tab.
     *
     * @param session
     */
    public void invokeMintty(ConfigSession session) {
        String args = " --dir ~";

        // Mount command-line Putty parameters:
        String tabDisplayName = "Cygwin";

        Number hHeap = OS.GetProcessHeap();
        String programPath = MainFrame.CONFIGURATION.getProgramPath(ProgramEnum.MINTTY);
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
                            MainFrame.CONFIGURATION.getProgramPath(ProgramEnum.PUTTY), args));
            return;
        }

        int count = 15;
        Number hWnd = 0;
        while (count > 0
                && (hWnd = OS.FindWindow(null, new TCHAR(0, "bash", true))).intValue() == 0) {
            int waitingTime = MainFrame.CONFIGURATION.getWaitForInitTime();
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
            tabItem.setData(FieldConstants.SESSION, session);
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
        String path = MainFrame.CONFIGURATION.getProgramPath(program);
        // 2. 如果路径不为空, 执行应用程序
        exec(path, arg);
    }


    public static void exec(String cmd) {
        exec(cmd, "");
    }

    /**
     * Execute an utility from left bar.
     *
     * @param program
     * @param arg
     */
    public static void exec(String cmd, String arg) {
        if (StrUtil.isBlank(cmd)) {
            log.warn("程序路径为空. 过滤执行.");
            return;
        }

        if (!FileUtil.isFile(cmd)) {
            MessageDialog.openInformation(null, "错误", cmd + " 程序不存在, 请检查程序路径是否正确.");
            return;
        }

        try {
            if (StrUtil.isNotBlank(arg)) {
                // 对于Winscp程序, 直接使用这个方式
                log.info("启动程序:{} {}", cmd, arg);
                Runtime.getRuntime().exec(cmd + " " + arg);
            } else {
                // 如果以上面方式启动如java反编译luyten-0.5.0.exe程序, 在关闭luyten程序后, 任务管理器中进程还在, 所以使用cmd方式启动
                Runtime.getRuntime().exec(new String[] { "cmd", "/c", "start", cmd });
            }
        } catch (Exception ex) {
            MessageDialog.openInformation(null, "错误", cmd + " " + ex.getMessage());
            log.error(ExceptionUtil.getMessage(ex));
        }
    }

    public static void invokeProxy(String host, String user, String password, String port) {
        String cmd = "cmd /c start " + MainFrame.CONFIGURATION.getProgramPath(ProgramEnum.PLINK)
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
            log.error(ExceptionUtil.getMessage(e));
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
        String commandString = MainFrame.CONFIGURATION.getProgramPath(ProgramEnum.PUTTY) + args;

        try {
            if (StrUtil.isNotBlank(commandString)) {
                Runtime.getRuntime().exec(commandString);
            }
        } catch (Exception e) {
            log.error("执行程序失败:{}", e);
        }
    }
}
