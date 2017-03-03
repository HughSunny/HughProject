package com.test.list;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.set.R;
import com.set.ui.list.CHScrollView;
public class HListActivity extends Activity{
	 private ListView mListView;
	 public HorizontalScrollView mTouchView;
	 protected List<CHScrollView> mHScrollViews =new ArrayList<CHScrollView>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_test_scroll);
		initViews();
	}
	
	private void initViews() {
		List<Map<String, String>> datas = new ArrayList<Map<String,String>>();
		Map<String, String> data = null;
		CHScrollView headerScroll = (CHScrollView) findViewById(R.id.item_scroll_title);
		//���ͷ�����¼� 
		mHScrollViews.add(headerScroll);
		mListView = (ListView) findViewById(R.id.scroll_list);
		for(int i = 0; i < 100; i++) {
			data = new HashMap<String, String>();
			data.put("title", "Title_" + i);
			data.put("data_" + 1, "Date_" + 1 + "_" +i );
			data.put("data_" + 2, "Date_" + 2 + "_" +i );
			data.put("data_" + 3, "Date_" + 3 + "_" +i );
			data.put("data_" + 4, "Date_" + 4 + "_" +i );
			data.put("data_" + 5, "Date_" + 5 + "_" +i );
			data.put("data_" + 6, "Date_" + 6 + "_" +i );
			datas.add(data);
		}
		SimpleAdapter adapter = new ScrollAdapter(this, datas, R.layout.item
							, new String[] { "title", "data_1", "data_2", "data_3", "data_4", "data_5", "data_6", }
							, new int[] { R.id.item_title 
										, R.id.item_data1
										, R.id.item_data2
										, R.id.item_data3
										, R.id.item_data4
										, R.id.item_data5
										, R.id.item_data6 });
		mListView.setAdapter(adapter);
	}
	
	public void addHViews(final CHScrollView hScrollView) {
		if(!mHScrollViews.isEmpty()) {
			int size = mHScrollViews.size();
			CHScrollView scrollView = mHScrollViews.get(size - 1);
			final int scrollX = scrollView.getScrollX();
			if(scrollX != 0) {
				mListView.post(new Runnable() {
					@Override
					public void run() {
						hScrollView.scrollTo(scrollX, 0);
					}
				});
			}
		}
		mHScrollViews.add(hScrollView);
	}
	
	public void onScrollChanged(int l, int t, int oldl, int oldt){
		for(CHScrollView scrollView : mHScrollViews) {
			if(mTouchView != scrollView)
				scrollView.smoothScrollTo(l, t);
		}
	}
	
	class ScrollAdapter extends SimpleAdapter {

		private List<? extends Map<String, ?>> datas;
		private int res;
		private String[] from;
		private int[] to;
		private Context context;
		public ScrollAdapter(Context context,
				List<? extends Map<String, ?>> data, int resource,
				String[] from, int[] to) {
			super(context, data, resource, from, to);
			this.context = context;
			this.datas = data;
			this.res = resource;
			this.from = from;
			this.to = to;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if(v == null) {
				v = LayoutInflater.from(context).inflate(res, null);
				addHViews((CHScrollView) v.findViewById(R.id.item_scroll));
				View[] views = new View[to.length];
				for(int i = 0; i < to.length; i++) {
					View tv = v.findViewById(to[i]);;
					tv.setOnClickListener(clickListener);
					views[i] = tv;
				}
				v.setTag(views);
			}
			View[] holders = (View[]) v.getTag();
			int len = holders.length;
			for(int i = 0 ; i < len; i++) {
				((TextView)holders[i]).setText(this.datas.get(position).get(from[i]).toString());
			}
			return v;
		}
	}
	
	protected View.OnClickListener clickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Toast.makeText(HListActivity.this, ((TextView)v).getText(), Toast.LENGTH_SHORT).show();
		}
	};
}
