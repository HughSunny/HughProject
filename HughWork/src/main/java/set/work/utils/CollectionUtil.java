package set.work.utils;

import java.util.Collection;

/**
 * Created by Hugh on 2017/2/7.
 */

public class CollectionUtil {

    public static boolean isEmpty (Collection collection) {
        if (collection == null || collection.isEmpty()) {
            return true;
        }
        return false;
    }
}
