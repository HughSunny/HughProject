package set.work.app;

import android.app.Application;

import set.work.utils.EquipmentInfo;


public class BaseApp extends Application {
	public String APP_NAME = "App";
	public int currentApiVersion;
	public static boolean Debug = false;
	//是否是被激活状态
	public static boolean isActive;

	@Override
	public void onCreate() {
		super.onCreate();
		currentApiVersion =android.os.Build.VERSION.SDK_INT;
		EquipmentInfo info = new EquipmentInfo(this, APP_NAME);
	}
}
