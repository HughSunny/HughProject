package com.set.ui.list;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

import com.test.list.CRScrollViewTest;

/**
 * 横向监控的HorizontalScrollView
 */
public class CHScrollView extends HorizontalScrollView {
	
	CRScrollViewTest activity;
	
	public CHScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		activity = (CRScrollViewTest) context;
	}

	
	public CHScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		activity = (CRScrollViewTest) context;
	}

	public CHScrollView(Context context) {
		super(context);
		activity = (CRScrollViewTest) context;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		activity.mTouchView = this;
		return super.onTouchEvent(ev);
	}
	
	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		if(activity.mTouchView == this) {
			activity.onScrollChanged(l, t, oldl, oldt);
		}else{
			super.onScrollChanged(l, t, oldl, oldt);
		}
	}
}
