package com.set.ui.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import com.set.R;

public class LongPressButton extends View {
	private final String TAG ="LongPressButton";
	private long mStartTime; //记录长按开始
	private int mRepeatCount; //重复次数计数
	private long mInterval = 500;
	private RepeatListener mListener;
	private Paint paint;
	private boolean isfocused;
	private boolean faceup = true;
	private Drawable add,reduce;
	int totalWidth;
	int totalHeight;
	int corner = 5;
	
	private Shader shaderUp;
	private Shader shaderDown;
	private Shader shaderFocusUp;
	private Shader shaderFocusDown;
	public LongPressButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		paint = new Paint();
		paint.setAntiAlias(true);
		setFocusable(true); //允许获得焦点
		setLongClickable(true); //启用长按事件
		
		Resources res = context.getResources();
		add = res.getDrawable(R.drawable.picker_add);
		reduce = res.getDrawable(R.drawable.picker_reduce);
		
		shaderDown =new LinearGradient(0,0,0,totalHeight/2,   
				new int[]{0xFF666666,Color.DKGRAY},   
				null,Shader.TileMode.CLAMP); 
		
		shaderUp =new LinearGradient(0,totalHeight/2,0,totalHeight,   
				new int[]{Color.DKGRAY,0xFF666666},
				null,Shader.TileMode.CLAMP); 
		
		shaderFocusUp = new LinearGradient(0,totalHeight/2,0,totalHeight,   
				new int[]{0xFF3399FF,0xFF666666},
				null,Shader.TileMode.CLAMP); 
	
		shaderFocusDown =new LinearGradient(0,0,0,totalHeight/2,   
				new int[]{0xFF3399FF,Color.DKGRAY},   
				null,Shader.TileMode.CLAMP); 
		
		this.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus == true) {
					isfocused = true;
					Log.d(TAG, "-------hasFocus is true");
					invalidate();
				}else{
					Log.d(TAG, "-------hasFocus is false");
					isfocused = false;
					invalidate();
				}
			}
		});
	}
	
	public void setStyle(boolean isup){
		faceup = isup;
	}
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		totalWidth = getMeasuredWidth();
		totalHeight = getMeasuredHeight();
		paint.setStyle(Style.FILL);
		if(faceup){
			if(isfocused){
				paint.setShader(shaderFocusUp);
			}else{
				paint.setShader(shaderUp);
			}
		
			canvas.drawRoundRect(new RectF(0,0,totalWidth,totalHeight+corner), corner, corner, paint);
			
			int w = add.getIntrinsicWidth();
			
			int h = add.getIntrinsicHeight();
			
			add.setBounds(totalWidth/2 - w/2, totalHeight/2 - h/2, totalWidth/2 + w/2, totalHeight/2 + h/2);
			
			add.draw(canvas);
			
		}else{
			if(isfocused){
				paint.setShader(shaderFocusDown);
			}else{
				paint.setShader(shaderDown);
			}
			canvas.drawRoundRect(new RectF(0,-corner,totalWidth,totalHeight), corner, corner, paint);
			
			int w = reduce.getIntrinsicWidth();
			int h = reduce.getIntrinsicHeight();
			reduce.setBounds(totalWidth/2 - w/2, totalHeight/2 - h/2, totalWidth/2 + w/2, totalHeight/2 + h/2);
			reduce.draw(canvas);
//			paint.setShader(null);
//			paint.setTextSize(textSize);
//			paint.setColor(Color.GRAY);
//			canvas.drawText("-", totalWidth/2, totalHeight/2+textSize, paint);
		}
		
		if(isfocused){
			paint.setStyle(Style.STROKE);
			paint.setStrokeWidth(1);
			paint.setColor(Color.YELLOW);
			canvas.drawRoundRect(new RectF(0,0,totalWidth,totalHeight+corner), corner, corner, paint);
		}
		
	}

	public void setRepeatListener(RepeatListener l, long interval) { //实现重复按下事件listener
		mListener = l;
		mInterval = interval;
	}

	@Override
	public boolean performLongClick() {
		mStartTime = SystemClock.elapsedRealtime();
		mRepeatCount = 0;
		post(mRepeater);
		return true;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_UP) { // 本方法原理同onKeyUp的一样，这里处理屏幕事件，下面的onKeyUp处理Android手机上的物理按键事件
			removeCallbacks(mRepeater);
			if (mStartTime != 0) {
				doRepeat(true);
				mStartTime = 0;
			}
		}
		return super.onTouchEvent(event);

	}

	//处理导航键事件的中键或轨迹球按下事件

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_CENTER:
		case KeyEvent.KEYCODE_ENTER:
			super.onKeyDown(keyCode, event);
			return true;
		}
		return super.onKeyDown(keyCode, event);

	}

	//当按键弹起通知长按结束

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_CENTER:
		case KeyEvent.KEYCODE_ENTER:
			removeCallbacks(mRepeater); //取消重复listener捕获
			if (mStartTime != 0) {
				doRepeat(true); //如果长按事件累计时间不为0则说明长按了
				mStartTime = 0; //重置长按计时器
			}
		}
		return super.onKeyUp(keyCode, event);
	}



	private Runnable mRepeater = new Runnable() { //在线程中判断重复
		public void run() {
			doRepeat(false);
			if (isPressed()) {
				postDelayed(this, mInterval); //计算长按后延迟下一次累加
			}
		}
	};

	private void doRepeat(boolean last) {
		long now = SystemClock.elapsedRealtime();
		if (mListener != null) {
			mListener.onRepeat(this, now - mStartTime, last ? -1 : mRepeatCount++);
		}
	}

	public interface RepeatListener {
		void onRepeat(View v, long duration, int repeatcount); 
		//参数一为用户传入的Button对象，参数二为延迟的毫秒数，第三位重复次数回调。
	}
}

