package com.hugh.work.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 类型转换工具类
 * Created by Hugh on 2016/6/7.
 */
public class ConvertUtil {
    /**yyyy-MM-dd**/
    public static SimpleDateFormat dateYMD = new SimpleDateFormat("yyyy-MM-dd");
    /**yyyy-MM-dd HH:mm:ss**/
    public static SimpleDateFormat dateYMDHMS = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss");
    /**yyyyMMddHHmmss**/
    public static SimpleDateFormat dateYMDHMS2 = new SimpleDateFormat(
            "yyyyMMddHHmmss");
    /**yyyyMMddHHmmss**/
    public static SimpleDateFormat dateYMDHM = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm");
    /**yyyy-MM-ddHH:mm:ss**/
    public static SimpleDateFormat dateflow = new SimpleDateFormat(
            "yyyy-MM-ddHH:mm:ss");
    /**yyyy-MM-dd_HH:mm:ss**/
    public static SimpleDateFormat dateYMD_HMS = new SimpleDateFormat(
            "yyyy-MM-dd_HH:mm:ss");
    /**MM-dd HH:mm:ss**/
    public static SimpleDateFormat dateMDHMS = new SimpleDateFormat(
            "MM-dd HH:mm:ss");
    /**yyyy-MM-ddTHH:mm:ss**/
    public static SimpleDateFormat dateYMDTHMS = new SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss");
    public static String PATTERN_NUM = "[+-]?[1-9]+[0-9]*(\\.[0-9]+)?";
    public static String PATTERN_LETTER = "[a-zA-Z]+";
    // 中日韩文字 ^[\u2E80-\u9FFF]*$
    public static String PATTERN_CHINESE = "[\u4e00-\u9fa5]";

    public static boolean isNumbic(String value) {
        if ( value != null) {
            return value.matches(PATTERN_NUM);
        }
        return false;
    }

    /**
     * 转成Int
     * @param value
     * @return
     */
    public static int toInt(String value){
        if (value == null) return 0;

        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 转成double
     * @param value
     * @return
     */
    public static double toDouble(String value){
        if (value == null) return 0;
        try {
            return Double.parseDouble(value);
        } catch (Exception e) {
            return 0;
        }
    }

    public static Date toDateTime(String value ,SimpleDateFormat sdf) {
        if (value == null) return null;
        try {
            Date temp = sdf.parse(value.toString());
            return temp;
        } catch (Exception e) {
            return null;
        }
    }
}
