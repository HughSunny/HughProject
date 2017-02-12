package com.set.ui;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.util.Log;

import com.set.util.PublicTool;

public class UIUtil {

	private static String TAG = "UIUtil";

	/**
	 * 清除canvas 主要用于SurfaceView
	 * 
	 * @param canvas
	 */
	public void clearCanvas(Canvas canvas) {
		Paint paint = new Paint();
		paint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
		canvas.drawPaint(paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC));
	}

	/**
	 * scaleType = 1, FillXY scaleType = 2, FillCenter
	 **/
	public static Bitmap getBitmap(String file, int areaWidth, int areaHeight,
			int scaleType) {
		Bitmap bitmap = null;
		bitmap = getLegalBitmap(file);

		if (1 == scaleType) {
			return getFillXYBitmap(bitmap, areaWidth, areaHeight);
		} else if (2 == scaleType) {
			return getFillCenterBitmap(bitmap, areaWidth, areaHeight);
		}
		return null;
	}

	private static synchronized Bitmap getLegalBitmap(String file) {
		Bitmap bitmap = null;
		boolean needDecode = true;
		String name = PublicTool.getFileName(file);
		// Log.i(TAG, "bitmapList SIZE == " + bitmapList.size());
		// for (int i = 0; i < bitmapList.size(); i++) {
		// BitmapNode bitmapNode = bitmapList.get(i);
		// if (bitmapNode.name.equals(name)) {
		// needDecode = false;
		// bitmap = bitmapNode.bitmap;
		// if (i > 0) {
		// bitmapList.set(i, bitmapList.get(i - 1));
		// bitmapList.set(i - 1, bitmapNode);
		// }
		// return bitmap;
		// }
		// }
		if (needDecode) {
			bitmap = decode2Bitmap(file);
			if (bitmap == null || name == null) {
				return bitmap;
			}
			// BitmapNode bitmapNode = new BitmapNode();
			// bitmapNode.bitmap = bitmap;
			// bitmapNode.name = name;
			//
			// bitmapList.add(0, bitmapNode);
			//
			// int size = bitmapList.size();
			// if (size >= 10) {
			// bitmapList.get(size - 1).bitmap.recycle();
			// bitmapList.remove(size - 1);
			// }
		}
		return bitmap;
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

}
