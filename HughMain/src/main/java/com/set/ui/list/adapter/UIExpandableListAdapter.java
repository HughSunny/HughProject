package com.set.ui.list.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.set.R;
import com.set.ui.list.UIExpandableListView.PinnedExpandableListViewAdapter;

import java.util.ArrayList;

public class UIExpandableListAdapter extends BaseExpandableListAdapter implements PinnedExpandableListViewAdapter, OnTouchListener{
	private ArrayList<String> titleList;
	private ArrayList<ArrayList<String>> childList;
	private Context mContext;
	private LayoutInflater mInflater;
	private ExpandableListView listView;

	public  boolean[] isopen;
	public UIExpandableListAdapter( Context context ,ExpandableListView _listView ,ArrayList<String> _titleList, ArrayList<ArrayList<String>> _childList ){
		mContext = context;
		mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		titleList = _titleList;
		childList = _childList;
		listView = _listView;
		isopen = new boolean[titleList.size()];
	}


	public int getPinnedHeaderState(int groupPosition, int childPosition) {                                       
		final int childCount = getChildrenCount(groupPosition);                                                   
		if(childPosition == childCount - 1){                                                                      
			return PINNED_HEADER_PUSHED_UP;                                                                       
		}else if(childPosition == -1 && !listView.isGroupExpanded(groupPosition)){                      
			return PINNED_HEADER_GONE;                                                                            
		}else {                                                                                                   
			return PINNED_HEADER_VISIBLE;                                                                         
		}                                                                                                         
	}      
	private TextView headerText;
	@Override                                                                                                    
	public void configurePinnedHeader(View header, int groupPosition, int childPosition, int alpha) {             
		headerText = (TextView)header.findViewById(R.id.expandableList_header_text);
		headerText.setText(titleList.get(groupPosition));
	}       

	public int getGroupCount() {
		// TODO Auto-generated method stub
		return titleList.size();
	}

	public int getChildrenCount(int groupPosition) {
		// TODO Auto-generated method stub
		return childList.get(groupPosition).size();
	}

	public Object getGroup(int groupPosition) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object getChild(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return null;
	}

	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		return 0;
	}

	public long getChildId(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}

	private int sectionHeight;
	private AbsListView.LayoutParams lp;
	public void SetSectionHeight(int height){
		sectionHeight = height;
		lp = new AbsListView.LayoutParams(-1, sectionHeight);
	}
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		SectionHolder holder = null;
		if(convertView == null){
			holder = new SectionHolder();
			convertView = mInflater.inflate(R.layout.group_item, null);
			convertView.setLayoutParams(lp);
			holder.headerText = (TextView)convertView.findViewById(R.id.expandableList_header_text);
			holder.arrowdown = (ImageView)convertView.findViewById(R.id.arrow_down);
			holder.arrowup= (ImageView)convertView.findViewById(R.id.arrow_up);
			convertView.setTag(holder);
			convertView.setOnTouchListener(this);
		}else {
			holder = (SectionHolder)convertView.getTag();
		}
		holder.position = groupPosition;
		if(isopen[groupPosition]){
			holder.arrowup.setVisibility(View.GONE);
			holder.arrowdown.setVisibility(View.VISIBLE);
		}else{
			holder.arrowdown.setVisibility(View.GONE);
			holder.arrowup.setVisibility(View.VISIBLE);
		}
		holder.headerText.setText(titleList.get(groupPosition));

		return convertView;
	}

	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ChildHolder holder = null;
		if(convertView == null){
			holder = new ChildHolder();
			convertView = mInflater.inflate(R.layout.child_item, null);
			holder.childText = (TextView)convertView.findViewById(R.id.expandableList_child_text);
			convertView.setTag(holder);
		}else {
			holder = (ChildHolder)convertView.getTag();
		}
		holder.childText.setText(childList.get(groupPosition).get(childPosition));

		return convertView;
	}

	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		Toast.makeText(mContext, "这是第"+groupPosition+"组，第"+childPosition+"个", Toast.LENGTH_SHORT).show(); 
		return false;
	}

	public class SectionHolder{
		private TextView headerText;
		private ImageView arrowup;
		private ImageView arrowdown;
		private int position;
	}

	public class ChildHolder{
		private TextView childText;
	}

	private boolean iscancle;
	public boolean onTouch(View v, MotionEvent event) {
		if(event.getAction() == MotionEvent.ACTION_UP){
			if(!iscancle){
				SectionHolder hold = (SectionHolder)v.getTag();
				if(isopen[hold.position]){
					hold.arrowdown.setVisibility(View.VISIBLE);
					hold.arrowup.setVisibility(View.GONE);
					isopen[hold.position]= false;
					listView.collapseGroup(hold.position);
				}else{
					hold.arrowup.setVisibility(View.VISIBLE);
					hold.arrowdown.setVisibility(View.GONE);
					isopen[hold.position]= true;
					listView.expandGroup(hold.position);
				}
			}
			iscancle = false;
		}else if(event.getAction() == MotionEvent.ACTION_DOWN){
			iscancle = false;
			
		}else if(event.getAction() == MotionEvent.ACTION_MOVE){
			if(event.getX()<0||event.getX()>v.getWidth()||event.getY()<0||event.getY()>v.getHeight()){
				iscancle = true;
			}
		}else if(event.getAction() == MotionEvent.ACTION_CANCEL){
			System.out.println("cancle");
		}

		return true;
	}

}
