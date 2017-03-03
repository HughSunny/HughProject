package set.work.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.util.DisplayMetrics;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

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
		getExtSDCardPath();
		getDevMountList();
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

	public static int getScreenWindowHeight() {
		return getScreenHeight() - getStatusBarHeight();
	}


	public static String getSDCardSavePath() {
		String savePath = null;
		A:
		if (getSDMounted()) {
			String  externalpath = Environment.getExternalStorageDirectory().getAbsolutePath();
			File extPathFile = new File(externalpath);

			if (extPathFile.exists() && extPathFile.isDirectory()) {
				if (!extPathFile.canWrite()) {
					LogUtil.logW(APP_NAME, externalpath  + " CAN NOT WRITE" );
					break A;
				}
			} else {
				LogUtil.logW(APP_NAME, externalpath  + " IS  NOT EXISTED" );
				break A;
			}
			savePath = Environment.getExternalStorageDirectory().getAbsolutePath()
					+ File.separator
					+ APP_NAME
					+ File.separator;
			File dir = new File(savePath);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			return savePath;
		}

		String path = null;
		File sdCardFile = null;
		ArrayList<String> devMountList = getDevMountList();
		for (String devMount : devMountList) {
			File file = new File(devMount);
			if (file.isDirectory() && file.canWrite()) {
				path = file.getAbsolutePath();
				String timeStamp = ConvertUtil.dateYMDHM.format(TimeUtil.getDate());
				File testWritable = new File(path, "test_" + timeStamp);
				if (testWritable.mkdirs()) {
					testWritable.delete();
				} else {
					path = null;
				}
			}
		}
		if (path != null) {
			savePath = path + File.separator + APP_NAME + File.separator;
			File dir = new File(savePath);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			return savePath;
		}
		return null;
	}

	/**
	 * 判断sd卡是否存在
	 * @return
     */
	private static boolean  getSDMounted() {
		return Environment.getExternalStorageState()
				.equals(Environment.MEDIA_MOUNTED);
	}


	/**
	 *
	 * 遍历 "system/etc/vold.fstab” 文件，获取全部的Android的挂载点信息
	 * @return
	 */
	private static ArrayList<String> getDevMountList() {
		File file = new File("/etc/vold.fstab");
		if (file.exists()) {
			String[] toSearch = FileUtil.getFileString(file).split(" ");
			ArrayList<String> out = new ArrayList<String>();
			for (int i = 0; i < toSearch.length; i++) {
				if (toSearch[i].contains("dev_mount")) {
					if (new File(toSearch[i + 2]).exists()) {
						out.add(toSearch[i + 2]);
					}
				}
			}
			return out;
		}
		return null;
	}


	/**
	 * 获取外置SD卡路径
	 * @return    应该就一条记录或空
	 */
	public List getExtSDCardPath() {
		List lResult = new ArrayList();
		try {
			Runtime rt = Runtime.getRuntime();
			Process proc = rt.exec("mount");
			InputStream is = proc.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line;
			while ((line = br.readLine()) != null) {
				if (line.contains("extSdCard"))
				{
					String [] arr = line.split(" ");
					String path = arr[1];
					File file = new File(path);
					if (file.isDirectory())
					{
						lResult.add(path);
					}
				}
			}
			isr.close();
		} catch (Exception e) {
		}
		return lResult;
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
			LogUtil.logW(APP_NAME,"get status bar height fail");
			e1.printStackTrace();
		}
//		LogUtil.logW(APP_NAME,"getStatusBarHeight + " + sbar);
		return sbar;
	}
}
