package com.set.listener;

import android.view.MotionEvent;
import android.view.View;

public interface TouchEventListener {
	public abstract void touchEvent(View view, MotionEvent event);
}
