package com.test;

import android.app.Activity;
import android.os.Bundle;

import com.set.ui.view.CustomDatePickerView;

public class DatePickerDemo extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(new CustomDatePickerView(this, null));
	}
}
