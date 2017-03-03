package com.test.list;

import com.set.ui.list.HorScrollListView;

import android.app.Activity;
import android.os.Bundle;

public class HorScrollListDemo extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(new HorScrollListView(this, null));
	}
	
}
