package set.work.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.SignalStrength;
import android.util.Log;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Hugh on 2016/5/31.
 * 网络工具类
 */
public class NetworkUtil {
    private static final String TAG = "NetworkUtil";
    public static final String CTWAP = "ctwap";
    public static final String CTNET = "ctnet";
    public static final String CMNET = "cmnet";
    public static final String CMWAP = "cmwap";
    public static final int SIGNAL_STRENGTH_NONE_OR_UNKNOWN = 0;
    public static final int SIGNAL_STRENGTH_POOR = 1;
    public static final int SIGNAL_STRENGTH_MODERATE = 2;
    public static final int SIGNAL_STRENGTH_GOOD = 3;
    public static final int SIGNAL_STRENGTH_GREAT = 4;
    public static int signalStrengthValue;
    private static String localIp;

    public static String getLocalIp() {
        if (localIp == null) {
            localIp = getLocalIpAddress();
        }
        return localIp;
    }

    public static void setLocalIp(String localIp) {
        NetworkUtil.localIp = localIp;
    }

    /**
     * 获取本机ip地址
     * @return
     */
    public static String getLocalIpAddress() {
        String hostIp = null;
        try {
            Enumeration nis = NetworkInterface.getNetworkInterfaces();
            InetAddress ia = null;
            while (nis.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) nis.nextElement();
                Enumeration<InetAddress> ias = ni.getInetAddresses();
                while (ias.hasMoreElements()) {
                    ia = ias.nextElement();
                    if (ia instanceof Inet6Address) {
                        continue;// skip ipv6
                    }
                    String ip = ia.getHostAddress();
                    if (!"127.0.0.1".equals(ip)) {
                        hostIp = ia.getHostAddress();
                        break;
                    }
                }
            }
        } catch (SocketException e) {
            Log.i(TAG, "SocketException");
            e.printStackTrace();
        }
        return hostIp;
    }
    /**
     * 计算wifi的信号强度
     * @param mContext
     * @return
     */
    public static int calculateWifiSignalLevel(Context mContext) {
        WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        //获得信号强度值
       int level = wifiInfo.getRssi();
        //根据获得的信号强度发送信息
        if (level <= 0 && level >= -50) {
            signalStrengthValue = SIGNAL_STRENGTH_GREAT;
        } else if (level < -50 && level >= -70) {
            signalStrengthValue = SIGNAL_STRENGTH_GOOD;
        } else if (level < -70 && level >= -80) {
            signalStrengthValue = SIGNAL_STRENGTH_MODERATE;
        } else if (level < -80 && level >= -100) {
            signalStrengthValue = SIGNAL_STRENGTH_POOR;
        } else {
            signalStrengthValue = SIGNAL_STRENGTH_NONE_OR_UNKNOWN;
        }
        return signalStrengthValue;
    }

    /**
     * 计算gsm的信号强度
     * @param signalStrength
     * @return
     */
    public static int calculateGsmSignalLevel(SignalStrength signalStrength) {
        if (signalStrength.isGsm()) {
            if (signalStrength.getGsmSignalStrength() != 99)
                signalStrengthValue = signalStrength.getGsmSignalStrength() * 2 - 113;
            else {
                signalStrengthValue = signalStrength.getGsmSignalStrength();
                signalStrengthValue = -101;
            }
        } else {
            signalStrengthValue = signalStrength.getCdmaDbm(); // mCdmaDbm
        }
        int levelDbm = 0;
        int cdmaDbm = signalStrengthValue;
        if (cdmaDbm >= -75)
            levelDbm = SIGNAL_STRENGTH_GREAT;
        else if (cdmaDbm >= -85)
            levelDbm = SIGNAL_STRENGTH_GOOD;
        else if (cdmaDbm >= -95)
            levelDbm = SIGNAL_STRENGTH_MODERATE;
        else if (cdmaDbm >= -100)
            levelDbm = SIGNAL_STRENGTH_POOR;
        else
            levelDbm = SIGNAL_STRENGTH_NONE_OR_UNKNOWN;
        Log.d(TAG, "onSignalStrengthsChanged" + levelDbm );
        return levelDbm;
    }

    /**
     * wifi可以用
     * @param inContext
     * @return
     */
    public static boolean isWiFiActive(Context inContext) {
        Context context = inContext.getApplicationContext();
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getTypeName().equals("WIFI") && info[i].isConnected()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 获取当前的网络类型
     * @param mContext
     * @return
     */
    public static String getCurrentNetType(Context mContext) {
        ConnectivityManager connManager = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connManager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI); // wifi
        NetworkInfo gprs = connManager
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE); // gprs
        if (wifi != null && wifi.getState() == NetworkInfo.State.CONNECTED) {
            Log.d(TAG, "Current net type:  WIFI.");
            return "Wifi";
        } else if (gprs != null && gprs.getState() == NetworkInfo.State.CONNECTED) {
            Log.d(TAG, "Current net type:  MOBILE.");
            return "mobile";
        }
        Log.e(TAG, "Current net type:  NONE.");
        return "none";
    }

    /**
     * 是否是连接apn
     * @param context
     * @return
     */
    public static String getApnConnect(Context context) {
        String user = null;
        String apn = null;
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            // 获取网络连接管理的对象
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                // 判断当前网络是否已经连接
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    if (info.getTypeName().equals("WIFI")) {

                    } else {
                        Uri uri = Uri
                                .parse("content://telephony/carriers/preferapn");
                        Cursor cr = context.getContentResolver().query(uri,
                                null, null, null, null);
                        while (cr != null && cr.moveToNext()) {
                            // APN id
                            @SuppressWarnings("unused")
                            String id = cr.getString(cr.getColumnIndex("_id"));
                            // APN name
                            apn = cr.getString(cr.getColumnIndex("apn"));
                            String type = cr.getString(cr
                                    .getColumnIndex("type"));
                            user = cr.getString(cr.getColumnIndex("user"));
                            // do other things...
                            String strProxy = cr.getString(cr
                                    .getColumnIndex("proxy"));
                            String strPort = cr.getString(cr
                                    .getColumnIndex("port"));
                            if (strProxy != null && !"".equals(strProxy)) {
                                // Config.host = strProxy;
                                // Config.port = Integer.valueOf(strPort);
                            }
                        }
                    }
                }
            }
        }
        if (user != null) {
            if (user.startsWith(CTWAP)) {
                return CTWAP;
            } else if (user.startsWith(CTNET)) {
                return CTNET;
            }
        } else {
            if (apn != null) {
                if (apn.startsWith(CMNET)) {
                    return CMNET;
                } else if (apn.startsWith(CMWAP)) {
                    return CMWAP;
                }
            }
        }
        return "";
    }

    public static  boolean isIP(String addr) {
        if (addr.length() < 7 || addr.length() > 15 || "".equals(addr)) {
            return false;
        }
        /**
         * 判断IP格式和范围
         */
//        String rexp = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";
        String rexp = "\\b((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\b";
        Pattern pat = Pattern.compile(rexp);
        Matcher mat = pat.matcher(addr);
        boolean ipAddress = mat.find();
        LogUtil.logW("isIP",addr + " is " + ipAddress);
        return ipAddress;
    }


    /**
     * 判断是否有网络连接  而非是internet连接
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
        } else { //如果仅仅是用来判断网络连接
            //则可以使用 cm.getActiveNetworkInfo().isAvailable();
            NetworkInfo[] info = cm.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
