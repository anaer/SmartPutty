package utils;

import cn.hutool.core.util.StrUtil;

public class Comms {

    // 格式化标签名
    public static String formatName(String name, String host, String internat) {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append("@").append(host);

        // 可能未配置内网ip
        if (StrUtil.isNotBlank(internat)) {
            sb.append("(").append(internat).append(")");
        }
        return sb.toString();
    }

}
