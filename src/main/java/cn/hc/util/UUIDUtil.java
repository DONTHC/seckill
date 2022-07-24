package cn.hc.util;

import java.util.UUID;

/**
 * UUID工具类
 *
 * @author zhoubin
 * @since 1.0.0
 */
public class UUIDUtil {

    public static String uuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static String SESSION() {
        String res = uuid() + uuid();
        return "SESSION=" + res.substring(0, 48);
    }

}