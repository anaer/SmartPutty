package utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        setRegistry(REGISTRY_PUTTY_SETTINGS, "WarnOnClose", "REG_DWORD", "0");
        setRegistry(REGISTRY_PUTTY_SETTINGS, "LineCodePage", "REG_SZ", "UTF-8");
        setRegistry(REGISTRY_PUTTY_SETTINGS, "ScrollbackLines", "REG_DWORD", "5000");
        setRegistry(REGISTRY_PUTTY_SETTINGS, "WinTitle", "REG_SZ", " ");
        setRegistry(REGISTRY_PUTTY_SETTINGS, "NoRemoteWinTitle", "REG_DWORD", "1");
        setRegistry(REGISTRY_PUTTY_SETTINGS, "BlinkCur", "REG_DWORD", "1");
    }

    /**
     * 读取注册表配置.
     *
     * @return
     */
    public static Map<String, Object> readPuttyDefaultSettings() {
        Map<String, Object> map = new HashMap<>(6);
        map.put("WarnOnClose", readRegistry(REGISTRY_PUTTY_SETTINGS, "WarnOnClose"));
        map.put("LineCodePage", readRegistry(REGISTRY_PUTTY_SETTINGS, "LineCodePage"));
        map.put("ScrollbackLines", readRegistry(REGISTRY_PUTTY_SETTINGS, "ScrollbackLines"));
        map.put("WinTitle", readRegistry(REGISTRY_PUTTY_SETTINGS, "WinTitle"));
        map.put("NoRemoteWinTitle", readRegistry(REGISTRY_PUTTY_SETTINGS, "NoRemoteWinTitle"));
        map.put("BlinkCur", readRegistry(REGISTRY_PUTTY_SETTINGS, "BlinkCur"));
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
                    case "REG_SZ":
                        obj = parsed[3];
                        break;
                    case "REG_DWORD":
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
