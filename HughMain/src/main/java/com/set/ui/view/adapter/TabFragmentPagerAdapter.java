package com.set.ui.view.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;

public class TabFragmentPagerAdapter extends FragmentPagerAdapter {
	private  ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();
	ArrayList<Fragment> fragments;
	private Context context;
	private FragmentManager fm;
	public TabFragmentPagerAdapter(FragmentManager fm, Context context) {
		super(fm);
		this.fm = fm;
	}

	@Override
	public Fragment getItem(int position) {
		TabInfo info = mTabs.get(position);
		Fragment.instantiate(context, info.clss.getName(), info.args);
		return null;
	}
	
	
	@Override
	public int getCount() {
		return  mTabs.size();
	}
	
	
	class TabInfo {
		private  final String tag;
		private  Class<?> clss;
		private  Bundle args;

		TabInfo(String _tag, Class<?> _class, Bundle _args) {
			tag = _tag;
			clss = _class;
			args = _args;
		}
	}
	
	public void setFragments(ArrayList<Fragment> fragments) {
		if(this.fragments != null){
			FragmentTransaction ft = fm.beginTransaction();
			for(Fragment f:this.fragments){
				ft.remove(f);
			}
			ft.commit();
			ft=null;
			fm.executePendingTransactions();
		}
		this.fragments = fragments;
		notifyDataSetChanged();
	}

}
