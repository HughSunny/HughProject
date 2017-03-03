package set.work.utils;

import android.util.Log;

import set.work.app.BaseApp;

/**
 * android 日志工具类
 */
public class LogUtil {
	public static void Debug(Object object, String message) {
		LoggerFileTool logger = new LoggerFileTool(object);
		logger.Debug(message);
	}

	public static void Info(Object object, String message) {
		LoggerFileTool logger = new LoggerFileTool(object);
		logger.Info(message);
	}

	public static void Error(Object object, String message) {
		if (message == null) {
			message = "exception";
		}
		if (object instanceof  String) {
			logE((String)object, message);
		} else {
			logE(object.getClass().getSimpleName(), message);
		}
		LoggerFileTool logger = new LoggerFileTool(object);
		logger.Error(message);
	}
	
	
	public static void logW(String tag, String msg){
		if (BaseApp.Debug) {
			Log.w(tag, msg);
		}
	}
	public static void logE(String tag, String msg){
		if (BaseApp.Debug) {
			Log.e(tag, msg);
		}
	}
	public static void logI(String tag, String msg){
		if (BaseApp.Debug) {
			Log.i(tag, msg);
		}
	}
}
