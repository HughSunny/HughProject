package com.set.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;

import com.set.R;
import com.set.ui.view.LongPressButton.RepeatListener;
import com.set.ui.view.wheel.NumericWheelAdapter;
import com.set.ui.view.wheel.OnWheelChangedListener;
import com.set.ui.view.wheel.WheelView;

public class CustomWheel extends RelativeLayout implements RepeatListener, OnClickListener {
	private LayoutInflater minflater;
	private WheelView wheel;
	private LongPressButton add;
	private LongPressButton reduce;
	private boolean isLeft;
	public CustomWheel(Context context, AttributeSet attrs) {
		super(context, attrs);
		minflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		minflater.inflate(R.layout.custom_wheel, this);
		wheel = (WheelView)findViewById(R.id.wheel);
		
		add = (LongPressButton)findViewById(R.id.add);
		add.setOnClickListener(this);
		add.setRepeatListener(this, 200);
		reduce = (LongPressButton)findViewById(R.id.reduce);
		reduce.setStyle(false);
		reduce.setRepeatListener(this, 200);
		reduce.setOnClickListener(this);
		
		add.setOnKeyListener(keyListener);
		reduce.setOnKeyListener(keyListener);
	}
	
	
	public void setLeft(){
		isLeft = true;
	}
	
	@Override
	public void onClick(View v) {
		if(v == add){
			wheel.scroll(1,200);
		}else if(v == reduce){
			wheel.scroll(-1,200);
		}
		invalidate();
	}
	private OnKeyListener keyListener = new OnKeyListener() {
		
		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			if(v==add&&isLeft){
				if(event.getAction()==KeyEvent.ACTION_DOWN){
					if(keyCode==KeyEvent.KEYCODE_DPAD_LEFT){
						add.setNextFocusLeftId(R.id.add);
					}
				}
			}else if(v==reduce&&isLeft){
				if(event.getAction()==KeyEvent.ACTION_DOWN){
					if(keyCode==KeyEvent.KEYCODE_DPAD_LEFT){
						reduce.setNextFocusLeftId(R.id.reduce);
					}
				}
			}
			return false;
		}
	};

	@Override
	public void onRepeat(View v, long duration, int repeatcount) {
		if(v == add){
			System.out.println("add ----");
			wheel.scroll(1,100);
		}else if(v == reduce){
			System.out.println("reduce ----");
			wheel.scroll(-1,100);
		}
	}
	
	

	public void setCyclic(boolean b) {
		wheel.setCyclic(b);
	}


	public void setAdapter(NumericWheelAdapter numericWheelAdapter) {
		wheel.setAdapter(numericWheelAdapter);
	}


	public void setLabel(String string) {
		wheel.setLabel(string);
	}


	public void setCurrentItem(int i) {
		wheel.setCurrentItem(i);
	}


	public int getCurrentItem() {
		return wheel.getCurrentItem();
	}


	public void scroll(int i, int j) {
		wheel.scroll(i, j);
	}


	public void addChangingListener(OnWheelChangedListener wheelListener_year) {
		wheel.addChangingListener(wheelListener_year);
		
	}
	
}
