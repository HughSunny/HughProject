package com.test.jazz;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import set.work.utils.ConvertUtil;
import set.work.utils.TimeUtil;

/**
 * Created by Hugh on 2017/11/8.
 */

public class DateTest {
    public static void main(String[] args) throws Exception {
        String stopTimeStr = "1|08:00";
        String[] stopTimeArray = stopTimeStr.split("\\|");
        if (stopTimeArray.length == 2) {
            Date date = TimeUtil.getDate();
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(date);
            calendar.add(calendar.DATE, Integer.parseInt(stopTimeArray[0]));
            String stopDay = ConvertUtil.dateYMD.format(calendar.getTime());
            String stopTime = stopDay + " "+ stopTimeArray[1] ;
            System.out.println(stopTime);
            Date stopTimeDate = ConvertUtil.dateYMDHM.parse(stopTime);
            System.out.println(stopTimeDate.toString());
        }
    }
}
