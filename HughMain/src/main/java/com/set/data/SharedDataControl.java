package com.set.data;

import com.set.app.MainApp;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharedDataControl {
	private static SharedPreferences preferences;
	private static Editor editor;
	private static final String PREFERENCES_NAME = "setting";
	
	private static SharedDataControl share = null;
	
	public static SharedDataControl getInstance() {
		if (share == null) {
			share = new SharedDataControl();
			createPerferences(MainApp.getInstance());
		}
		return share;
	}
	private static void createPerferences(Context context) {
		if (preferences == null) {
			preferences = context.getSharedPreferences(PREFERENCES_NAME, 0);
		}
		if (editor == null) {
			editor = preferences.edit();
		}
	}
	
	private void saveData(String key, Object value){
		
	}
}
