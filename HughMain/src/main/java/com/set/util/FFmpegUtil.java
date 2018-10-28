package com.set.util;

/**
 * Created by Hugh on 2017/5/26.
 */

public class FFmpegUtil {
    static {
        System.loadLibrary("avcodec-57");
        System.loadLibrary("avfilter-6");
        System.loadLibrary("avformat-57");
        System.loadLibrary("avutil-55");
        System.loadLibrary("swresample-2");
        System.loadLibrary("swscale-4");
        System.loadLibrary("ffmpegdemo");
    }
    public static native String getInfo();
}
