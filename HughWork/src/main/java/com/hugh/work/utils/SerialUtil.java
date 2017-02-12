package com.hugh.work.utils;

import java.text.SimpleDateFormat;

/**
 * Created by Hugh on 2016/11/28.
 */
public class SerialUtil extends Thread {
    private static String dateMillis;
    private static int SerialNum = 0;
    public static SimpleDateFormat dateFormat = new SimpleDateFormat(
            "yyyyMMddHHmmss");
    public synchronized static String getTimeSerial(String ip, String id) throws Exception {
        String ret = "";
        String strBSerialTime = System.currentTimeMillis() + "";
        if (strBSerialTime.equals(dateMillis)) {
            SerialNum++;
            if (SerialNum > 90) {
                Thread.sleep(1);
            } else if (SerialNum >= 100) {
                throw new Exception( String.format("序列号生成算法异常：%s，strBSerialTime=%s，strSerialTime=%s，SerialNum=%s",
                        new Object[]{
                                "SerialNum >= 100",strBSerialTime,dateMillis,SerialNum+""}));
            }
        }  else {
            SerialNum = 0;
            dateMillis = strBSerialTime;
        }
        ret = dateMillis + String.format("%03d", SerialNum);
        if (ret.length() > 16) {
            throw new Exception(
                    String.format("序列号生成算法异常：%s，strBSerialTime=%s，strSerialTime=%s，SerialNum=%s",
                            new Object[]{
                                    "ret.Length > 19",strBSerialTime,dateMillis,SerialNum+""}));
        }
        return String.format("%s%s%s", ip, id ,ret);
    }

    public void run() {
        try {
            for (int i = 0; i < 5; i++) {
                System.out.println(getTimeSerial("11.11.11", i + ""));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


    public static void main(String[] args) {
        System.out.println(System.currentTimeMillis() + "");

        for (int i = 0; i < 5; i++) {
           new SerialUtil().start();
        }
    }
}
