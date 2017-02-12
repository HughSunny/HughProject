
package com.set.util;


import android.util.Log;

public final class NSLog {
	private static boolean s_debug = true;
	
	public static void setDebug(boolean debug) {
		s_debug = debug;
	}
	
	public static void printLine(String tag, String msg, boolean useNative) {
		if(s_debug) return;
		
		if(useNative) {
			Log.d(tag, msg);
		} else {
			System.out.println(msg);
		}
	}
}
