package set.work.utils;

import java.util.UUID;

/**
 * Created by Hugh on 2016/7/12.
 */
public class UUidUtil {

    private static byte[] toUUIDByteArray(String u) {
        UUID uuid = null;
        if (u == null) {
            uuid = UUID.randomUUID();
        } else {
            uuid = UUID.fromString(u);
        }
        long msb = uuid.getMostSignificantBits();
        long lsb = uuid.getLeastSignificantBits();
        byte[] buffer = new byte[16];
        for (int i = 0; i < 8; i++) {
            buffer[i] = (byte) ((msb >>> 8 * (7 - i)) & 0xFF);
            buffer[i + 8] = (byte) ((lsb >>> 8 * (7 - i)) & 0xFF);
        }
        return buffer;
    }

    public static String getGuid(String u) {
        char[] res =  Base64Util.encode(toUUIDByteArray(u));
        //        System.out.println(new String(res));
        return new String(res, 0, res.length - 2);
    }
}
