package com.set.util;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

public class Assist {
	
	public static void addKeyAndValue(Hashtable map, Object key, Object value) {
		   map.put(key, value);
	}
	public static void removeByKey(Hashtable map, Object key) {
		   map.remove(key);
	}

	public static void addKeyAndValue(Hashtable map, Object key, int value) {
		   map.put(key, value);
	}
	
	public static void addKeyAndValue(Hashtable map, int key, int value) {
		   map.put(key, value);
	}
	
	public static Object getValueByKey(Hashtable map, Object key) {
		return map.get(key);
	}
	
	public static Object getValueByKey(Hashtable map, int key) {
		return map.get(key);
	}
	
	public static boolean add(Vector targetVector, Object value) {
		return targetVector.add(value);
	}
	
	public static boolean addAll(Vector targetVector, Vector sourceVector) {
		if (targetVector == null || sourceVector == null) {
			return false;
		}
		return targetVector.addAll(sourceVector);
	}
	
	/**
	 * �б����������Ƿ���ͬһ��, 20110101
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static boolean isSameWeek(int date1, int date2) {
		Calendar ca = Calendar.getInstance();
		ca.setTime(new Date(date1/10000, date1%10000/100, date1%100));
		int dayOfWeek1 = ca.get(Calendar.DAY_OF_WEEK);
		long millis1 = ca.getTimeInMillis();
		
		ca.setTime(new Date(date2/10000, date2%10000/100, date2%100));
		long millis2 = ca.getTimeInMillis();
		
		long offset1 = (dayOfWeek1) * 24 * 3600 *  1000;
		long offset2 = (7 - dayOfWeek1) * 24 * 3600 *  1000;
		
		long offset = Math.abs(millis2 - millis1);

		return offset <= offset1 || offset <= offset2; 
	}
	public static String getCurDate(String msg) {
		//"yyyyMMdd HHmmss"
		SimpleDateFormat df = new SimpleDateFormat(msg);
		return df.format(Calendar.getInstance().getTime());
	
	}
	public static String replace(String src, String target, String replace) {
		if (src == null || target == null || replace == null) {
			return src;
		}
		
		return src.replace(target, replace);
	}
	
	public static String replace(String src, char target, char replace) {
		return src.replace(target, replace);
	}
	
	public static boolean equalsIgnoreCase(String str1, String str2){
		if((str1 == null)&&(str2 == null)){
			return true;
		}else if((str1 == null && str2 != null)||(str2 == null && str1 != null)){
			return false;
		}
		return str1.equalsIgnoreCase(str2);	
	}
		
	public static String string(byte[] data, String encoding) throws UnsupportedEncodingException{
		return new String(data, encoding);
	}
	
	public static String string(byte[] data, int off, int len, String encoding) throws UnsupportedEncodingException{
		return new String(data, off, len, encoding);
	}
	
	public static byte[] bytes(String str, String encoding) throws UnsupportedEncodingException{
		return str.getBytes(encoding);
	}
	
	public static double longBitsToDouble(long bits){
		return Double.longBitsToDouble(bits);
	}
	
	public static float intBitsToFloat(int bits){
		return Float.intBitsToFloat(bits);
	}
	
	public static int floatToIntBits(float value) {
		return Float.floatToIntBits(value); 
	}
	
	public static long doubleToLongBits(double value) {
		return Double.doubleToLongBits(value);
	}
	
	public static int lastIndexOf(Vector v, Object object){
		return v.lastIndexOf(object);
	}
	
	public static long currentTimeMillis(){
		return System.currentTimeMillis();
	}
	
	public static void sleep(int milliseconds) throws InterruptedException{
		try {
			Thread.sleep(milliseconds);
		} catch (java.lang.InterruptedException e) {
			throw new InterruptedException();
		}
	}
	
}
