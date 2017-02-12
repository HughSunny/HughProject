package com.hugh.work.app;

import android.app.Application;

import com.hugh.work.utils.EquipmentInfo;


public class BaseApp extends Application {
	public int currentapiVersion;
	public static boolean Debug = false;
	public String APP_NAME = "App";
	@Override
	public void onCreate() {
		super.onCreate();
		currentapiVersion=android.os.Build.VERSION.SDK_INT;
		EquipmentInfo info = new EquipmentInfo(this, APP_NAME);
	}
}
