package com.test;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

/**
 * ClassName:WebJSTestActivity
 * Function: TODO ADD FUNCTION
 *
 * @author   wl
 * @version  
 * @Date	 2015-4-9		下午4:08:57
 */
public class WebJSTestActivity extends Activity {
	WebView contentWebView;
	@Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        contentWebView = new WebView(this);
        setContentView(contentWebView);  
        // 启用javascript  
        contentWebView.getSettings().setJavaScriptEnabled(true);  
        // 从assets目录下面的加载html  
        contentWebView.loadUrl("http://192.168.21.124/NurseMobileWebService/download/内科一护理单元/患者护理评估记录单.html");  
        contentWebView.addJavascriptInterface(new AndroidBridge(), "JavaScriptInterface");  
    } 
	
	class  AndroidBridge{
		public String getNursingName(){  
			return "chenyuxi";
		}
		
		public void callFromAndroid(){
			try {
//				web.loadUrl("javascript:getNurseNameFromAndroid('"+application.getUser().getName()+"')");
			} catch (Exception ex) {
				Log.d("cyxlog", ex.toString());
			}
		}
	}
	
}

