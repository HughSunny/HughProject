package com.hugh.work.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Hugh on 2016/8/10.
 */
public class ClazzUtil {
    public static void setFieldValue(String fieldName, Object clazzObj, String value) {
        Class clazz = clazzObj.getClass();
        String getMethodName = parseMethodName(fieldName, "set");
        // 获得和属性对应的getXXX()方法
        Method getMethod = null;
        try {
            getMethod = clazz.getMethod(getMethodName, new Class[]{});
            // 调用原对象的getXXX()方法
            Object ret = getMethod.invoke(clazzObj, new Object[]{value});
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private void getFieldValue(Object clazzObj, String fieldName) {
        Class clazz = clazzObj.getClass();
        String getMethodName = parseMethodName(fieldName, "get");
        Method getMethod = null;
        try {
            getMethod = clazz.getMethod(getMethodName, new Class[]{});
            // 调用原对象的getXXX()方法
            Object value = getMethod.invoke(clazzObj, new Object[]{});
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * 拼接某属性的 get或者set方法
     *
     * @param fieldName
     *            字段名称
     * @param methodType
     *            方法类型
     * @return 方法名称
     */
    public static String parseMethodName(String fieldName, String methodType) {
        if (null == fieldName || "".equals(fieldName)) {
            return null;
        }
        return methodType + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
    }

}
