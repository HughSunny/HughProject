package set.work.utils;

import android.annotation.TargetApi;
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
import android.net.Uri;
import android.os.Build;
import android.util.Log;

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






}
