package com.set.ui.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.set.R;

/**
 * 横向滑动菜单的适配器
 * @File: MenuListAdapter.java
 * @Paceage com.meiya.ui
 * @Version 
 */
public class MenuListAdapter extends BaseAdapter {
	
	int [] listTextSrc = new int[]{R.string.tecenology,R.string.society,
			R.string.entertainment,R.string.news,
			R.string.sports,R.string.fashion,
			};
	
	int [] listImageSrc = new int[]{R.drawable.navigation_classify_ico_science,R.drawable.navigation_classify_ico_social,
			R.drawable.navigation_classify_ico_entertainment,R.drawable.navigation_classify_ico_financial,
			R.drawable.navigation_classify_ico_sport,R.drawable.navigation_classify_ico_fashion,
			};
	private Context mContext;
	
	private LayoutInflater inflater;
	
	public MenuListAdapter(Context mContext){
		this.mContext = mContext;
		this.inflater = LayoutInflater.from(mContext);
	}
	
	public MenuListAdapter(){}

	@Override
	public int getCount() {
		return listTextSrc.length;
	}

	@Override
	public Object getItem(int arg0) {
		return mContext.getResources().getString(listTextSrc[arg0]);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		ViewHolder viewHolder;
		if(convertView == null){
			viewHolder = new ViewHolder();
			convertView = inflater.inflate(R.layout.menu_item_list, null);
			viewHolder.ivSelected = (ImageView)convertView.findViewById(R.id.ivSelected);
			viewHolder.ivIcon = (ImageView)convertView.findViewById(R.id.ivIcon);
			viewHolder.tvMenu = (TextView)convertView.findViewById(R.id.tvMenu);
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.tvMenu.setText(mContext.getResources().getString(listTextSrc[position]));
		viewHolder.ivIcon.setBackgroundResource(listImageSrc[position]);
		convertView.setTag(viewHolder);
		return convertView;
	}
	
	class ViewHolder {
		ImageView ivSelected;
		ImageView ivIcon;
		TextView tvMenu; 
	}

}
