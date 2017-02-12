package com.hugh.work.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;

/**
 * 计算公式 pixels = dips * (density / 160)
 * @author
 */
public class DensityUtil {

	private static final String TAG = DensityUtil.class.getSimpleName();
	// 当前屏幕的densityDpi
	private static float dmDensityDpi = 0.0f;
	private static DisplayMetrics dm;
	private static float scale = 0.0f;
	private static float  scaledDensity;
	/**
	 * 
	 * 根据构造函数获得当前手机的屏幕系数
	 * 
	 * */
	public DensityUtil(Context context) {
		// 获取当前屏幕
		dm = new DisplayMetrics();
		dm = context.getApplicationContext().getResources().getDisplayMetrics();
		// 设置DensityDpi
		setDmDensityDpi(dm.densityDpi);
		// 密度因子
		scale = getDmDensityDpi() / 160;
		scaledDensity = context.getResources().getDisplayMetrics().scaledDensity; 
		Log.e(TAG, "scale == " + scale + " ; dmDensityDpi = " +dmDensityDpi );
	}

	/**
	 * 当前屏幕的density因子
	 * 
	 * @retrun dmDensityDpi
	 * */
	public static float getDmDensityDpi() {
		return dmDensityDpi;
	}

	/**
	 * 当前屏幕的density因子
	 * 
	 * @param dmDensityDpi
	 * @retrun DmDensity Setter
	 * */
	public static void setDmDensityDpi(float dmDensityDpi) {
		DensityUtil.dmDensityDpi = dmDensityDpi;
	}

	/**
	 * 密度转换像素
	 * */
	public static int dip2px(float dipValue) {
		return (int) (dipValue * scale + 0.5f);
	}

	/**
	 * Convert Dp to Pixel
	 */
	public static int dpToPx(float dp, Resources resources){
		float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics());
		return (int) px;
	}

	public static int pxConvert(int typeValue,int value,Resources resources) {
		float px = TypedValue.applyDimension(typeValue, value, resources.getDisplayMetrics());
		return (int) px;
	}

	/**
	 * 像素转换密度
	 * */
	public static int px2dip(float pxValue) {
		return (int) (pxValue / scale + 0.5f);
	}

	public static int px2sp(float spValue) {
		return (int) (spValue / scaledDensity);
	}

	public static int sp2px(float spValue) { 
        return (int) (spValue * scaledDensity + 0.5f); 
    }

	@Override
	public String toString() {
		return " dmDensityDpi:" + dmDensityDpi;
	}
}
