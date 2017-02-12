package com.set.util;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class ImageUtils {
	/**
	 * ��ô�Ӱ��ͼƬ����
	 * 
	 * @param bitmap
	 * @return
	 */
	public static Bitmap createReflectionImageWithOrigin(Bitmap bitmap) {

		final int reflectionGap = 4;

		int width = bitmap.getWidth();

		int height = bitmap.getHeight();

		Matrix matrix = new Matrix();

		matrix.preScale(1, -1);

		Bitmap reflectionImage = Bitmap.createBitmap(bitmap, 0, height / 2, width, height / 2, matrix, false);

		Bitmap bitmapWithReflection = Bitmap.createBitmap(width, (height + height / 2), Config.ARGB_8888);

		Canvas canvas = new Canvas(bitmapWithReflection);

		canvas.drawBitmap(bitmap, 0, 0, null);

		Paint deafalutPaint = new Paint();

		canvas.drawRect(0, height, width, height + reflectionGap,

		deafalutPaint);

		canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);

		Paint paint = new Paint();

		LinearGradient shader = new LinearGradient(0,

		bitmap.getHeight(), 0, bitmapWithReflection.getHeight() + reflectionGap, 0x70ffffff, 0x00ffffff, TileMode.CLAMP);

		paint.setShader(shader);

		// Set the Transfer mode to be porter duff and destination in

		paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));

		// Draw a rectangle using the paint with our linear gradient

		canvas.drawRect(0, height, width, bitmapWithReflection.getHeight() + reflectionGap, paint);

		return bitmapWithReflection;

	}

	/**
	 * bitmapת��Ϊdrawable
	 * 
	 * @param bitmap
	 * @return
	 */
	public static Drawable bitmapToDrawable(Bitmap bitmap) {
		BitmapDrawable bd = new BitmapDrawable(bitmap);
		return bd;
	}

	/**
	 * drawableת��Ϊbitmap
	 * 
	 * @param drawable
	 * @return
	 */
	public static Bitmap drawableToBitmap(Drawable drawable) {

		Bitmap bitmap = Bitmap.createBitmap(

		drawable.getIntrinsicWidth(),

		drawable.getIntrinsicHeight(),

		drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);

		Canvas canvas = new Canvas(bitmap);

		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());

		drawable.draw(canvas);

		return bitmap;

	}

	/**
	 * ͼƬ����
	 * 
	 * @param bitmap
	 * @param newWidth
	 * @param newHeight
	 * @return
	 */
	public static Bitmap zoomBitmap(Bitmap bitmap, float scaleWidht, float scaleHeight) {

		int width = bitmap.getWidth();

		int height = bitmap.getHeight();

		Matrix matrix = new Matrix();

		matrix.postScale(scaleWidht, scaleHeight);

		Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);

		return newbmp;
	}

	/**
	 * �޸�ͼƬalphaֵ
	 * @param sourceImg
	 * @param number
	 * @return
	 */
	public static Bitmap setAlphaByBitmap(Bitmap sourceImg, int number) {
		int[] argb = new int[sourceImg.getWidth() * sourceImg.getHeight()];
		sourceImg.getPixels(argb, 0, sourceImg.getWidth(), 0, 0, sourceImg.getWidth(), sourceImg.getHeight());// ���ͼƬ��ARGBֵ
		number = number * 255 / 100;
		for (int i = 0; i < argb.length; i++) {
			argb[i] = (number << 24) | (argb[i] & 0x00FFFFFF);// �޸����2λ��ֵ
		}
		sourceImg = Bitmap.createBitmap(argb, sourceImg.getWidth(), sourceImg.getHeight(), Config.ARGB_8888);
		return sourceImg;
	}

	/**
	 * �޸�ͼƬrgbֵ
	 * @param sourceImg
	 * @param Beta
	 * @return
	 */
	public static Bitmap effectBitmapBeta(Bitmap sourceImg, int Beta) {
		int srcW = sourceImg.getWidth();
		int srcH = sourceImg.getHeight();
		int[] srcPixels = new int[sourceImg.getWidth() * sourceImg.getHeight()];
		sourceImg.getPixels(srcPixels, 0, sourceImg.getWidth(), 0, 0, sourceImg.getWidth(), sourceImg.getHeight());// ���ͼƬ��ARGBֵ
		int r = 0;
		int g = 0;
		int b = 0;
		int a = 0;
		int argb;
		for (int i = 0; i < srcH; i++) {
			for (int ii = 0; ii < srcW; ii++) {
				argb = srcPixels[i * srcW + ii];
				a = ((argb & 0xff000000) >> 24); // alpha channel
				r = Beta + ((argb & 0x00ff0000) >> 16); // red channel
				g = Beta + ((argb & 0x0000ff00) >> 8); // green channel
				b = Beta + (argb & 0x000000ff); // blue channel

				r = r > 255 ? 255 : r;
				g = g > 255 ? 255 : g;
				b = b > 255 ? 255 : b;
				srcPixels[i * srcW + ii] = ((a << 24) | (r << 16) | (g << 8) | b);
			}
		}
		sourceImg = Bitmap.createBitmap(srcPixels, sourceImg.getWidth(), sourceImg.getHeight(), Config.ARGB_8888);
		return sourceImg;
	}
	  public static Bitmap getRoundedCornerBitmap(Bitmap bitmap,float roundPx){   
          
	        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap   
	                .getHeight(), Config.ARGB_8888);   
	        Canvas canvas = new Canvas(output);   
	    
	        final int color = 0xff424242;   
	        final Paint paint = new Paint();   
	        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());   
	        final RectF rectF = new RectF(rect);   
	    
	        paint.setAntiAlias(true);   
	        canvas.drawARGB(0, 0, 0, 0);   
	        paint.setColor(color);   
	        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);   
	    
	        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));   
	        canvas.drawBitmap(bitmap, rect, rect, paint);   
	    
	        return output;   
	    } 
	   public static Bitmap getGrayscale(Bitmap bmpOriginal) {
			int width, height;
			height = bmpOriginal.getHeight();
			width = bmpOriginal.getWidth();
	
			Bitmap bmpGrayscale = Bitmap.createBitmap(width, height,
					Bitmap.Config.RGB_565);
			Canvas c = new Canvas(bmpGrayscale);
			Paint paint = new Paint();
			ColorMatrix cm = new ColorMatrix();
			cm.setSaturation(0);
			ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
			paint.setColorFilter(f);
			c.drawBitmap(bmpOriginal, 0, 0, paint);
			return bmpGrayscale;
	}
		       
}
