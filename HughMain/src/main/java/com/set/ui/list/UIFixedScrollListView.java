package com.set.ui.list;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Scroller;
import android.widget.TextView;

import com.set.listener.TouchEventListener;

/**
 * ****************************************************************
 * 文件名称	: UIFixedScrollListView.java
 * 作    者	: Aofei.Wang
 * 创建时间	: 2013-3-15 下午02:05:01
 * 文件描述	: 可以左右滑动列的listView
 * 修改历史	: 2013-3-15 1.00 初始版本
 *****************************************************************
 */
public class UIFixedScrollListView extends ListView implements 
	OnItemClickListener,OnScrollListener{
	
//	private boolean needAutoScroll;	//解决个人版自动滚动到边界问题
	private Scroller mScroller;
	protected View titleView;
	private View titleViewSub;

	private int isDown = -1; // 1 - y  2 - x
	private float firstX,firstY;
	private VelocityTracker mVelocityTracker;
	public int maxScrollWidth = 0;
	private boolean isMoved = false;

	private FixedRowSelect fixedRowSelect;
	private FixedRowSelectListener fixedRowSelectListener;
	private LinearLayout progressFootLayout;
	private android.widget.LinearLayout.LayoutParams mLayoutParams;
	private static final int HIDE_FOOT_VIEW = 0;
	private static final int SHOW_PROGRESS_FOOT_VIEW = 1;
	private static final int SHOW_NOVALUE_FOOT_VIEW = 2;
	private static final int HIDE_PROGRESSBAR_VIEW = 3;
	private static final int HAVE_NO_ENOUGH_DATA = 4;
	private static final int LIST_REFRESH_OK = 5;
	private static final int REFRESH_NEWSCONTENT_TITLELIST = 6;
	private boolean isNeedLoading = true;
	private int offWidth = 0;
	public int wholeWidth = 0;
	private AddButtonSelectListener addButtonSelectListener;
	private int listViewBG = 0XFF1B1B1B;
	private int titleViewBG = 0xFF252525;
	private Drawable defaultBG;
	public boolean isEnd = false;
	//private boolean isNeedRun = false;
	private int addButtonWidth = -1;
	public int contentHeigth = -1;
	//private long keyDownTime;
	private int shadowWidth = 120;
	private int mMaximumVelocity = 1200;
	private int marginWidth = 8;//StringUtils.dipToPx(8)
	private int bgColor;
	private static final String FOOT_TAG = "FOOT";
	private View highLightView = null;
//	public Object lockObj = null;
	protected boolean isSelected = true;
	
	private int dividerColor = 0;
	private int dividerHeight = 0;
	private TouchEventListener touchEventListener;
	
	/**
	 * adapter初始化
	 */
	private boolean adapterIsInit = false;
	/**
	 * 页面在setAdapter后数据第一次初始化
	 */
	private boolean isInitFirst = false;
	
	
	public boolean isInitFirst() {
		return isInitFirst;
	}

	public void setTouchEventListener(TouchEventListener touchEventListener){
		this.touchEventListener = touchEventListener;
	}

	public void setInitFirst(boolean isInitFirst) {
		this.isInitFirst = isInitFirst;
	}
	
	public void setNeedAutoScroll(boolean needAutoScroll){
//		this.needAutoScroll = needAutoScroll;
	}
	@Override
	public void setAdapter(ListAdapter adapter) {
		super.setAdapter(adapter);

		if(null==adapter){
			return;
		}
		adapterIsInit = true;
		isInitFirst = false;
		
//		adapter.registerDataSetObserver(new DataSetObserver() {
//			public void onChanged() {
//				// TODO Auto-generated method stub
//				super.onChanged();
//				adapterIsInit = true;
//				isInitFirst = false;
//			}
//		});
	}
	
	
	public UIFixedScrollListView (Context context, AttributeSet attrs){
		super(context,attrs);		
		final String path = "http://schemas.android.com/apk/res/android";
		final String tag = attrs.getAttributeValue(path, "tag");
		if(tag != null){
			isNeedLoading = false;
		}
		init();
	}
	
	public void setIsSelected(boolean s)
	{
		isSelected = s;
	}
	
	public UIFixedScrollListView (Context context){
		super(context);		
		init();
	}
	
	/**
	 * 回收资源
	 */
	public void dispose(){
		if(mScroller != null){
			mScroller.abortAnimation();
		}
		if(titleView != null && titleView instanceof ViewGroup){
			((ViewGroup)titleView).removeAllViews();
		}
		if(titleViewSub != null && titleViewSub instanceof ViewGroup){
			((ViewGroup)titleViewSub).removeAllViews();
		}
		if(mVelocityTracker != null){
			mVelocityTracker.recycle();
		}
		if(progressFootLayout != null){
			progressFootLayout.removeAllViews();
		}

		//mScroller = null;
		titleView = null;
		titleViewSub = null;
		mVelocityTracker = null;
		progressFootLayout = null;

		fixedRowSelect = null;
		mVelocityTracker = null;
		mLayoutParams = null;
	}
	
	public void onItemClick(AdapterView<?> parent, View view, int position, long id){
		if(fixedRowSelect != null ){
			fixedRowSelect.onRowSelected(view.getId());
		}
		if(fixedRowSelectListener != null){
			fixedRowSelectListener.onRowSelected(view, position);
		}
	}
	
	private void init(){
		
		try{
//			defaultBG = getResources().getDrawable(R.drawable.listview_selector);
			//setDescendantFocusability(focusability);
			super.setOnScrollListener(this);
			final ViewConfiguration configuration = ViewConfiguration.get(getContext());
			mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
			setOnItemClickListener(this);
			//new Thread(this).start();
			mScroller = new Scroller(UIFixedScrollListView.this.getContext());	
			//setOnScrollListener(this);
			if(isNeedLoading){
				progressFootLayout = new LinearLayout(getContext());
				progressFootLayout.setOrientation(LinearLayout.HORIZONTAL);
				progressFootLayout.setTag(FOOT_TAG);
				progressFootLayout.setBackgroundColor(Color.TRANSPARENT);
				super.addFooterView(progressFootLayout);
			}
		
		}catch(Exception e){
			
			e.printStackTrace();
			
		}
	}
	
	public void addFooterView(View v) {
		progressFootLayout.addView(v);
    }
	
	public void setFooterLayout(int orientation) {
		progressFootLayout.setOrientation(orientation);
	}
	
	public void setTitleView(View view,int width,BaseAdapter adapter){
		this.titleView = view;
		//this.screenWidth = width;
		setAdapter(adapter);
	}
	
	public void setTitleView(View view){
		this.titleView = view;
	}
	
	public View getTitleView(){
		if(titleView == null){
			return null;
		}
		return titleView;
	}
	
	public void setTitleView(View view,View viewSub){
		titleView = view;
		titleViewSub = viewSub;
	}
	
	public void setListAdapter(BaseAdapter adapter){
		setAdapter(adapter);
	}
	
	public void computeScroll() {
		//LogHelper.print("%%%%%%%%%%%%%%%%%" + mScroller.computeScrollOffset());
		if (mScroller != null && mScroller.computeScrollOffset() && titleView != null) {
			final int scrollX = mScroller.getCurrX();
			//LogHelper.print("@@@ curX = " + titleView.getScrollX() + "  moveX = " + scrollX);
//			if (titleView.getScrollX() - Math.abs(scrollX) <= 0){
//				mScroller.abortAnimation();
//				return;
//			}else if (titleView.getScrollX() +  scrollX > maxScrollWidth){
//				mScroller.abortAnimation();
//				return;
//			}			
			for (int i = 0, j = getChildCount(); i < j; i++) {
				try{
					if(getChildAt(i).getTag() == null || !getChildAt(i).getTag().toString().equals(FOOT_TAG)){
						View child = getScrollView(i);
						if (null!=child) {
							child.scrollTo(scrollX, 0);
						}
					}					
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
			titleView.scrollTo(scrollX , 0);	
			//LogHelper.print("@@@@@@ titleView.getScrollX() = " + titleView.getScrollX() + " scrollX = " + scrollX);
			if(titleViewSub != null){
				titleViewSub.scrollTo(scrollX , 0);	
			}
			//Animation end
			if(mScroller.getCurrX() == mScroller.getFinalX()){
//				if(needAutoScroll){
					scrollEnd();
//				}
			}
			invalidate();
		}
	}
	
	protected View getScrollView(int index){
		return ((ViewGroup) ((ViewGroup) getChildAt(index)).getChildAt(0)).getChildAt(1);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (!isEnabled()) {
			 return false;
		}
		
		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement(ev);
		//LogHelper.print("@@@ === getAction = " + ev.getAction());
		if(ev.getAction() == MotionEvent.ACTION_DOWN){
			isDown = 0;
			firstX = ev.getX();
			firstY = ev.getY();
			isMoved = false;
			if(touchEventListener != null){
				final int itemnum = pointToPosition((int)firstX, (int)firstY);			
				if (itemnum != AdapterView.INVALID_POSITION) {
					final ViewGroup item = (ViewGroup) getChildAt(itemnum - getFirstVisiblePosition());
					touchEventListener.touchEvent(item, ev);
				}				
			}
			//notify();
			//return true;
		}else if(ev.getAction() == MotionEvent.ACTION_UP){
			isEnd = true;
			if(isDown == 2){				
				final VelocityTracker velocityTracker = mVelocityTracker;
				velocityTracker.computeCurrentVelocity(1000,mMaximumVelocity);
				//int initialVelocity = (int) velocityTracker.getXVelocity(-1);
				int velocityX = (int) velocityTracker.getXVelocity();

				isScrollMove = true;
				mScroller.fling(titleView.getScrollX(), titleView.getScrollY(), -velocityX, 0, 0,maxScrollWidth, 0, 10);
				//LogHelper.print("###### titleView.getScrollX() = " + titleView.getScrollX() + " maxScrollWidth = " + maxScrollWidth);
				
//				LogHelper.print("velocityX = " + velocityX + " titleView.getScrollX() = " + titleView.getScrollX());
				//awakenScrollBars(mScroller.getDuration());  
				invalidate();
				//
				if (mVelocityTracker != null) {
					mVelocityTracker.recycle();
					mVelocityTracker = null;
				}
				return true;

			}
			restoreItemBG((int)firstX, (int)firstY);
			final int itemnum = pointToPosition((int)firstX, (int)firstY);	
			
			if(touchEventListener != null){
				if (itemnum != AdapterView.INVALID_POSITION) {
					final ViewGroup item = (ViewGroup) getChildAt(itemnum - getFirstVisiblePosition());
					touchEventListener.touchEvent(item, ev);
				}				
			}

			if(addButtonWidth > 0 && firstX < addButtonWidth && !isMoved){
				if(addButtonSelectListener != null){
					addButtonSelectListener.onButtonKeyDown(itemnum);
					isDown = -1;
//					if(StackController.getInstance().getTopView() != null && StackController.getInstance().getTopView().getBaseActivity() != null){
//						StackController.getInstance().getTopView().getBaseActivity().listViewTouchDown();
//					}
					return true;
				}
			}
			
//			if(StackController.getInstance().getTopView() != null && StackController.getInstance().getTopView().getBaseActivity() != null){
//				StackController.getInstance().getTopView().getBaseActivity().listViewTouchDown();
//			}
			
			isDown = -1;
			touchCancel();
		}else if(ev.getAction() == MotionEvent.ACTION_MOVE){
			final float offX = Math.abs(ev.getX() - firstX);
			final float offY = Math.abs(ev.getY() - firstY);
			
			//为了处理点击时手指稍微滑动一下就判断为移动的问题
			if(offX>marginWidth){
				isMoved = true;
			}

			if(addButtonWidth > 0 && offX < addButtonWidth && Math.abs(ev.getY() - firstY) < 10){
				return true;
			}
			//Toast.makeText(getContext(), Math.abs(ev.getX() - firstX) + " y = " + Math.abs(ev.getY() - firstY), 800).show();
			if(offX > offY && offX  > marginWidth && isDown == 0){
				isDown = 2;
			}else if(offY >= marginWidth && isDown == 0){
				isDown = 1;
				restoreItemBG((int)firstX, (int)firstY);
			}else{
				if(isDown == 2){
					scrollRow(ev.getX());
					final int itemnum = pointToPosition((int)ev.getX(), (int)ev.getY());			
					if(touchEventListener != null){
						if (itemnum != AdapterView.INVALID_POSITION) {
							final ViewGroup item = (ViewGroup) getChildAt(itemnum - getFirstVisiblePosition());
							touchEventListener.touchEvent(item, ev);
						}				
					}
					return true;
				}else{
					return super.onTouchEvent(ev);
				}
			}
		}else if(ev.getAction() == MotionEvent.ACTION_CANCEL){
			isEnd = true;
			restoreItemBG((int)firstX, (int)firstY);
			final int itemnum = pointToPosition((int)ev.getX(), (int)ev.getY());			
			if(touchEventListener != null){
				if (itemnum != AdapterView.INVALID_POSITION) {
					final ViewGroup item = (ViewGroup) getChildAt(itemnum - getFirstVisiblePosition());
					touchEventListener.touchEvent(item, ev);
				}				
			}
//			if(addButtonWidth > 0 && firstX < addButtonWidth && !isMoved){
//				if(addButtonSelectListener != null){
//					addButtonSelectListener.onButtonKeyDown(itemnum);
//				}
//			}
			isDown = -1;
			touchCancel();
		}
		return super.onTouchEvent(ev);
	}
	
	private void touchCancel() {
		for (int i = 0, j = getChildCount(); i < j; i++) {
			try {
				if (getChildAt(i).getTag() == null
						|| !getChildAt(i).getTag().toString().equals(FOOT_TAG)) {
					getChildAt(i).setBackgroundColor(bgColor);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		if (touchCancelListener != null) {
			touchCancelListener.onTouchCancel();
		}
	}

	
	public void setAddButtonWidth(int width){
		addButtonWidth = width;
	}
	
	
	public boolean isHaveHeader = false;
	private boolean bg = false;
	private void changeItemBG(int x, int y){
		int itemnum = pointToPosition(x, y);
		if(isHaveHeader && itemnum == 0){
			itemnum = AdapterView.INVALID_POSITION;
		}
		if (itemnum != AdapterView.INVALID_POSITION) {
			final ViewGroup item = (ViewGroup) getChildAt(itemnum - getFirstVisiblePosition());
			try {
				if(isDown != 2 && isDown != 1 && !isEnd){
					final ViewGroup view = (ViewGroup)item;
					if(isSelected)
						view.setBackgroundDrawable(defaultBG);
					bg = true;
					//LogHelper.print("@@@ === " + "listview_selector");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void restoreItemBG(int x, int y){
		if(!bg){
			return;
		}
		//long time = System.currentTimeMillis();
		
		final int itemnum = pointToPosition(x, y);			
		if (itemnum != AdapterView.INVALID_POSITION) {
			final ViewGroup item = (ViewGroup) getChildAt(itemnum - getFirstVisiblePosition());
			item.setBackgroundColor(bgColor);	
			if(touchEventListener != null){
				MotionEvent me = MotionEvent.obtain(
						0, 0, MotionEvent.ACTION_CANCEL, x, y, 0, 0, 0, 0, 0, 0, 0);
				touchEventListener.touchEvent(item, me);
				me.recycle();
			}

		}
		//LogHelper.print("######### === "  + (System.currentTimeMillis() - time));
	}
	
	/**
	 * handler处理事件
	 */
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if(msg.what == HIDE_FOOT_VIEW){
				if(progressFootLayout != null){
					progressFootLayout.removeAllViews();
					progressFootLayout.setVisibility(View.GONE);
				}	
			}
			else{
				if(isDown != 2 && isDown != 1 && !isEnd){
					final ViewGroup view = (ViewGroup)msg.obj;
					if(isSelected)
						view.setBackgroundDrawable(defaultBG);
					bg = true;
					//LogHelper.print("@@@ === " + "listview_selector");
				}
			}
		}
	};
	/**
	 * 发送message
	 * @param arg 
	 */
	public void sendMessage(int what){
		Message message = handler.obtainMessage();  
		message.what = what;
		if(what == 0){
			handler.sendMessage(message);
		}else{
			handler.sendMessageDelayed(message, 80);
		}		
	}
	/**
	 * 发送message
	 * @param arg 
	 */
	public void sendMessage(Object obj,int time){
		Message message = handler.obtainMessage();
		message.what = -1;
		message.obj = obj;
		handler.sendMessageDelayed(message, time);
	}
	
	/** �ַ������¼� */
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (!isEnabled()) {
			 return false;
		}
		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement(ev);		
		
		if(ev.getAction() == MotionEvent.ACTION_DOWN){
			isEnd = false;
			isDown = 0;
			firstX = ev.getX();
			firstY = ev.getY();
			isMoved = false;
			if(mScroller != null && !mScroller.isFinished()){
				mScroller.abortAnimation();
			}
			//set bg
			if(addButtonWidth > 0 && firstX < addButtonWidth){
				
			}else{
				try {
					changeItemBG((int)ev.getX(), (int)ev.getY());
				} catch (Exception e) {
					e.printStackTrace();
				}
//				final int itemnum = pointToPosition((int)ev.getX(), (int)ev.getY());
//				if (itemnum != AdapterView.INVALID_POSITION) {
//					final ViewGroup item = (ViewGroup) getChildAt(itemnum - getFirstVisiblePosition());
//					sendMessage(item,80);
//				}
			}
			

			//keyDownTime = System.currentTimeMillis();
			//return true;
		}else if(ev.getAction() == MotionEvent.ACTION_CANCEL){
			isEnd = true;
		}else if(ev.getAction() == MotionEvent.ACTION_UP){
			isEnd = true;
		}
//		else if(ev.getAction() == MotionEvent.ACTION_UP){
//			if(isDown == 2){
//
//				final VelocityTracker velocityTracker = mVelocityTracker;
//				velocityTracker.computeCurrentVelocity(1000);
//				int velocityX = (int) velocityTracker.getXVelocity();
//				mScroller.startScroll(0, 0, - (velocityX / 120), 0, Math.abs(velocityX) / 4);
//				invalidate();					
//				return true;
//			}
//			
//			isDown = -1;
//		}else if(ev.getAction() == MotionEvent.ACTION_MOVE){
//			if(Math.abs(ev.getX() - firstX) >= 4 && isDown == 0){
//				isDown = 2;
//			}else if(Math.abs(ev.getY() - firstY) >= 3 && isDown == 0){
//				isDown = 1;
//			}else{
//				if(!isMoved){
//					final int itemnum = pointToPosition((int)ev.getX(), (int)ev.getY());
//					if (itemnum != AdapterView.INVALID_POSITION) {
//						final ViewGroup item = (ViewGroup) getChildAt(itemnum - getFirstVisiblePosition());
//						item.setBackgroundColor(listViewBG);
//					}
//				}
//				isMoved = true;
//				//set bg
//				if(Math.abs(ev.getX() - firstX) < Math.abs(ev.getY() - firstY) && isDown == 0 ){
//					isDown = 1;
//				}else if(Math.abs(ev.getX() - firstX) >= Math.abs(ev.getY() - firstY) && isDown == 0){
//					isDown = 2;
//				}
//
//				if(isDown == 2){
//					scrollRow(ev.getX());
//					return false;
//				}
//			}						
//		}
	    return false;
	}
	
	private boolean isScrollMove = false;
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
					//scrollToPostion(item.getRight()- titleView.getScrollX() - titleView.getWidth());
				}else{
					x = item.getLeft()- titleView.getScrollX() - titleView.getWidth();
					mScroller.startScroll(titleView.getScrollX(), 0,  x, 0, 500);
					//mScroller.fling(titleView.getScrollX(), titleView.getScrollY(), x, 0, 0,maxScrollWidth, 0, 10);
					postInvalidate();
					//隐藏item
					//scrollToPostion(item.getLeft()- titleView.getScrollX() - titleView.getWidth());
				}
				isScrollMove = false;
				break;
			}
		}	
	}
	
//	private void scrollToPostion(int x){
//		for (int i = 0, j = getChildCount(); i < j; i++) {
//			try{
//				if(getChildAt(i).getTag() == null || !getChildAt(i).getTag().toString().equals(FOOT_TAG)){
//					final View child = getScrollView(i);
//					if (null!=child) {
//						child.scrollBy(x, 0);
//					}
//					getChildAt(i).setBackgroundColor(bgColor);
//				}
//			}catch(Exception ex){
//				ex.printStackTrace();
//			}			
//		}
//		//滚动title
//		//titleView.scrollBy(1150 , 0);
//		titleView.scrollBy(x , 0);
//	}

	private synchronized void scrollRow(float x){
		if(titleView == null){
			return;
		}
		getMaxScrollWidth();
		
		int moveX = (int) (x - firstX);		
		
		final int curX =  titleView.getScrollX();
		//int scrollWidth = getWidth();
		int dx = moveX ;
//		LogHelper.print("~~~~~ = " + curX + "  moveX = " + moveX);
		//����Խ������
		if (curX - moveX < 0){
			restoreItemBG((int)firstX, (int)firstY);
			if(curX <= 0){
				return;
			}else{
				dx = curX;
			}
		}else{
			//dx = -curX;
		}
		if (curX - moveX > maxScrollWidth){
			restoreItemBG((int)firstX, (int)firstY);
			moveX = curX - maxScrollWidth;
			if(curX >= maxScrollWidth){
				return;
			}else{
				dx = curX - maxScrollWidth;
			}
		}

		//滚动每行
		for (int i = 0, j = getChildCount(); i < j; i++) {
			try{
				if(getChildAt(i).getTag() == null || !getChildAt(i).getTag().toString().equals(FOOT_TAG)){
					final View child = getScrollView(i);
					if (null!=child) {
						child.scrollBy(-dx, 0);
					}
					getChildAt(i).setBackgroundColor(bgColor);
				}
			}catch(Exception ex){
				ex.printStackTrace();
			}			
		}
		//滚动title
		//titleView.scrollBy(1150 , 0);
		titleView.scrollBy(-dx , 0);
		//LogHelper.print("~~~~ dx === " + dx + " movex = " + curX );
		if(titleViewSub != null){
			titleViewSub.scrollBy(-dx, 0);
		}
		firstX = x;
		invalidate();
	}
	
	public void scrollToFirst(){
		//滚动每行
		for (int i = 0, j = getChildCount(); i < j; i++) {
			try{
				if(getChildAt(i).getTag() == null || !getChildAt(i).getTag().toString().equals(FOOT_TAG)){
					final View child = ((ViewGroup) ((ViewGroup) getChildAt(i)).getChildAt(0)).getChildAt(1);
					child.scrollTo(0, 0);
				}
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
		titleView.scrollTo(0 , 0);
	}
	
	public void getMaxScrollWidth(){
		if(maxScrollWidth != 0) return;
		final int size = ((ViewGroup)titleView).getChildCount();
		for(int i = 0; i < size;i++){
			final View tempView = ((ViewGroup)titleView).getChildAt(i);
			if(tempView.getVisibility() == View.VISIBLE){
				maxScrollWidth += tempView.getMeasuredWidth();
			}			
		}
		maxScrollWidth += 10;//StringUtils.dipToPx(10)
		maxScrollWidth -= titleView.getWidth();
	}
	
	public void getMaxScrollWidth(int size){
		for(int i = 0; i < size;i++){
			final View tempView = ((ViewGroup)titleView).getChildAt(i);
			if(tempView.getVisibility() == View.VISIBLE){
				maxScrollWidth +=  tempView.getMeasuredWidth();
			}			
		}
		maxScrollWidth -= titleView.getWidth();
	}
	
	public void setShadowLineWidth(int w){
		shadowWidth = w;
	}
	
//	protected void dispatchDraw(Canvas canvas) {
//		super.dispatchDraw(canvas);
//		if(titleView != null && titleView.getWidth() > 0){
//			getWholeWidth();
//			final Paint p = new Paint();
//			//Separate view_line
//			p.setColor(0x50000000);
//			canvas.drawRect(offWidth==0?StringUtils.dipToPx(shadowWidth):offWidth, 0, StringUtils.dipToPx(shadowWidth) + 10, getHeight(), p);
//			//Scroll bar
//			p.setColor(0xFF626262);
//			final float offSetX = titleView.getScrollX() * ( ((float)titleView.getMeasuredWidth() - 60)/ 
//					((float)(wholeWidth - titleView.getWidth())));
//			//titleView.getScrollX() ;
//			
//			canvas.drawRect(StringUtils.dipToPx(shadowWidth) + offSetX, 0, StringUtils.dipToPx(shadowWidth) + 60 + offSetX, StringUtils.dipToPx(2), p);
//		}
//	}
	
	public void refreshList(){
		maxScrollWidth = 0;
		getMaxScrollWidth();		
		titleView.postInvalidate();
		wholeWidth = 0;		
	}
	
	public void getWholeWidth(){
		if(wholeWidth == 0){
			final ViewGroup group = (ViewGroup)titleView;
			for (int i = 0; i < group.getChildCount(); i++) {
				final View tempView = group.getChildAt(i);
				if(tempView.getVisibility() == View.VISIBLE){
					wholeWidth += group.getChildAt(i).getWidth();
				}				
			}
		}
	}
	
	public void setWidthAndColor(int w, int c){
		offWidth = w;
	}
	
	/**
	 * 构建底部进度条
	 */
	public void buildProgressFoot() {
		if(progressFootLayout.getChildCount() == 0){
			progressFootLayout.setVisibility(View.VISIBLE);
			ProgressBar progressBar = new ProgressBar(getContext());
	        progressBar.setPadding(0, 0, 15, 0);
//	        progressBar.setIndeterminateDrawable(getResources().getDrawable(R.drawable.progressbar));
	        if(mLayoutParams == null){
	        	mLayoutParams = new LinearLayout.LayoutParams(
	                    LinearLayout.LayoutParams.WRAP_CONTENT,
	                    LinearLayout.LayoutParams.WRAP_CONTENT);	
	        }
	        
	        progressFootLayout.addView(progressBar, mLayoutParams);
	        TextView textView = new TextView(getContext());
	        textView.setTextColor(Color.WHITE);
	        textView.setText("Loading...");
	        textView.setGravity(Gravity.CENTER_VERTICAL);
	        progressFootLayout.addView(textView, new LinearLayout.LayoutParams(
	                LinearLayout.LayoutParams.FILL_PARENT,
	                LinearLayout.LayoutParams.FILL_PARENT));
	        progressFootLayout.setGravity(Gravity.CENTER);			
		}
	}
	
	public void removeFootView(){
		if(progressFootLayout != null && progressFootLayout.getParent() != null){			
			sendMessage(HIDE_FOOT_VIEW);
		}
		
	}
	
	public void setFixedRowSelect(FixedRowSelect fixedRowSelect){
		this.fixedRowSelect = fixedRowSelect;
	}
	
	private int firstItemIndex;
	private boolean isHideFootView = false;
	
//	public void InvokeOnMainThread(int tag){
//	
//		//progressFootLayout.getLayoutParams().height = 0;
//		//progressFootLayout.setVisibility(View.GONE);
//	}	
	
	public void InvokeOnMainThread(Object args){
		if(args != null){
			if(isDown != 2 && isDown != 1 && !isEnd){
				final ViewGroup view = (ViewGroup)args;
				if(isSelected)view.setBackgroundDrawable(defaultBG);
//				view.setBackgroundResource(defaultBG);
				//LogHelper.print("@@@ === " + "listview_selector");
			}
		}
	}
	

	
	//����footView
	public void hideFootView() {
		if(!isHideFootView){
			//MainUIActivity.getMainController().sendMessage(this, HIDE_FOOT_VIEW);
			isHideFootView = true;					 
		}
	}
	
	public void setItemBackground(int sid){
		defaultBG = getResources().getDrawable(sid);
	}
	
	public void setItemBackground(Drawable bg){
		defaultBG = bg;
	}
	
//	private int tempY = -1;
//	private boolean isLoop = true;
//	private Object obj = new Object();
//	public void run(){
//		try{
//			while(isLoop){
//				synchronized(obj){
//					if(isNeedRun){
//						Thread.sleep(200);
//						if(tempY == -1){
//							tempY = getChildAt(0).getTop();
//							//wait();
//						}else{
//							if(tempY == getChildAt(0).getTop()){
//								//wait();
//								
//								isNeedRun = false;
//								if(StackController.getInstance().getTopView()!= null && StackController.getInstance().getTopView().getBaseActivity() != null){
//									StackController.getInstance().getTopView().getBaseActivity().scrollEndEvent();
//								}
//							}else{
//								tempY = getChildAt(0).getTop();
//							}
//						}
//					}else{
//						Thread.sleep(1000);
//						
//					}
//				}				
//			}			
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//	}
	
	/**
     * 重置滚动列表的状态
     * 
     */
	public void reset() {
		if(mScroller != null){
			mScroller.abortAnimation();
		}
		maxScrollWidth = 0;
		wholeWidth = 0;
		if (titleView != null) {
			titleView.scrollTo(0, 0);
		}
		if (titleViewSub != null) {
			titleViewSub.scrollTo(0, 0);
		}
	}
    
	public interface FixedRowSelect{
		void onRowSelected(int index);
	}
	
	public interface FixedRowSelectListener{
		void onRowSelected(View view,int index);
	}
	
	public void setAddRowSelectListener(FixedRowSelectListener addButtonSelectListener){
		this.fixedRowSelectListener = addButtonSelectListener;
	}
	
	public interface AddButtonSelectListener{
		void onButtonKeyDown(int id);
	}
	
	public void setAddListener(AddButtonSelectListener addButtonSelectListener){
		this.addButtonSelectListener = addButtonSelectListener;
	}

	public interface TouchCancelListener {
		void onTouchCancel();
	}

	private TouchCancelListener touchCancelListener;

	public void setTouchCancelListener(TouchCancelListener touchCancelListener) {
		this.touchCancelListener = touchCancelListener;
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		if (adapterIsInit && !isInitFirst) {
			// 第一次给出显示位置
			if (totalItemCount > 0 && visibleItemCount > 0 && scrollStateChangeListener!=null) {
				
				int visibleFirst = firstVisibleItem;
				int visibleLast = firstVisibleItem + visibleItemCount > totalItemCount ? firstVisibleItem
						+ visibleItemCount - 2
						: firstVisibleItem + visibleItemCount - 1;
				if(visibleLast > 0){
					isInitFirst = true;
					scrollStateChangeListener.onItemsVisible(visibleFirst,
							visibleLast, true);
				}else if(firstVisibleItem == 0 && visibleItemCount == 1 && totalItemCount == 1){
					isInitFirst = true;
					scrollStateChangeListener.onItemsVisible(0,1, true);
				}
			}
		}
		
	}
	
	private int itemsCount;
	private int startFirst;
	private int startLast;
	private int endFirst;
	private int endLast;
	/**
	 * 0.开始滚动 1.isFling 2.SCROLL_STATE_IDLE
	 */
	public int isFlingState = 0;
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {

		if (scrollState == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
			// 开始滚动监听
			if (isFlingState != 1) {
				startFirst = view.getFirstVisiblePosition();
				startLast = view.getLastVisiblePosition();
				isFlingState = 0;
			}

		} else if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
			if (isFlingState != 2) {
				scrollStateIdle(view);
				isFlingState = 2;
			}
		} else if (scrollState == OnScrollListener.SCROLL_STATE_FLING) {
			isFlingState = 1;
			if (view.getFirstVisiblePosition() == 0
					|| view.getLastVisiblePosition() == view.getCount() - 1) {
				// 增加冗余判断,是否已滚动到顶端或者底端,解决MOTO手机等特殊机型问题
				// 滚动停止之后监听不了SCROLL_STATE_IDLE(主要体现在滚动到最上或者最下时)
				scrollStateIdle(view);
				isFlingState = 2;
			}
		}
		
	
	}
	
	private void scrollStateIdle(AbsListView view) {
		if (scrollStateChangeListener != null) {
			// 滚动停止监听
			endFirst = view.getFirstVisiblePosition();
			endLast = view.getLastVisiblePosition();
			int visibleFirst;
			int visibleLast;
			boolean isUp = false; // true.up false.down
			if (startFirst == endFirst && startLast == endLast) {
				// 2.完全重合 TODO
//				scrollStateChangeListener.onItemsDisappear(-1, -1);
//				scrollStateChangeListener.onItemsVisible(-1, -1);
			} else  {
				visibleFirst = endFirst;
				visibleLast = endLast;
				scrollStateChangeListener.onItemsVisible(visibleFirst,
						visibleLast, isUp);
			}
		}
	}
	
	
	private ScrollStateChangeListener scrollStateChangeListener;

	public void setScrollStateChangeListener(
			ScrollStateChangeListener scrollStateChangeListener) {
		this.scrollStateChangeListener = scrollStateChangeListener;
	}
	
	public interface ScrollStateChangeListener {
		
		// -1 代表没有变化  isUp: true.Up   false.down
		public void onItemsDisappear(int startPosition, int endPosition, boolean isUp);
		
		public void onItemsVisible(int startPosition, int endPosition, boolean isUp);
	}

    public void setBgColor(int bgColor) {
        this.bgColor = bgColor;
    }
}
