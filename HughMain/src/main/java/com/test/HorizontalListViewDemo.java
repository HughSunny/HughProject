package com.test;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.set.R;
import com.set.ui.list.HorizontialListView;

public class HorizontalListViewDemo extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.listviewdemo);
		HorizontialListView listview = (HorizontialListView) findViewById(R.id.listview);
		listview.setAdapter(mAdapter);
//		setContentView(new HorCombineListView(this, null));
//		setContentView(R.layout.activity_test_twd);
	}
	
	private static String[] dataObjects = new String[]{ "Text #1",
		"Text #2",
		"Text #3" ,"Text #4",
		"Text #5" ,"Text #6",
		"Text #7" ,"Text #8",
		"Text #9","Text #10",
		"Text #11" ,"Text #12",
		"Text #13" }; 
	
	private BaseAdapter mAdapter = new BaseAdapter() {

		@Override
		public int getCount() {
			return dataObjects.length;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View retval = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewitem, null);
			TextView title = (TextView) retval.findViewById(R.id.title);
			title.setText(dataObjects[position]);
			
			return retval;
		}
		
	};

}
