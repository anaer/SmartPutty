package utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.RuntimeUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * Utilities to deal with Windows registry.
 *
 * @author Carlos SS
 */
@Slf4j
public class RegistryUtils {

    /**
     *
     */
    private static final String REG_SZ = "REG_SZ";
    /**
     *
     */
    private static final String REG_DWORD = "REG_DWORD";
    /**
     *
     */
    private static final String BLINK_CUR = "BlinkCur";
    /**
     *
     */
    private static final String NO_REMOTE_WIN_TITLE = "NoRemoteWinTitle";
    /**
     *
     */
    private static final String WIN_TITLE = "WinTitle";
    /**
     *
     */
    private static final String SCROLLBACK_LINES = "ScrollbackLines";
    /**
     *
     */
    private static final String LINE_CODE_PAGE = "LineCodePage";
    /**
     *
     */
    private static final String WARN_ON_CLOSE = "WarnOnClose";

    private RegistryUtils(){}

    /**
     * putty会话配置.
     */
    private static final String REGISTRY_PUTTY_SESSIONS = "HKCU\\Software\\SimonTatham\\PuTTY\\Sessions";
    /**
     * putty默认配置.
     */
    private static final String REGISTRY_PUTTY_SETTINGS = "HKCU\\Software\\SimonTatham\\PuTTY\\Sessions\\Default%20Settings";

    /**
     * Create all needed Putty registry keys.
     */
    public static void createPuttyKeys() {
        try {
            initPuttyDefaultSettings();
        } catch (Exception ex) {
            log.error("初始化Putty默认配置异常.", ex);
        }
    }

    /**
     * 初始化 注册表 默认配置.
     */
    public static void initPuttyDefaultSettings() {
        setRegistry(REGISTRY_PUTTY_SETTINGS, WARN_ON_CLOSE, REG_DWORD, "0");
        setRegistry(REGISTRY_PUTTY_SETTINGS, LINE_CODE_PAGE, REG_SZ, "UTF-8");
        setRegistry(REGISTRY_PUTTY_SETTINGS, SCROLLBACK_LINES, REG_DWORD, "5000");
        setRegistry(REGISTRY_PUTTY_SETTINGS, WIN_TITLE, REG_SZ, " ");
        setRegistry(REGISTRY_PUTTY_SETTINGS, NO_REMOTE_WIN_TITLE, REG_DWORD, "1");
        setRegistry(REGISTRY_PUTTY_SETTINGS, BLINK_CUR, REG_DWORD, "1");
    }

    /**
     * 读取注册表配置.
     *
     * @return
     */
    public static Map<String, Object> readPuttyDefaultSettings() {
        Map<String, Object> map = MapUtil.newHashMap(6);
        map.put(WARN_ON_CLOSE, readRegistry(REGISTRY_PUTTY_SETTINGS, WARN_ON_CLOSE));
        map.put(LINE_CODE_PAGE, readRegistry(REGISTRY_PUTTY_SETTINGS, LINE_CODE_PAGE));
        map.put(SCROLLBACK_LINES, readRegistry(REGISTRY_PUTTY_SETTINGS, SCROLLBACK_LINES));
        map.put(WIN_TITLE, readRegistry(REGISTRY_PUTTY_SETTINGS, WIN_TITLE));
        map.put(NO_REMOTE_WIN_TITLE, readRegistry(REGISTRY_PUTTY_SETTINGS, NO_REMOTE_WIN_TITLE));
        map.put(BLINK_CUR, readRegistry(REGISTRY_PUTTY_SETTINGS, BLINK_CUR));
        return map;
    }

    /**
     * Get all Putty sessions.
     *
     * @return
     */

    public static List<String> getAllPuttySessions() {
        List<String> list = new ArrayList<>();
        String[] locations = readRegistryList(REGISTRY_PUTTY_SESSIONS);
        for (String location : locations) {
            String[] pieces = location.split("\\\\");
            String name = pieces[pieces.length - 1].trim();
            if (StrUtil.isNotBlank(name) && !StrUtil.equals(name, "Default%20Settings")) {
                list.add(name);
            }
        }
        return list;
    }

    /**
     * 读取注册表配置.
     *
     * @param location
     * @param key
     * @return
     */
    public static final Object readRegistry(String location, String key) {
        Object obj = null;
        try {
            String cmd = String.format("reg query %s /v %s", location, key);
            log.debug("{}", cmd);
            String output = RuntimeUtil.execForStr(cmd);
            String[] parsed = output.trim().split("\\s+");

            if (parsed.length == 4) {
                switch (parsed[2]) {
                    case REG_SZ:
                        obj = parsed[3];
                        break;
                    case REG_DWORD:
                        obj = Integer.decode(parsed[3]);
                        break;
                    default:
                        obj = "";
                        break;
                }
            } else if (parsed.length == 3) {
                obj = "";
            } else {
                log.debug("query result: {}", Arrays.deepToString(parsed));
            }

            return obj;
        } catch (Exception e) {
            log.error("readRegistry异常.", e);
        }
        return obj;
    }

    /**
     * 设置注册表.
     *
     * @param location
     * @param key
     * @param type
     * @param value
     */
    public static final void setRegistry(String location, String key, String type, String value) {
        try {
            String cmd = String.format("reg add %s /v %s /t %s /f /d %s", location, key, type,
                value);
            RuntimeUtil.exec(cmd);
        } catch (Exception e) {
            log.error("setRegistry异常.", e);
        }
    }

    /**
     * 读取注册表列表.
     *
     * @param location
     * @return
     */
    public static final String[] readRegistryList(String location) {
        try {
            String cmd = String.format("reg query %s ", location);
            String output = RuntimeUtil.execForStr(cmd);
            return StrUtil.trimToEmpty(output).split("\\s+");
        } catch (Exception e) {
            log.error("readRegistryList异常.", e);
            return new String[]{};
        }
    }

}
