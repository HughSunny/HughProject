package set.work.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

import set.work.app.BaseApp;
import set.work.utils.ApplicationUtil;

public abstract class SetActivity extends Activity{
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
	protected void onStop() {
		super.onStop();
		if (!ApplicationUtil.isAppOnForeground(this)) {
			//app 进入后台
			//全局变量isActive = false 记录当前已经进入后台
			((BaseApp)getApplication()).isActive = false;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		//if (((BaseApp)getApplication()).isActive) {
		//app 从后台唤醒，进入前台
		//((BaseApp)getApplication()).isActive = true;
		//}
	}

	@Override
	protected void onPause(){
		super.onPause();
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
