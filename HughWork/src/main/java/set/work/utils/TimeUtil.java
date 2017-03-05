package set.work.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * 时间的工具
 */
public class TimeUtil {

    public static long timeDiff = 0l;
    /**
     * 获取时间
     * @return
     */
    public static Date getDate(){
        if (timeDiff != 0) {
            return new Date(System.currentTimeMillis() + timeDiff);
        }
        return new Date();
    }

    /**
     * 计算时间差
     */
    public static void calcTimeDiff (Date serverTime) {
        timeDiff = serverTime.getTime() - new Date().getTime();;
    }

    public static boolean isDateAgo(Date date,int dayAgoCount){
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(calendar.DATE, dayAgoCount);
        date= calendar.getTime();
        Date nowDate = getDate();
        return  date.before(nowDate);
    }

    /**
     *
     * @param dateFormat
     * @return yyyyMMddHHmmss
     */
    public static String getDate(SimpleDateFormat dateFormat) {
        Date curDate = getDate();
        return dateFormat.format(curDate);
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
        ConvertUtil.SDF_GMT.setTimeZone(gmtZone);
        GregorianCalendar gc = new GregorianCalendar(gmtZone);
        gc.setTimeInMillis(milliseconds);
        return ConvertUtil.SDF_GMT.format(gc.getTime());
    }
}
