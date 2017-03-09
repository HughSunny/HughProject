package com.set.app;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

import set.work.app.BaseApp;

public class MainApp extends BaseApp{
	private static MainApp application;
	public boolean ispad;

	public MainApp() {
		application = this;
	}

	public static MainApp getInstance() {
		if (application == null) {
			application = new MainApp();
		}
		return application;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		ispad = isTablet(this);// ture是pad，false是pda
		BaseApp.Debug = true;
		APP_NAME = "DaShun";
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	public static boolean isTablet(Context context) {
		return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
	}

}
