package com.set.ui.list;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.set.R;
import com.set.ui.view.adapter.HorScrollListAdapter;

/**
 * 测试失败，scrollview + listview 一起滚动
 */
public class HorScrollListView extends LinearLayout{
	LayoutInflater inflater;
	private ListView listView;
	public HorScrollListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.horscroll_list, this);
		listView = (ListView)findViewById(R.id.listview);
		HorScrollListAdapter adapter = new HorScrollListAdapter(context);
		listView.setAdapter(adapter);	
	}
	
}
