package set.work.utils;

import java.util.Collection;

/**
 * Created by Hugh on 2017/2/7.
 */

public class CollectionUtil {
    /**
     * 判断是否是空集合
     * @param collection
     * @return
     */
    public static boolean isEmpty (Collection collection) {
        if (collection == null || collection.isEmpty()) {
            return true;
        }
        return false;
    }

    /**
     * 获取集合的大小
     * @param collection
     * @return
     */
    public static int getSize(Collection collection) {
        if (isEmpty(collection)) {
            return 0;
        }
        return collection.size();
    }
}
