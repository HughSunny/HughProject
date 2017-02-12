package com.set.ui.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.set.R;
/**
 *	作用&目的：自定义tab按钮
 * 	作者：石晓星
 * 	编写日期：2014-7-16
 */
public class CustomTabView extends LinearLayout implements  OnClickListener {
	
	private static int imageLeftFocusID = R.drawable.tab_left;
	private static int imageRightFocusID =  R.drawable.tab_right;
	private static final int FOCUS_COLOR = Color.WHITE;
	private static final int UNFOCUS_COLOR = Color.parseColor("#4169E1");
	private TextView leftText;
	private TextView rightText;
	private static boolean isLeft = true ;
	private LinearLayout layout;
	public CustomTabView(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.custom_tab_button, this);
		leftText  = (TextView)findViewById(R.id.left_btn);
		rightText  = (TextView)findViewById(R.id.right_btn);
		leftText.setOnClickListener(this);
		rightText.setOnClickListener(this);
		layout = (LinearLayout)findViewById(R.id.custom_tab_layout);
		isLeft = true;
		setFocus(isLeft);
	}
	
	public void setText(String leftTxt, String rightTxt) {
		leftText.setText(leftTxt);
		rightText.setText(rightTxt);
	}
	
	public void setImageResource(int leftFocusImage, int rightFocusImage) {
		imageLeftFocusID = leftFocusImage;
		imageRightFocusID = rightFocusImage;
	}
	
	@Override
	public void onClick(View v) {	
		if (!(v instanceof TextView)) {
			return;
		}
		if (v == leftText) {
			if (isLeft) {
				return;
			}
			setFocus(true);
			isLeft = true;
		} else if (v == rightText) {
			if (!isLeft) {
				return;
			}
			setFocus(false);
			isLeft = false;
		}
		if (listener != null) {
			listener.onTabClick(isLeft);
		}
	}
	
	private void setFocus(boolean isLeft) {
		if (isLeft) {
			layout.setBackgroundResource(imageLeftFocusID);
			leftText.setTextColor(FOCUS_COLOR);
			rightText.setTextColor(UNFOCUS_COLOR);
		} else {
			layout.setBackgroundResource(imageRightFocusID);
			leftText.setTextColor(UNFOCUS_COLOR);
			rightText.setTextColor(FOCUS_COLOR);
		}
	}
	
	private onTabClickListener listener;
	
	public void setListener(onTabClickListener listener) {
		this.listener = listener;
	}

	public interface onTabClickListener {
		/**
		 * tab被点击 回调
		 * @param left 是否是左边
		 */
		void onTabClick(boolean left);
	}
}
