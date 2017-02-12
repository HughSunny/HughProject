package com.hugh.work.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

import com.hugh.work.RunnableManager;
import com.hugh.work.handler.ProgressListenHandler;
import com.hugh.work.network.RequestBack;

public abstract class SetActivity extends Activity implements RequestBack {
	protected ProgressListenHandler handler = new ProgressListenHandler(this, this);
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onPause(){
		super.onPause();
		quit();
	}
	
	protected void quit(){
		RunnableManager.getInstance().clearCallBack(this);
		if (handler != null) {
			handler.removeCallbacks(null);
			handler.quit();
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		//onRestoreInstanceState的bundle参数也会传递到onCreate方法中，你也可以选择在onCreate方法中做数据还原。
		//onRestoreInstanceState被调用的前提是，activity A“确实”被系统销毁了，而如果仅仅是停留在有这种可能性的情况下，则该方法不会被调用
	}
}
