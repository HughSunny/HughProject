package set.work.utils;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PublicTools {
	
	private static final String TAG = "PublicTools";
	/**
	 *
	 * @param context
	 * @param downloadUrl
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static long downloadApk(Context context, String downloadUrl, String downloadPath, String fileName, String decs) {
		final DownloadManager dManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
		Uri uri = Uri.parse(downloadUrl);
		Request request = new Request(uri);
		// 设置下载路径和文件名
		request.setDestinationInExternalPublicDir(downloadPath, fileName);
		request.setDescription(decs); //"软件新版本下载"
		request.setNotificationVisibility(Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
		request.setMimeType("application/vnd.android.package-archive");
		// 下载时，通知栏显示途中
		request.setNotificationVisibility(Request.VISIBILITY_VISIBLE);
		// 设置为可被媒体扫描器找到
		request.allowScanningByMediaScanner();
		// 设置为可见和可管理
		request.setVisibleInDownloadsUi(true);
		// 获取此次下载的ID
		long reference = dManager.enqueue(request);
		return reference;

	}

	private static String sysDir = null;
	public static float percent2f(String percent) {
		float ret = 0f;
		if (percent == null || percent.length() < 2) {
			return ret;
		}
		ret = Float.parseFloat(percent.substring(0, percent.length() - 1));
		return (ret / 100);
	}

	/**
	 * selector 定义
	 * @param context
	 * @param idNormal
	 * @param idPressed
	 * @param idFocused
     * @return
     */
	private StateListDrawable addStateDrawable(Context context, int idNormal, int idPressed, int idFocused) {
		StateListDrawable sd = new StateListDrawable();
		Drawable normal = idNormal == -1 ? null : context.getResources().getDrawable(idNormal);
		Drawable pressed = idPressed == -1 ? null : context.getResources().getDrawable(idPressed);
		Drawable focus = idFocused == -1 ? null : context.getResources().getDrawable(idFocused);
		//注意该处的顺序，只要有一个状态与之相配，背景就会被换掉
		//所以不要把大范围放在前面了，如果sd.addState(new[]{},normal)放在第一个的话，就没有什么效果了
		sd.addState(new int[]{android.R.attr.state_enabled, android.R.attr.state_focused}, focus);
		sd.addState(new int[]{android.R.attr.state_pressed, android.R.attr.state_enabled}, pressed);
		sd.addState(new int[]{android.R.attr.state_focused}, focus);
		sd.addState(new int[]{android.R.attr.state_pressed}, pressed);
		sd.addState(new int[]{android.R.attr.state_enabled}, normal);
		sd.addState(new int[]{}, normal);
		return sd;
	}

	
	//------------------------图片处理-----------------------------------
	public static Bitmap getFillCenterBitmap(String file, int areaWidth,
			int areaHeight) {
		Bitmap bitmap = null;
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(file, opt);
		int imageHeight = opt.outWidth;
		int imageWidth = opt.outHeight;
		if (imageHeight >= 768 || imageWidth >= 1366) {
			float scale = 1.0f;
			float areaRate = 1.0f * 1366 / 768;
			float imgRate = 1.0f * imageWidth / imageHeight;
			if (areaRate < imgRate) {
				scale = (1366 * 1.0f) / imageWidth;
			} else {
				scale = (768 * 1.0f) / imageHeight;
			}
			opt.inSampleSize = (int) (1 / scale);
			opt.inJustDecodeBounds = false;
			bitmap = BitmapFactory.decodeFile(file, opt);
		} else {
			bitmap = BitmapFactory.decodeFile(file);
		}
		if (bitmap != null) {
			imageHeight = bitmap.getHeight();
			imageWidth = bitmap.getWidth();
			if (imageHeight == areaHeight && imageWidth == areaWidth) {
				return bitmap;
			}
			Matrix matrix = new Matrix();
			float scale = 1.0f;
			float dx = 0.0f;
			float dy = 0.0f;
			float areaRate = 1.0f * areaWidth / areaHeight;
			float imgRate = 1.0f * imageWidth / imageHeight;

			if (areaRate < imgRate) {
				scale = (areaWidth * 1.0f) / imageWidth;
			} else {
				scale = (areaHeight * 1.0f) / imageHeight;
			}
			dx = (areaWidth - scale * imageWidth) / 2;
			dy = (areaHeight - scale * imageHeight) / 2;

			matrix.setScale(scale, scale);
			matrix.postTranslate(dx, dy);

			Bitmap retBitmap = Bitmap.createBitmap(areaWidth, areaHeight,
					Config.RGB_565);
			Canvas canvas = new Canvas(retBitmap);
			canvas.drawBitmap(bitmap, matrix, null);
			bitmap.recycle();
			return retBitmap;
		}
		return null;
	}

	public static Bitmap getFillCenterRawBitmap(String file, int areaWidth,
			int areaHeight) {
		Bitmap bitmap = null;
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(file, opt);
		int imageHeight = opt.outWidth;
		int imageWidth = opt.outHeight;
		if (imageHeight >= 768 || imageWidth >= 1366) {
			float scale = 1.0f;
			float areaRate = 1.0f * 1366 / 768;
			float imgRate = 1.0f * imageWidth / imageHeight;
			if (areaRate < imgRate) {
				scale = (1366 * 1.0f) / imageWidth;
			} else {
				scale = (768 * 1.0f) / imageHeight;
			}
			opt.inSampleSize = (int) (1 / scale);
			opt.inJustDecodeBounds = false;
			bitmap = BitmapFactory.decodeFile(file, opt);
		} else {
			bitmap = BitmapFactory.decodeFile(file);
		}
		if (bitmap != null) {
			imageHeight = bitmap.getHeight();
			imageWidth = bitmap.getWidth();
			if (imageHeight == areaHeight && imageWidth == areaWidth) {
				return bitmap;
			}
			Matrix matrix = new Matrix();
			float scale = 1.0f;
			float areaRate = 1.0f * areaWidth / areaHeight;
			float imgRate = 1.0f * imageWidth / imageHeight;

			if (areaRate < imgRate) {
				scale = (areaWidth * 1.0f) / imageWidth;
			} else {
				scale = (areaHeight * 1.0f) / imageHeight;
			}
			matrix.setScale(scale, scale);
			Bitmap retBitmap = Bitmap.createBitmap((int) (scale * imageWidth),
					(int) (scale * imageHeight), Config.RGB_565);
			Canvas canvas = new Canvas(retBitmap);
			canvas.drawBitmap(bitmap, matrix, null);
			bitmap.recycle();
			return retBitmap;
		}
		return null;
	}

	public static Bitmap getFillXYBitmap(String file, int areaWidth,
			int areaHeight) {
		Bitmap bitmap = BitmapFactory.decodeFile(file);
		if (bitmap != null) {
			int imageHeight = bitmap.getHeight();
			int imageWidth = bitmap.getWidth();
			if (imageHeight == areaHeight && imageWidth == areaWidth) {
				return bitmap;
			}
			Matrix matix = new Matrix();
			float scaleX = (areaWidth * 1.0f) / imageWidth;
			float scaleY = (areaHeight * 1.0f) / imageHeight;
			matix.setScale(scaleX, scaleY);
			Log.d(TAG, "scaleX is:" + scaleX + " scaleY is:" + scaleY);
			Bitmap retBitmap = Bitmap.createBitmap(bitmap, 0, 0, imageWidth,
					imageHeight, matix, true);
			bitmap.recycle();
			return retBitmap;
		}
		return null;
	}

	/**
	 * scaleType = 1, FillXY scaleType = 2, FillCenter
	 * */
	public static Bitmap getBitmap(String file, int areaWidth, int areaHeight,
			int scaleType) {
		if (1 == scaleType) {
			return getFillXYBitmap(file, areaWidth, areaHeight);
		} else if (2 == scaleType) {
			return getFillCenterBitmap(file, areaWidth, areaHeight);
		}
		return null;
	}

	public static Bitmap createVideoThumbnali(String file, int width, int height) {
		Bitmap bitmap = null;
		MediaPlayer mp = new MediaPlayer();
		try {
			mp.setDataSource(file);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		int i = mp.getDuration();
		mp.release();
		// MediaMetadataRetriever retriver = new MediaMetadataRetriever();
		// retriver.setDataSource(file);
		// bitmap = retriver.getFrameAtTime(i);
		// retriver.release();
		if (bitmap != null) {
			int imageHeight = bitmap.getHeight();
			int imageWidth = bitmap.getWidth();
			if (imageHeight == height && imageWidth == width) {
				return bitmap;
			}
			Matrix matix = new Matrix();
			float scaleX = (width * 1.0f) / imageWidth;
			float scaleY = (height * 1.0f) / imageHeight;
			matix.setScale(scaleX, scaleY);
			Bitmap retBitmap = Bitmap.createBitmap(bitmap, 0, 0, imageWidth,
					imageHeight, matix, true);
			bitmap.recycle();
			return retBitmap;
		}
		return bitmap;
	}

	public static int findFrontCamera() {
		int cameraCount = 0;
		Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
		cameraCount = Camera.getNumberOfCameras(); // get cameras number

		for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
			Camera.getCameraInfo(camIdx, cameraInfo); // get camerainfo
			if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
				return camIdx;
			}
		}
		return -1;
	}

	public static int findBackCamera() {
		int cameraCount = 0;
		Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
		cameraCount = Camera.getNumberOfCameras(); // get cameras number

		for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
			Camera.getCameraInfo(camIdx, cameraInfo); // get camerainfo
			if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
				return camIdx;
			}
		}
		return -1;
	}

	public static boolean isServiceRunning(Context mContext, String className) {
		boolean IsRunning = false;
		ActivityManager activityManager = (ActivityManager) mContext
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> serviceList = activityManager
				.getRunningServices(30);
		if (!(serviceList.size() > 0)) {
			return false;
		}
		for (int i = 0; i < serviceList.size(); i++) {
			if (serviceList.get(i).service.getClassName().contains(className) == true) {
				IsRunning = true;
				break;
			}
		}
		return IsRunning;
	}


	public static FileOutputStream getFileOutputStream(File file) {
		FileOutputStream stream = null;
		if (file == null) {
			Log.i(TAG, "getFileOutputStream stream is " + stream);
			return stream;
		} else {
			try {
				stream = new FileOutputStream(file);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		return stream;
	}

	public static FileInputStream getFileInputStream(File file) {
		FileInputStream stream = null;
		if (file == null) {
			Log.i(TAG, "getFileInputStream stream is " + stream);
			return stream;
		} else {
			try {
				stream = new FileInputStream(file);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		return stream;
	}



	/**
	 * 鍐檓ap鏁版嵁
	 *
	 * @param outputStream
	 * @param map
	 */
	public static void write(FileOutputStream outputStream,
			Map<String, String> map) {
		if (outputStream == null || map == null) {
			Log.i(TAG, "write outputStream is " + outputStream);
			Log.i(TAG, "write map is " + map);
			return;
		}
		try {
			XmlSerializer serializer = Xml.newSerializer();
			serializer.setOutput(outputStream, "UTF-8");
			serializer.startDocument("UTF-8", null);
			String tag2 = null;
			for (Iterator<String> it = map.keySet().iterator(); it.hasNext();) {
				String key = it.next();
				String value = map.get(key);
				if (value == null) {
					serializer.startTag(null, key);
					tag2 = key;
				} else {
					serializer.attribute(null, key, value);
				}
			}
			serializer.endTag(null, tag2);
			for (Iterator<String> it = map.keySet().iterator(); it.hasNext();) {
				String key = it.next();
				String value = map.get(key);
				if (value == null && key != tag2) {
					serializer.endTag(null, key);
				}
			}
			serializer.endDocument();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (outputStream != null) {
					outputStream.flush();
					outputStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 获取文件的名字
	 *
	 * @param file
	 * @return
	 */
	public static String getFileName(File file) {
		String name = null;
		if (file == null || !file.exists()) {
			Log.i(TAG, "getFileName file is " + file + " or not exists");
			return name;
		}
		String fileName = file.getName();
		if (fileName == null) {
			return name;
		}
		int last = fileName.lastIndexOf('.');
		if (last == -1) {
			return name;
		}
		return fileName.substring(0, last);
		// if (fileName != null && fileName.length() >= 4) {
		// name = fileName.substring(0, fileName.length() - 4);
		// }
		// Log.d(TAG, "getFileName name is" + name);
		// return name;
	}
	
	
	private static final byte ASCII_0 = 0x30;
	private static final byte ASCII_9 = 0x39;
	private static final byte ASCII_A = 0x41;
	private static final byte ASCII_Z = 0x5A;
	private static final byte ASCII_a = 0x61;
	private static final byte ASCII_z = 0x71;
	public static boolean isAllABC123(byte[] src, int length) {
		if (src == null || src.length < length) {
			return false;
		}

		for (int i = 0; i < length; i++) {
			if ((src[i] <= ASCII_9 && src[i] >= ASCII_0)
					|| (src[i] <= ASCII_Z && src[i] >= ASCII_A)
					|| (src[i] <= ASCII_z && src[i] >= ASCII_a)) {
				continue;
			}
			return false;
		}
		return true;
	}

	public static boolean isAll123(byte[] src, int length) {
		if (src == null || src.length < length) {
			return false;
		}
		for (int i = 0; i < length; i++) {
			if (src[i] <= ASCII_9 && src[i] >= ASCII_0) {
				continue;
			}
			return false;
		}
		return true;
	}


	public static byte[] getBytes(String text) {
		if (text.length() < 2) {
			return new byte[8];
		}
		byte[] oldPwdByte = new byte[text.length() / 2];
		for (int i = 0; i < text.length(); i = i + 2) {
			// oldPwdByte[(i+1)/2] =
			// getByte(text.substring(i,i+2).toUpperCase());
			oldPwdByte[(i + 1) / 2] = (byte) (Integer.parseInt(
					text.substring(i, i + 2), 16) & 0xff);
			// oldPwdByte[(i+1)/2] = Byte.parseByte(text.substring(i,i+2), 16);
		}
		return oldPwdByte;
	}

	public static byte getByte(String s) {
		char ch0 = s.charAt(0);
		char ch1 = s.charAt(1);
		byte result = 0;
		if (ch0 > '9') {
			result = (byte) ((ch0 - 'A' + 10) * 16);
		} else {
			result = (byte) ((ch0 - '0') * 16);
		}

		if (ch1 > '9') {
			result += (ch1 - 'A' + 10);
		} else {
			result += (ch1 - '0');
		}

		result = (byte) (result & 0xff);
		return result;
	}



}
