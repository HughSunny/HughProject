package com.set.util;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory.Options;
import android.util.Log;

public class PublicTool {
	private static final String TAG = "PublicTool";
	/** yyyy-MM-dd **/
	public static SimpleDateFormat dateYMD = new SimpleDateFormat("yyyy-MM-dd");
	/** yyyy-MM-dd HH:mm:ss **/
	public static SimpleDateFormat dateYMDHMS = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
	/** yyyy-MM-dd_HH:mm:ss **/
	public static SimpleDateFormat date_YMDHMS = new SimpleDateFormat(
			"yyyy-MM-dd_HH:mm:ss");
	
	/**
	 * scaleType = 1, FillXY scaleType = 2, FillCenter
	 * */
	public static Bitmap getBitmap(String file, int areaWidth, int areaHeight,
			int scaleType) {
		Bitmap bitmap = null;
		bitmap = decode2Bitmap(file);

		if (1 == scaleType) {
			return getFillXYBitmap(bitmap, areaWidth, areaHeight);
		} else if (2 == scaleType) {
			return getFillCenterBitmap(bitmap, areaWidth, areaHeight);
		}
		return null;
	}

	private static Bitmap getFillCenterBitmap(Bitmap bitmap, int areaWidth,
			int areaHeight) {
		if (bitmap == null) {
			return null;
		}

		int srcWidth = bitmap.getWidth();
		int srcHeight = bitmap.getHeight();
		Matrix matrix = new Matrix();
		float scale = 1.0f;
		float dx = 0.0f;
		float dy = 0.0f;
		float areaRate = 1.0f * areaWidth / areaHeight;
		float imgRate = 1.0f * srcWidth / srcHeight;

		if (areaRate < imgRate) {
			scale = (areaWidth * 1.0f) / srcWidth;
		} else {
			scale = (areaHeight * 1.0f) / srcHeight;
		}
		dx = (areaWidth - scale * srcWidth) / 2;
		dy = (areaHeight - scale * srcHeight) / 2;

		matrix.setScale(scale, scale);
		matrix.postTranslate(dx, dy);

		Bitmap retBitmap = Bitmap.createBitmap(areaWidth, areaHeight,
				Config.RGB_565);
		Canvas canvas = new Canvas(retBitmap);
		canvas.drawBitmap(bitmap, matrix, null);
		return retBitmap;
	}

	private static Bitmap getFillXYBitmap(Bitmap bitmap, int areaWidth,
			int areaHeight) {
		if (bitmap == null) {
			return null;
		}
		int srcWidth = bitmap.getWidth();
		int srcHeight = bitmap.getHeight();
		Matrix matix = new Matrix();
		float scaleX = (areaWidth * 1.0f) / srcWidth;
		float scaleY = (areaHeight * 1.0f) / srcHeight;
		matix.setScale(scaleX, scaleY);
		Log.d(TAG, "scaleX is:" + scaleX + " scaleY is:" + scaleY);
		Bitmap retBitmap = Bitmap.createBitmap(bitmap, 0, 0, srcWidth,
				srcHeight, matix, true);
		return retBitmap;
	}

	private static Bitmap decode2Bitmap(String file) {
		if (file == null) {
			return null;
		}
		Options opt = new Options();
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
		}
		opt.inJustDecodeBounds = false;
		opt.inMutable = true;
		return BitmapFactory.decodeFile(file, opt);
	}

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
			// 这次再真正地生成一个有像素的，经过缩放了的bitmap
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
					android.graphics.Bitmap.Config.RGB_565);
			Canvas canvas = new Canvas(retBitmap);
			canvas.drawBitmap(bitmap, matrix, null);
			bitmap.recycle();
			return retBitmap;
		}
		return null;
	}

	public static String getHexString(byte[] bytes) {
        String ret = null;
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            int value = (int)bytes[i] & 0xff;
            if (value < 0x10) {
                sb.append('0');
            }
            sb.append(Integer.toHexString(value));
        }
        ret = sb.substring(0, sb.length());
        return ret;
    }
	
	public static float FormetFloat(float text) {
		DecimalFormat fmt = new DecimalFormat("0.##");
		String aa = fmt.format(text);
		return Float.parseFloat(aa);
	}

}
