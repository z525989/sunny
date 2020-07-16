package com.zjh.sunny.core.util;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

public class StringUtil {

    private static boolean isEmpty(String str) {
        if (str == null) return true;
        return str.length() == 0;
    }

    /**
     * 判断字符串是否包含空
     *
     * @param args string列表
     * @return boolean
     */
    public static boolean isEmpty(String... args) {
        if (args.length <= 0) return true;
        for (String str : args) {
            boolean flag = isEmpty(str);
            if (flag) return true;
        }
        return false;
    }

    public static boolean isNotEmpty(String... args) {
        if (args.length <= 0) return false;
        for (String str : args) {
            boolean flag = isEmpty(str);
            if (flag) return false;
        }
        return true;
    }

    public static boolean isValid(Object obj) {
        if (obj == null) {
            return false;
        } else if ((obj instanceof String) && obj.toString().trim().length() == 0) {
            return false;
        } else if ((obj instanceof List) && ((List) obj).size() == 0) {
            return false;
        } else if ((obj instanceof Map) && ((Map) obj).keySet().size() == 0) {
            return false;
        }
        return true;
    }

    public static boolean isBlank(Object obj) {
        return !isValid(obj);
    }

    public static boolean isInt(String str) {
        if (str == null)
            return false;
        Pattern pattern = Pattern.compile("[-+]{0,1}[0-9]+");
        return pattern.matcher(str).matches();
    }

    public static boolean isFloat(String str) {
        if (str == null)
            return false;
        Pattern pattern = Pattern.compile("^[-\\+]?[.\\d]*$");
        return pattern.matcher(str).matches();
    }

    /**
     * 获取int参数，若输入字符串为null或不能转为int，则返回0
     *
     * @param str
     * @return int
     */
    public static int strToInt(String str) {
        if (!isInt(str)) return 0;
        return Integer.parseInt(str);
    }

    public static float strToFloat(String str) {
        if (!isFloat(str)) return 0;
        return Float.parseFloat(str);
    }

    /**
     * 获取UUID
     *
     * @return UUID
     */
    public static String getUUID() {
        return UUID.randomUUID().toString();
    }
}
