package com.set.ui.view.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.set.R;
import com.set.ui.list.HVListView;

public class HorScrollListAdapter extends BaseAdapter {
	
	private List<HorScrollDataItem> dataList = new ArrayList<HorScrollDataItem>();
	private Context context;
	LayoutInflater mInflater;
	
	private ListView listView;
	public HorScrollListAdapter(Context context) {
		this.context = context;
		mInflater = LayoutInflater.from(context);
		HorScrollDataItem tempItem;
		for (int i = 0; i < 100; i++) {
			tempItem = new HorScrollDataItem();
			tempItem.bedNo = i +"床";
			tempItem.name ="某某" + i;
			tempItem.time = "2010-10-10 11:11:11";
			tempItem.doInfo = "普通巡房";
			tempItem.doPeople ="执行人" + i;
			tempItem.status = "通过";
			dataList.add(tempItem);
		}
	}
	
	public void setListView(ListView _listView) {
		listView = _listView;
	}
	
	@Override
	public int getCount() {
		return dataList.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}
	HorScrollDataItem item;
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
        if (null == convertView) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.hor_list_item, null);
            viewHolder.text1 = (TextView) convertView.findViewById(R.id.plxcck_ch);
            viewHolder.text2 = (TextView) convertView.findViewById(R.id.plxcck_xm);
            viewHolder.text3 = (TextView) convertView.findViewById(R.id.plxcck_sj);
            viewHolder.text4 = (TextView) convertView.findViewById(R.id.plxcck_zx);
            viewHolder.text5 = (TextView) convertView.findViewById(R.id.plxcck_zxr);
            viewHolder.text6 = (TextView) convertView.findViewById(R.id.plxcck_zt);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        item = dataList.get(position);
        viewHolder.text1.setText(item.bedNo);
        viewHolder.text2.setText(item.name);
        viewHolder.text3.setText(item.time);
        viewHolder.text4.setText(item.doInfo);
        viewHolder.text5.setText(item.doPeople);
        viewHolder.text6.setText(item.status); 
        
        if (listView != null && listView instanceof HVListView) {
			int headx = ((HVListView)listView).getHeadScrollX();
			if (convertView.getScrollX() != headx) {
				convertView.scrollTo(headx, 0);
			}
		}
		return convertView;
	}
	
	 private static  class ViewHolder{
	        TextView text1;
	        TextView text2;
	        TextView text3;
	        TextView text4;
	        TextView text5;
	        TextView text6;
	    }
	

}
