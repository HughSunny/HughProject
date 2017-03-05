package set.work.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Gson类库的封装工具类，专门负责解析json数据</br>
 * 内部实现了Gson对象的单例
 * Created by Hugh on 2016/6/3.
 *
 * 作为方法传入参数，Java编译过程会将泛型对象类型擦除。
 */
public class GsonUtil {
    private static final String TAG = "GsonUtil";
    private static Gson gson = null;
    private static String dateFormat;
    static {
        if (gson == null) {
            gson = new Gson();
        }
    }

    private GsonUtil() {

    }


    public static void initDateGson( String _dateformat) {
        dateFormat = _dateformat;
        gson = new GsonBuilder().setDateFormat(dateFormat).create();
    }

    /**
     * 将对象转换成json格式
     *
     * @param ts
     * @return
     */
    public static String objectToJson(Object ts) {
        String jsonStr = null;
        if (gson != null) {
            jsonStr = gson.toJson(ts);
        }
        return jsonStr;
    }

    /**
     * 将json转换成bean对象
     *
     * 作为方法传入参数，Java编译过程会将泛型对象类型擦除。
     *
     * 不能用
     * @param jsonStr
     * @return
     */
    public static <T> T jsonToBean(String jsonStr) {
        T obj = null;
        if (gson != null) {
            obj = gson.fromJson(jsonStr, new TypeToken<T>() {}.getType());
        }
        return obj;
    }


    /**
     * 文件转化成 实体类
     *
     * 作为方法传入参数，Java编译过程会将泛型对象类型擦除。
     *
     * 不能用
     * @param file
     * @param <T>
     * @return
     */
    public static <T> T file2Bean(File file, Class<T> type) {
        if (file == null || !file.exists()) {
            return null;
        }
        T obj = null;
        Reader reader = null;
        try {
            reader = new FileReader(file);
            obj = gson.fromJson(reader, type);
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                    reader = null;
                } catch (Exception e){
                    LogUtil.logE(TAG, "FILE READER CLOSE EXCEPTION");
                    e.printStackTrace();
                }
            }
        }
        return obj;
    }
    /**
     * 将对象转换成json格式(并自定义日期格式)
     *
     * @param ts
     * @return
     */
    public static String objectToJsonDateSerializer(Object ts,
                                                    final String dateformat) {
        String jsonStr = null;
        gson = new GsonBuilder()
                .registerTypeHierarchyAdapter(Date.class,
                        new JsonSerializer<Date>() {
                            public JsonElement serialize(Date src,
                                                         Type typeOfSrc,
                                                         JsonSerializationContext context) {
                                SimpleDateFormat format = new SimpleDateFormat(
                                        dateformat);
                                return new JsonPrimitive(format.format(src));
                            }
                        }).setDateFormat(dateformat).create();
        if (gson != null) {
            jsonStr = gson.toJson(ts);
        }
        return jsonStr;
    }

    /**
     * 将json格式转换成list对象
     * 废用
     * @param jsonStr
     * @return
     */
    public static <T> List<T> jsonToList(String jsonStr) {
        List<T> objList = null;
        if (gson != null) {
            Type type = new com.google.gson.reflect.TypeToken<List<T>>() {
            }.getType();
            objList = gson.fromJson(jsonStr, type);
        }
        return objList;
    }

    /**
     * 将json格式转换成list对象，并准确指定类型
     *
     * @param jsonStr
     * @param type
     * @return
     */
    public static List<?> jsonToList(String jsonStr, Type type) {
        List<?> objList = null;
        if (gson != null) {
            objList = gson.fromJson(jsonStr, type);
        }
        return objList;
    }


    /**
     * 将json转换成bean对象
     *
     * @param jsonStr
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T jsonToBeanDateSerializer(String jsonStr, final String pattern) {
        T obj = null;
        gson = new GsonBuilder()
                .registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                    public Date deserialize(JsonElement json, Type typeOfT,
                                            JsonDeserializationContext context)
                            throws JsonParseException {
                        SimpleDateFormat format = new SimpleDateFormat(pattern);
                        String dateStr = json.getAsString();
                        try {
                            return format.parse(dateStr);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                }).setDateFormat(pattern).create();
        if (gson != null) {
            obj = gson.fromJson(jsonStr, new TypeToken<T>() {}.getType());
        }
        return  obj;
    }

    public static <T> T jsonToBean_Date(String jsonStr, String pattern) {
        T obj = null;
        gson = new GsonBuilder().setDateFormat(pattern).create();
        if (gson != null) {
            obj = gson.fromJson(jsonStr, new TypeToken<T>() {}.getType());
        }
        return obj;
    }


    /**
     * 根据
     *
     * @param jsonStr
     * @param key
     * @return
     */
    public static Object getJsonValue(String jsonStr, String key) {
        Object rulsObj = null;
        Map<?, ?> rulsMap = jsonToMap(jsonStr);
        if (rulsMap != null && rulsMap.size() > 0) {
            rulsObj = rulsMap.get(key);
        }
        return rulsObj;
    }

    /**
     * 将json格式转换成map对象
     *
     * @param jsonStr
     * @return
     */
    public static Map<?, ?> jsonToMap(String jsonStr) {
        Map<?, ?> objMap = null;
        if (gson != null) {
            Type type = new com.google.gson.reflect.TypeToken<Map<?, ?>>() {
            }.getType();
            objMap = gson.fromJson(jsonStr, type);
        }
        return objMap;
    }

}
