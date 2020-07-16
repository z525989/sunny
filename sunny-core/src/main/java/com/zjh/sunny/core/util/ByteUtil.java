package com.zjh.sunny.core.util;

/**
 * @author zhangJinHui
 * @date 2020-3-19 10:45
 */
public class ByteUtil {

    public static boolean isJson(byte[] reqByte) {
        if (reqByte == null || reqByte.length < 2) {
            return false;
        }

        boolean statTag = false;
        boolean endTag = false;

        for (byte b : reqByte) {
            if (b == '{') {
                statTag = true;
            } else if (b == '}') {
                endTag = true;
            }
        }
        return statTag && endTag;
    }
}
