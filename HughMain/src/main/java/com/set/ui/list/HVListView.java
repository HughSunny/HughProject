package com.set.ui.list;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Scroller;

/**
 * 
 * ClassName:HVListView
 * Function: 固定列数横向 滚动listview控件
 *
 * @author   xxshi
 * @Date	 2014	2014-11-12		下午2:30:02
 *
 * @see
 */
public class HVListView extends ListView {
	private static final String TAG = "HVListView";
	public Scroller mScroller;
	private VelocityTracker mVelocityTracker;
	private static int isDown = 0;
	private float firstX;
	private float firstY;
	private boolean isMoved = false;
	public int maxScrollWidth = 0;
	private View titleView ;
	private int wholeWidth;
	public static final int SCORLL_INDEX = 2;
	private int otherScrollX = 0;
	public HVListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setClickable(true);
		mScroller = new Scroller(context);
	}
	private boolean isScrollMove = false;

	@Override
	public void computeScroll() {
		if (!((mScroller.computeScrollOffset()) && (titleView != null))) {
			return;
		}
		boolean needAboat = false;
		int scrollX = mScroller.getCurrX();
		int titleScrollX = titleView.getScrollX();
		Log.i(TAG, "computeScroll -- scrollX = " + scrollX + "  ; titleScrollX == " + titleScrollX + " ; otherScrollX = " + otherScrollX);
		Log.i(TAG, "computeScroll -- mScroller.getStartX = " + mScroller.getStartX() + "  ; mScroller.getFinalX() == " + mScroller.getFinalX() + "");
		int dx = 0;
		if (otherScrollX != 0) {
			dx = scrollX - otherScrollX;
		} else {
			dx = scrollX;
		}
		if (titleScrollX + dx <= 0) {//左边到顶      当前是SCROLLX是负的
			needAboat = true;
			dx = - titleScrollX;
		}
		if (dx + titleScrollX >= maxScrollWidth) {//右边到顶  SCROLLX 
			needAboat = true;
			dx = maxScrollWidth - titleScrollX;
		}
		Log.i(TAG, "computeScroll -- scrollX   dx= " + dx + "  ; maxScrollWidth == " + maxScrollWidth );
		titleView.scrollBy(dx, 0);
		int childCount = getChildCount();
		for (int j = 0; j < childCount; j++) {
			getScrollItem(j).scrollBy(dx, 0);
		}
		if (mScroller.getFinalX() != 0) { //==0 fling  != page_test_scroll
			otherScrollX = mScroller.getCurrX();
		}
		
        if (needAboat || (scrollX == mScroller.getFinalX())) {
//        	scrollEnd();
        	otherScrollX = 0;
        	mScroller.abortAnimation();
		}
        invalidate();
	}
	
	/**
	 * Animaiton end
	 */
	private void scrollEnd(){
		if(!isScrollMove) return;
		ViewGroup view = (ViewGroup)titleView;
		for (int i = 1; i < view.getChildCount(); i++) {
			View item = view.getChildAt(i);
			if(item.getLeft() - titleView.getScrollX() < titleView.getWidth() && 
					item.getRight() - titleView.getScrollX() > titleView.getWidth()){
				int x = item.getLeft()- titleView.getScrollX() - titleView.getWidth() + item.getWidth() / 2;
				if(x < 0){
					//需要显示全部item
					x = item.getRight()- titleView.getScrollX() - titleView.getWidth();
					mScroller.startScroll(titleView.getScrollX(), 0, x, 0, 500);
					postInvalidate();
				}else{
					x = item.getLeft()- titleView.getScrollX() - titleView.getWidth();
					mScroller.startScroll(titleView.getScrollX(), 0,  x, 0, 500);
					postInvalidate();
				}
				isScrollMove = false;
				break;
			}
		}	
	}

	public boolean onInterceptTouchEvent(MotionEvent event) {
		if (!isEnabled())
			return false;
		if (mVelocityTracker == null)
			mVelocityTracker = VelocityTracker.obtain();
		mVelocityTracker.addMovement(event);
		final int action = event.getAction();
//		Log.i(TAG, "onInterceptTouchEvent = " + action  + "  " +isDown);
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			isDown = 0;
			firstX = event.getX();
			firstY = event.getY();
			int k = pointToPosition((int) event.getX(), (int) event.getY());
			if (k != -1)
//				tempView = ((ViewGroup) getChildAt(k
//						- getFirstVisiblePosition()));
			isMoved = false;
			break;
		case MotionEvent.ACTION_MOVE:
			
			float cutX = Math.abs(event.getX() - firstX); 
			float cutY =  Math.abs(event.getY() - firstY);
			if ((cutX > cutY) && (cutX > 8) && (isDown == 0)) {
				isDown = 2;
				isMoved = true;
			} else if ((cutY > cutX) && (cutY > 8)&& (isDown == 0)) {
				isDown = 1;
				isMoved = true;
			}
//					int i = pointToPosition((int) event.getX(),
//							(int) event.getY());
//					if (i != -1){
//						Log.e(TAG, "SET COLOR");
//						((ViewGroup) getChildAt(i - getFirstVisiblePosition()))
//						.setBackgroundColor(listViewBG);
//					}
			if (isDown == 2 && isMoved) {
				Log.i(TAG, "scrollRow " + event.getX());
				isDown = 2;
				scrollRow(event.getX());
				return true;
			}
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			break;
		}
//		Log.i(TAG, "onInterceptTouchEvent   return false  ");
		return false;
	}

	private synchronized void scrollRow(float scrollx) {
		if (titleView == null) {
			return;
		}
		getMaxScrollWidth();
		int i = (int) (scrollx - firstX);
		int j = titleView.getScrollX();
		
		if (j - i < 0) {
			i = j;
		} 
		if (j - i > maxScrollWidth) {
			i = j - maxScrollWidth;
		}
		titleView.scrollBy(-i, 0);
		int count = getChildCount();
		for (int m = 0; m < count; m++) {
			getScrollItem(m).scrollBy(-i, 0);
		}
		firstX = scrollx;
		invalidate();
	}
	
	private View getScrollItem(int postion) {
		return ((ViewGroup)getChildAt(postion)).getChildAt(SCORLL_INDEX);
//		return getChildAt(postion);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement(event);
		final int action = event.getAction();
//		Log.i(TAG, "onTouchEvent = " + action + "   " +isDown);
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			isDown = 0;
		    firstX = event.getX();
		    firstY = event.getY();
		    isMoved = false;
			break;
		case MotionEvent.ACTION_MOVE:
			float moveX = Math.abs(event.getX() - firstX);
			float moveY = Math.abs(event.getY() - firstY);
			if ((moveX > moveY) && (moveX > 8F) && (isDown == 0)) {
				isDown = 2;
				isMoved = true;
			} else if ((moveY > moveX) && (moveY > 8)&& (isDown == 0)) {
				isDown = 1;
				isMoved = true;
			}
			if (isDown == 2 && isMoved) {
				scrollRow(event.getX());
				return true;
			}
			return super.onTouchEvent(event);
		case MotionEvent.ACTION_UP:
			final VelocityTracker velocityTracker = mVelocityTracker;
			velocityTracker.computeCurrentVelocity(1000);
			int velocityX = (int) velocityTracker.getXVelocity();
			if (isDown == 2)  {
				if (!mScroller.isFinished())
					mScroller.abortAnimation();
				isScrollMove = true;
				mScroller.fling(titleView.getScrollX(), titleView.getScrollY(), -velocityX, 0, 0,maxScrollWidth, 0, 10);
//				mScroller.startScroll(0, 0, -(velocityX / 10), 0, Math.abs(velocityX / 5));
				if (isMoved) {
//					if (tempView != null) {
//						tempView.setPressed(false);
//						tempView.postInvalidate();
//					}
					isMoved = false;
					return super.onTouchEvent(event);
				}
				//TODO 监听
//				if ((tempView != null) && (fixedRowSelect != null) && (!isMoved))
//					fixedRowSelect.onRowSelected(tempView.getId());
//				if (tempView != null) {
//					tempView.setPressed(false);
//					tempView.postInvalidate();
//				}
//				tempView = null;
			}
//			if ((tempView != null) && (fixedRowSelect != null) && (!isMoved))
//				fixedRowSelect.onRowSelected(tempView.getId());
			isDown = -1;
			if (mVelocityTracker != null) {
				mVelocityTracker.recycle();
				mVelocityTracker = null;
			}
			break;
		case MotionEvent.ACTION_CANCEL:
//			Log.e(TAG, "SET COLOR2");
//			this.tempView.setBackgroundColor(this.listViewBG);
			return false;
//	        ((ViewGroup)this.tempView.getChildAt(0)).getChildAt(0).setBackgroundColor(this.titleViewBG);
//			tempView.setBackgroundColor(this.titleViewBG);
		}
		return super.onTouchEvent(event);
	}

	public void getMaxScrollWidth() {
		if (maxScrollWidth != 0)
			return;
		int count = ((ViewGroup) titleView).getChildCount();
		for (int j = 0; j < count; j++) {
			View localView = ((ViewGroup) titleView).getChildAt(j);
			if (localView.getVisibility() == 0)
				maxScrollWidth += localView.getMeasuredWidth();
		}
		maxScrollWidth -= titleView.getWidth();
	}

	public void getWholeWidth() {
		if (wholeWidth != 0)
			return;
		ViewGroup title = (ViewGroup) titleView;
		for (int i = 0; i > title.getChildCount() ; i++) {
			if (title.getChildAt(i).getVisibility() == 0)
				wholeWidth += title.getChildAt(i).getWidth();
		}
	}
	
	public void setTitleView(View titleView) {
		maxScrollWidth = 0;
		this.titleView = titleView;
	}
	
	/** 获取列头偏移量 */
	public int getHeadScrollX() {
		return titleView.getScrollX();
	}

	public void scrollHor(int dx) {
		getMaxScrollWidth();
		if (!mScroller.isFinished())
			mScroller.abortAnimation();
		mScroller.startScroll(0, 0, dx, 0, 100);
		invalidate();
	}
	
}
