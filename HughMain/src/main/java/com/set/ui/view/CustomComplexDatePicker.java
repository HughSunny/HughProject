package com.set.ui.view;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.set.R;
import com.set.ui.view.wheel.NumericWheelAdapter;
import com.set.ui.view.wheel.OnWheelChangedListener;
import com.set.ui.view.wheel.WheelView;

public class CustomComplexDatePicker extends LinearLayout {
	private LayoutInflater minflater;
	private static int START_YEAR = 1990, END_YEAR = 2100;
	private CustomWheel yearWheel,monthWheel,dayWheel,hourWheel,minsWheel;
	// 添加大小月月份并将其转换为list,方便之后的判断
	private String[] months_big = { "1", "3", "5", "7", "8", "10", "12" };
	private String[] months_little = { "4", "6", "9", "11" };
	
	public static final int TYPE_DATE = 1;
	public static final int TYPE_TIME = 2;
	final List<String> list_big = Arrays.asList(months_big);
	final List<String> list_little = Arrays.asList(months_little);
	Calendar calendar;
	public static final int SYSTEM_DATE = 1; //更新系统时间
	public static final int REMOTE_DATE = 2; //更新开关机时间
	public CustomComplexDatePicker(Context context, AttributeSet attrs) {
		super(context, attrs);
		minflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		minflater.inflate(R.layout.date_picker, this);
		calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DATE);
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);

		// 年
		yearWheel = (CustomWheel)findViewById(R.id.year);
		yearWheel.setAdapter(new NumericWheelAdapter(START_YEAR, END_YEAR));// 设置"年"的显示数据
		yearWheel.setCyclic(true);// 可循环滚动
//		yearWheel.setLabel("年");// 添加文字
		yearWheel.setCurrentItem(year - START_YEAR);// 初始化时显示的数据

		// 月
		monthWheel = (CustomWheel)findViewById(R.id.month);
		monthWheel.setAdapter(new NumericWheelAdapter(1, 12));
		monthWheel.setCyclic(true);
//		monthWheel.setLabel("月");
		monthWheel.setCurrentItem(month);

		// 日
		dayWheel = (CustomWheel)findViewById(R.id.day);
		dayWheel.setCyclic(true);
		// 判断大小月及是否闰年,用来确定"日"的数据
		if (list_big.contains(String.valueOf(month + 1))) {
			dayWheel.setAdapter(new NumericWheelAdapter(1, 31));
		} else if (list_little.contains(String.valueOf(month + 1))) {
			dayWheel.setAdapter(new NumericWheelAdapter(1, 30));
		} else {
			// 闰年
			if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0)
				dayWheel.setAdapter(new NumericWheelAdapter(1, 29));
			else
				dayWheel.setAdapter(new NumericWheelAdapter(1, 28));
		}
//		dayWheel.setLabel("日");
		dayWheel.setCurrentItem(day - 1);

		// 时
		hourWheel = (CustomWheel)findViewById(R.id.hour);
		hourWheel.setAdapter(new NumericWheelAdapter(0, 23));
		hourWheel.setCyclic(true);
		hourWheel.setCurrentItem(hour);

		// 分
		minsWheel = (CustomWheel)findViewById(R.id.mins);
		minsWheel.setAdapter(new NumericWheelAdapter(0, 59, "%02d"));
		minsWheel.setCyclic(true);
		minsWheel.setCurrentItem(minute);

		yearWheel.addChangingListener(wheelListener_year);
		monthWheel.addChangingListener(wheelListener_month);
	}
	
	
	//设置日期wheel的大小
	public void setDateWheelParms(int width,int height){
		LayoutParams layoutParams  = new LayoutParams(width, height);
		yearWheel.setLayoutParams(layoutParams);
		monthWheel.setLayoutParams(layoutParams);
		dayWheel.setLayoutParams(layoutParams);
	}
	//设置时间wheel的大小
	public void setTimeWheelParms(int width,int height){
		LayoutParams layoutParams  = new LayoutParams(width, height);
		hourWheel.setLayoutParams(layoutParams);
		minsWheel.setLayoutParams(layoutParams);
	}
	
	public void setLeft(){
		hourWheel.setLeft();
	}

	// 添加"年"监听
	OnWheelChangedListener wheelListener_year = new OnWheelChangedListener() {
		public void onChanged(WheelView wheel, int oldValue, int newValue) {
			int year_num = newValue + START_YEAR;
			// 判断大小月及是否闰年,用来确定"日"的数据
			if (monthWheel.getCurrentItem() + 1 == 2) {
				if ((year_num % 4 == 0 && year_num % 100 != 0)
						|| year_num % 400 == 0){

					dayWheel.setAdapter(new NumericWheelAdapter(1, 29));
				}else{
					if(dayWheel.getCurrentItem()>27){
						dayWheel.setCurrentItem(27);
					}
					dayWheel.setAdapter(new NumericWheelAdapter(1, 28));
				}
			}
			
		}
	};

	// 添加"月"监听
	OnWheelChangedListener wheelListener_month = new OnWheelChangedListener() {
		public void onChanged(WheelView wheel, int oldValue, int newValue) {
			int month_num = newValue + 1;
			// 判断大小月及是否闰年,用来确定"日"的数据
			if (list_big.contains(String.valueOf(month_num))) {
				dayWheel.setAdapter(new NumericWheelAdapter(1, 31));
			} else if (list_little.contains(String.valueOf(month_num))) {
				if(dayWheel.getCurrentItem()>29){
					dayWheel.setCurrentItem(29);
				}
				dayWheel.setAdapter(new NumericWheelAdapter(1, 30));
			} else {
				if (((yearWheel.getCurrentItem() + START_YEAR) % 4 == 0 && (yearWheel
						.getCurrentItem() + START_YEAR) % 100 != 0)
						|| (yearWheel.getCurrentItem() + START_YEAR) % 400 == 0){
					if(dayWheel.getCurrentItem()>28){
						dayWheel.setCurrentItem(28);
					}
					dayWheel.setAdapter(new NumericWheelAdapter(1, 29));
				}else{
					if(dayWheel.getCurrentItem()>27){
						dayWheel.setCurrentItem(27);
					}
					dayWheel.setAdapter(new NumericWheelAdapter(1, 28));
				}
			}
			
		}
	};
	
	public void setType(int type){
		if(type == TYPE_TIME){
			yearWheel.setVisibility(View.GONE);
			monthWheel.setVisibility(View.GONE);
			dayWheel.setVisibility(View.GONE);
			
			hourWheel.setVisibility(View.VISIBLE);
			minsWheel.setVisibility(View.VISIBLE);
			
			int hour = calendar.get(Calendar.HOUR_OF_DAY);
			int minute = calendar.get(Calendar.MINUTE);
			hourWheel.setCurrentItem(hour);
			minsWheel.setCurrentItem(minute);
		}else if(type == TYPE_DATE){
			yearWheel.setVisibility(View.VISIBLE);
			monthWheel.setVisibility(View.VISIBLE);
			dayWheel.setVisibility(View.VISIBLE);
			
			hourWheel.setVisibility(View.GONE);
			minsWheel.setVisibility(View.GONE);
		}
	}
	
	
	public int getYear(){
		return yearWheel.getCurrentItem()+1990;
	}
	
	public int getMonth(){
		return monthWheel.getCurrentItem()+1;
	}

	public int getDay(){
		return dayWheel.getCurrentItem()+1;
	}
	
	public int getHour(){
		return hourWheel.getCurrentItem();
	}
	
	public int getMins(){
		return minsWheel.getCurrentItem();
	}
}
