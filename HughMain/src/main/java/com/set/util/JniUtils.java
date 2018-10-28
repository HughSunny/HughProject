package com.set.util;

/**
 * Created by Hugh on 2017/5/24.
 */

public class JniUtils {
    static {
        System.loadLibrary("NdkJniDemo");//之前在build.gradle里面设置的so名字，必须一致
    }
    public static native String getStringFormC();
}
