package set.work.utils;

/**
 * 按照时间 生成序列号
 */
public class SerialUtil {
    private static String dateMillis;
    private static int SerialNum = 0;
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
}
