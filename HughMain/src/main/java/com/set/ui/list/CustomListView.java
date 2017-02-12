package com.set.ui.list;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

public class CustomListView extends ListView implements OnScrollListener {
	HorCombineListView parent;
	public CustomListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setOnScrollListener(this);
	}
	boolean isScroll;
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		//进行触摸赋值
		HorCombineListView.scrollList = this;
		int action = ev.getAction();
		if (action == MotionEvent.ACTION_DOWN) {
		}else if (action == MotionEvent.ACTION_UP){
		}
		return super.onTouchEvent(ev);
	}
	
	int currentY = 0;
	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		//当当前的CHSCrollView被触摸时，滑动其它
		View c = getChildAt(0);
		if (c == null) {
			return;
		}
		int firstVisiblePosition = getFirstVisiblePosition();
		int top = c.getTop();
		int Y = -top + firstVisiblePosition * c.getHeight();
		if(HorCombineListView.scrollList == this) {
			Log.i("CustomListView", "L = " + l + ";t = " + t +";oldl = " + oldl +";oldt = "+ oldt );
			parent.onScrollChanged(firstVisiblePosition, top, Y - currentY);
			currentY = Y;
		}else{
			Log.i("CustomListView", "NOT  THIS ;;L = " + l + ";t = " + t +";oldl = " + oldl +";oldt = "+ oldt );
			currentY = Y;
			super.onScrollChanged(l, t, oldl, oldt);
		}
	}
	
	public int getListScrollY() {
		View c = getChildAt(0);
		if (c == null) {
			return 0;
		}
		int firstVisiblePosition = getFirstVisiblePosition();
		int top = c.getTop();
		currentY = -top + firstVisiblePosition * c.getHeight();
		return currentY;
	}
	
	public void setPatent(HorCombineListView horCombineListView) {
		parent = horCombineListView;
	}
	
	@Override
	public void scrollListBy(int y) {
		super.scrollListBy(y);
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		switch (scrollState) {
		case OnScrollListener.SCROLL_STATE_IDLE:// 空闲状态
			parent.onScrollIdle(this);
			break;
		case OnScrollListener.SCROLL_STATE_FLING:// 滚动状态
			break;
		case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:// 触摸后滚动
			break;
		}
	}
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub
		
	}

}
