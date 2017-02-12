package com.set.ui.view.adapter;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

public class TabPagerAdapter extends PagerAdapter {

	private List<View> viewList = new ArrayList<View>();

	@Override
	public void destroyItem(View view, int position, Object arg2) {
		ViewPager pViewPager = ((ViewPager) view);
		pViewPager.removeView(viewList.get(position));
	}

	@Override
	public void finishUpdate(View view) {
	}

	@Override
	public int getCount() {
		return viewList.size();
	}

	@Override
	public Object instantiateItem(View view, int position) {
		ViewPager pViewPager = ((ViewPager) view);
		pViewPager.addView(viewList.get(position));
		return viewList.get(position);
	}

	@Override
	public boolean isViewFromObject(View view, Object arg1) {
		return view == arg1;
	}

	@Override
	public void restoreState(Parcelable arg0, ClassLoader arg1) {
	}

	@Override
	public Parcelable saveState() {
		return null;
	}

	@Override
	public void startUpdate(View arg0) {
	}

}
