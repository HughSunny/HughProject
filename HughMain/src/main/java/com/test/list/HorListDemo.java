package com.test.list;

import android.app.Activity;
import android.os.Bundle;
import android.widget.LinearLayout;

import com.set.R;
import com.set.ui.list.HVListView;
import com.set.ui.list.adapter.HorScrollListAdapter;

/**
 * 失败
 * 开源 scroller + listview 横向滑动 协同滚动
 */
public class HorListDemo extends Activity {
	HVListView listView;
	LinearLayout headerView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hor_list);
		headerView = (LinearLayout)findViewById(R.id.item_scroll_content);
		listView = (HVListView)findViewById(R.id.hor_list);
		listView.setTitleView(headerView);
		HorScrollListAdapter adapter = new HorScrollListAdapter(this);
		adapter.setListView(listView);
		listView.setAdapter(adapter);	
	}
}
