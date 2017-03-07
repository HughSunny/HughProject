package set.work.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Hugh on 2016/9/5.
 * Map实例
 */
public class MapUtil {

    /**
     * map是否是空
     * @param map
     * @return
     */
    public static boolean isEmpty(Map map) {
        if (map == null || map.size() == 0) {
            return true;
        }

        return false;
    }


    /**
     * 使用 Map按value进行排序
     * @param oriMap
     * @return
     */
    public static Map<String, String> sortMapByValue(Map<String, String> oriMap) {
        if (oriMap == null || oriMap.isEmpty()) {
            return null;
        }
        Map<String, String> sortedMap = new LinkedHashMap<String, String>();
        List<Map.Entry<String, String>> entryList = new ArrayList<Map.Entry<String, String>>(
                oriMap.entrySet());
        Collections.sort(entryList, new Comparator<Map.Entry<String, String>>() {
            @Override
            public int compare(Map.Entry<String, String> me1, Map.Entry<String, String> me2) {
                return me1.getValue().compareTo(me2.getValue());
            }
        });
        Iterator<Map.Entry<String, String>> iter = entryList.iterator();
        Map.Entry<String, String> tmpEntry = null;
        while (iter.hasNext()) {
            tmpEntry = iter.next();
            sortedMap.put(tmpEntry.getKey(), tmpEntry.getValue());
        }
        return sortedMap;
    }

    /**
     * 根据key排序
     * @param oriMap
     * @return
     */
    public static Map<String, Object> sortMapByKey(Map<String, Object> oriMap) {
        if (oriMap == null || oriMap.isEmpty()) {
            return null;
        }
        Map<String, Object> sortedMap = new TreeMap<String, Object>(new Comparator<String>() {
            public int compare(String key1, String key2) {
                return key1.compareTo(key2);
            }});
        sortedMap.putAll(oriMap);
        return sortedMap;
    }

}
