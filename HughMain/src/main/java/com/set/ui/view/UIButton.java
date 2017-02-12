package com.set.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.TextView;

import com.set.R;
import com.set.listener.TouchEventListener;
import com.set.model.ImageViewModel;
import com.set.model.TextViewModel;

public class UIButton extends TextView implements OnTouchListener {

	private TouchEventListener touchEventListener;
	private LayoutInflater minflater;
	private TextViewModel localTextModel;
	private ImageViewModel localImageModel;
	public UIButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		minflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		TypedArray localTypedArray = context.obtainStyledAttributes(attrs,R.styleable.uibutton);

		int textNormalColor = localTypedArray.getResourceId(R.styleable.uibutton_textNormalColor, Color.WHITE);

		int textFocusColor = localTypedArray.getResourceId(R.styleable.uibutton_textFocusColor, Color.WHITE);
		
		localTextModel = new TextViewModel();
		localTextModel.normalColor = textNormalColor;
		localTextModel.focusColor = textFocusColor;
		
		int imageFocus = localTypedArray.getResourceId(R.styleable.uibutton_imageFocus, -1);

		int imageNormal = localTypedArray.getResourceId(R.styleable.uibutton_imageNormal, -1);
//		String iconwidth = attrs.getAttributeValue("http://schemas.android.com/apk/res/android", "layout_width");
//		String iconheight = attrs.getAttributeValue("http://schemas.android.com/apk/res/android", "layout_height");
		localImageModel = new ImageViewModel(imageNormal,imageFocus);
		
		localTypedArray.recycle();

		setOnTouchListener(this);
		
		setImageModel(localImageModel);
		setTextModel(localTextModel);
	}
	
	public void setImageModel(ImageViewModel imageModel){
		localImageModel = imageModel;
		if(imageModel.iconNormal != -1){
			this.setBackgroundResource(imageModel.iconNormal);
		}
	}
	
	public void setTextModel(TextViewModel textModel){
		if(textModel.label != null){
			setText(textModel.label);
		}
		if(textModel.normalColor != 0){
			setTextColor(textModel.normalColor);
		}
		if(textModel.focusColor != 0){
			setTextColor(textModel.focusColor);
		}
		if(textModel.alignment != 0){
			setGravity(textModel.alignment);
		}
		if(textModel.typeface != -1){
			setGravity(textModel.typeface);
		}
	}

	public void setTouchListener(TouchEventListener _touchEventListener){
		touchEventListener = _touchEventListener;
	}
	
	public void callBackListener(View paramView, MotionEvent paramMotionEvent)
	{
		if (this.touchEventListener == null)
			return;
		this.touchEventListener.touchEvent(paramView, paramMotionEvent);
	}

	public boolean onTouch(View v, MotionEvent event) {
		
		if (event.getAction() == MotionEvent.ACTION_DOWN){
			setFocus(true);
		}else if(event.getAction() == MotionEvent.ACTION_MOVE){
			if (event.getX() < 0||event.getX()>v.getMeasuredWidth()){
				setFocus(false);
				return false;
			}
			if (event.getY() < 0||event.getY()>v.getMeasuredHeight()){
				setFocus(false);
				return false;
			}
		}else if(event.getAction() == MotionEvent.ACTION_UP){
			setFocus(false);
			
		}
		return true;
		
	}

	private void setFocus(boolean isfocus)
	{
		if (localImageModel != null){
			if (isfocus){
				if(localImageModel.iconFocus != -1){
					this.setBackgroundResource(localImageModel.iconFocus);
				}
			}else{
				if(localImageModel.iconNormal != -1){
					this.setBackgroundResource(localImageModel.iconNormal);
				}
			}
		}
		if (localTextModel != null){
			if (isfocus){
				this.setTextColor(localTextModel.focusColor);
			}else{
				this.setTextColor(localTextModel.normalColor);
			}
		}
	}

}
