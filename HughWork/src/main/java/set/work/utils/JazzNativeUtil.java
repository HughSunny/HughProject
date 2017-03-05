package set.work.utils;

/**
 * Created by Hugh on 2017/3/5.
 */

public class JazzNativeUtil {


    /**
     * 获取0xfF
     * @param bytes
     * @return
     */
    public static String getHexString(byte[] bytes) {
        String ret = null;
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            int value = (int)bytes[i] & 0xff;
            if (value < 0x10) {
                sb.append('0');
            }
            sb.append(Integer.toHexString(value));
        }
        ret = sb.substring(0, sb.length());
        return ret;
    }

    public static byte getByte(String s) {
        char ch0 = s.charAt(0);
        char ch1 = s.charAt(1);
        byte result = 0;
        if (ch0 > '9') {
            result = (byte) ((ch0 - 'A' + 10) * 16);
        } else {
            result = (byte) ((ch0 - '0') * 16);
        }

        if (ch1 > '9') {
            result += (ch1 - 'A' + 10);
        } else {
            result += (ch1 - '0');
        }

        result = (byte) (result & 0xff);
        return result;
    }

    private static final byte ASCII_0 = 0x30;
    private static final byte ASCII_9 = 0x39;
    private static final byte ASCII_A = 0x41;
    private static final byte ASCII_Z = 0x5A;
    private static final byte ASCII_a = 0x61;
    private static final byte ASCII_z = 0x71;
    public static boolean isAllABC123(byte[] src, int length) {
        if (src == null || src.length < length) {
            return false;
        }

        for (int i = 0; i < length; i++) {
            if ((src[i] <= ASCII_9 && src[i] >= ASCII_0)
                    || (src[i] <= ASCII_Z && src[i] >= ASCII_A)
                    || (src[i] <= ASCII_z && src[i] >= ASCII_a)) {
                continue;
            }
            return false;
        }
        return true;
    }

    public static boolean isAll123(byte[] src, int length) {
        if (src == null || src.length < length) {
            return false;
        }
        for (int i = 0; i < length; i++) {
            if (src[i] <= ASCII_9 && src[i] >= ASCII_0) {
                continue;
            }
            return false;
        }
        return true;
    }

}
