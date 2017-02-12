package com.hugh.work.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.util.DisplayMetrics;

import java.io.File;
import java.lang.reflect.Field;

/**
 * 设备信息 
 * 包括设备屏幕密度等
 * @author Hugh
 *
 */
public class EquipmentInfo {
	private static String APP_NAME = "App";
	private static Context context;
	public EquipmentInfo (Context _context ,String appName) {
		context = _context;
		APP_NAME = appName;
		DensityUtil density = new DensityUtil(context);
	}
	public static int getScreenWidth() {
		if(context == null) {
			return 0;
		}
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		return dm.widthPixels;
	}

	public static int getScreenHeight() {
		DisplayMetrics dm =  context.getResources().getDisplayMetrics();
		return dm.heightPixels;
	}

	public static String getSDCardSavePath() {
		String savePath;
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			savePath = context.getFilesDir().getAbsolutePath() + File.separator
					+ APP_NAME + File.separator;
		} else {
			savePath = Environment.getExternalStorageDirectory()
					.getAbsolutePath()
					+ File.separator
					+ APP_NAME
					+ File.separator;
		}
		File dir = new File(savePath);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		return savePath;
	}

	public static String getMobileInfo() {
		StringBuffer sb = new StringBuffer();
		try {

			Field[] fields = Build.class.getDeclaredFields();
			for (Field field : fields) {
				field.setAccessible(true);
				String name = field.getName();
				String value = field.get(null).toString();
				sb.append(name + "=" + value);
				sb.append("\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	public static String getVersionInfo(Context context) {
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo info = pm.getPackageInfo(context.getPackageName(), 0);
			return info.versionName;
		} catch (Exception e) {
			LogUtil.Debug(null, "获取系统版本号出错：" + e.toString());
		}
		return null;
	}

	public static boolean isTablet(Context context) {
		return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
	}

	/**
	 * 获取可用硬盘大小
	 * @return
	 */
	public static long getAvailableStorage() {// /(1024 * 1024 * 1024)
		File path = Environment.getExternalStorageDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		long sdAvail = availableBlocks * blockSize;
		return sdAvail;
	}

	/**
	 * 获取硬盘大小
	 * @return
	 */
	public static float getStorageSize() {
		File path = Environment.getExternalStorageDirectory();
		StatFs stat = new StatFs(path.getPath());
		float blockSize = stat.getBlockSize();
		float totalBlocks = stat.getBlockCount();
		float sdSize = totalBlocks * blockSize;
		return sdSize / 1024 / 1024 / 1024;
	}

	/**
	 * 获取可用内存
	 * @return
     */
	public static long getAvailableInnerMemory() {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		long totalBlocks = stat.getBlockCount();
		return blockSize * availableBlocks;
	}

	public static int getStatusBarHeight() {
		Class<?> c = null;
		Object obj = null;
		Field field = null;
		int x = 0, sbar = 0;
		try {
			c = Class.forName("com.android.internal.R$dimen");
			obj = c.newInstance();
			field = c.getField("status_bar_height");
			x = Integer.parseInt(field.get(obj).toString());
			sbar = context.getResources().getDimensionPixelSize(x);
		} catch(Exception e1) {
//			LogUtil.logW(APP_NAME,"get status bar height fail");
			e1.printStackTrace();
		}
//		LogUtil.logW(APP_NAME,"getStatusBarHeight + " + sbar);
		return sbar;
	}
}
