package com.set.listener;

import android.view.View;

public interface RepeatListener {
	/**
     * @param v 用户传入的Button对象 
     * @param duration 延迟的毫秒数 
     * @param repeatcount 重复次数回调 
     */  
    void onRepeat(View v, long duration, int repeatcount);  
}
