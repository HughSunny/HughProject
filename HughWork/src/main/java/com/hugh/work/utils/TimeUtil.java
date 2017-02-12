package com.hugh.work.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * 时间的工具
 */
public class TimeUtil {
    /**GMT**/
    public static SimpleDateFormat SDF_GMT = new SimpleDateFormat(
            "E, dd MMM y HH:mm:ss 'GMT'", Locale.US);

    public static String YMDTHMS = "yyyy-MM-dd'T'HH:mm:ss";
    /**
     *
     * @param dateFormat
     * @return yyyyMMddHHmmss
     */
    public static String getDate(SimpleDateFormat dateFormat) {
        long currentTimeMillis = System.currentTimeMillis();
        Date currDate = new Date(currentTimeMillis);
        return dateFormat.format(currDate);
    }

    /**
     * long转化成时间的字符串
     * @param sdf
     * @param milliseconds
     * @return
     */
    public static String long2DateStr(SimpleDateFormat sdf, long milliseconds) {
        Date date= new Date(milliseconds);
        return sdf.format(date);
    }

    /**
     *
     * @param sdf
     * @param _date
     * @return
     */
    public static long date2long(SimpleDateFormat sdf, String _date)  {
        Date date = null;
        try {
            date = sdf.parse(_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (date == null) {
            return 0;
        }
        return (date.getTime());
    }


    /**
     * time format "00:00:00"
     * */
    public static int strTime2Int(String time) {
        int result = 0;
        String[] strBuffer = time.split(":");
        if (strBuffer == null || strBuffer.length < 3) {
            return 0;
        }
        result = Integer.parseInt(strBuffer[0]) * 3600
                + Integer.parseInt(strBuffer[1]) * 60
                + Integer.parseInt(strBuffer[2]);
        return result;
    }

    /**
     * long 转成 GMT
     * @param milliseconds
     * @return
     */
    public static String toGMTString(long milliseconds) {
        TimeZone gmtZone = TimeZone.getTimeZone("GMT");
        SDF_GMT.setTimeZone(gmtZone);
        GregorianCalendar gc = new GregorianCalendar(gmtZone);
        gc.setTimeInMillis(milliseconds);
        return SDF_GMT.format(gc.getTime());
    }
}
