package com.set.ui.list;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.set.R;

public class HorCombineListView extends LinearLayout {
	CustomListView leftList;
	CustomListView rightList;
	public static ListView scrollList;
	List<CustomListView> mHScrollViews = new ArrayList<CustomListView>();
	public HorCombineListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.hor_combine_list, this);
		leftList = (CustomListView)findViewById(R.id.hor_left_list);
		rightList = (CustomListView)findViewById(R.id.hor_right_list);
		leftList.setPatent(this);
		rightList.setPatent(this);
		
		leftList.setAdapter(new LeftAdapter(context));
		rightList.setAdapter(new RightAdapter(context));
		mHScrollViews.add(leftList);
		mHScrollViews.add(rightList);
	}
	
	public void onScrollChanged(int position, int topoffset,int scrollY){
		for(CustomListView scrollView : mHScrollViews) {
			//防止重复滑动
			if(scrollList != scrollView){
				scrollView.scrollListBy(scrollY);
//				scrollView.smoothScrollToPositionFromTop(position, topoffset);
//				scrollView.scrollTo(0 , scrollY);
			}
		}
	}
	
	/**
	 * 矫正
	 * @param list
	 */
	public void onScrollIdle(ListView list){
		Log.w("CustomListView", "onScrollIdle");
		int leftY = leftList.getListScrollY();
		int rightY = rightList.getListScrollY();
		if (list == leftList) {
			Log.w("CustomListView", "onScrollIdle  leftY ==" + leftY + "rightY" + rightY );
			leftList.scrollListBy(rightY - leftY);
		} else {
			Log.w("CustomListView", "onScrollIdle  leftY ==" + leftY + "rightY" + rightY );
			rightList.scrollListBy(leftY - rightY);
		}
		
	}
	
	
	class LeftAdapter extends BaseAdapter {
		private Context context;
		public LeftAdapter(Context context) {
			this.context = context;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder = null;
			View v = convertView;
			if(v == null) {
				viewHolder = new ViewHolder();
				v = LayoutInflater.from(context).inflate(R.layout.item_hor_left, null);
				//第一次初始化的时候装进来
				viewHolder.text1 = (TextView) v.findViewById(R.id.left_row1);
				viewHolder.text2 = (TextView) v.findViewById(R.id.left_row2);
				v.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder)v.getTag();
			}
			viewHolder.text1.setText("姓名" + position);
			viewHolder.text2.setText("床号" + position);
			return v;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 40;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}
		
		 private  class ViewHolder{
		        TextView text1;
		        TextView text2;
		 }
	}
	
	
	class RightAdapter extends BaseAdapter {
		private Context context;
		public RightAdapter(Context context) {
			this.context = context;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder = null;
			View v = convertView;
			if(v == null) {
				viewHolder = new ViewHolder();
				v = LayoutInflater.from(context).inflate(R.layout.hor_right_item, null);
				//第一次初始化的时候装进来
				viewHolder.text1 = (EditText) v.findViewById(R.id.right_row1);
				viewHolder.text2 = (EditText) v.findViewById(R.id.right_row2);
				v.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder)v.getTag();
			}
			return v;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 40;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}
		
		 private  class ViewHolder{
		       EditText text1;
		       EditText text2;
		 }
	}
	
	
	
}
