package com.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import com.set.R;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

public class LocalWebActivity extends Activity {
	private static final String TAG = "WebActivity";
	private WebView mWebView;
	private FrameLayout mFullscreenContainer;
	private FrameLayout mContentView;
	private View mCustomView = null;
	
	String customHtml = "<html>"
			+ "<body><font color='red'>hello baidu!</font>"
			+ "</body>"
			+ "<a target='_blank' href=\"getImages?type=lis&ec=id&id=PBCbNLsoiDLFPjNcoKFS2t7uslWjLRJkmAcTzLwjEAA%3D\"><strong><FONT color=\"red\">查看影像</FONT></strong></a>"
			+ "</html>";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_web);
//		Intent intent = this.getIntent();
//		String title = intent.getCharSequenceExtra("title").toString();
//		int id = intent.getIntExtra("id", -1);
//		if (title != null) {
//			setTitle(title);
//		}
		mFullscreenContainer = (FrameLayout) findViewById(R.id.fullscreen_custom_content);
		mContentView = (FrameLayout) findViewById(R.id.web_content);
		mWebView = (WebView) findViewById(R.id.webview_player);
		WebSettings settings = mWebView.getSettings();
		settings.setJavaScriptEnabled(true);
		settings.setRenderPriority(RenderPriority.HIGH);
		settings.setJavaScriptCanOpenWindowsAutomatically(true);
		settings.setPluginState(PluginState.ON);
		settings.setAllowFileAccess(true);
		settings.setLoadWithOverviewMode(true);
		settings.setSupportZoom(true);
		String baseUrl = "file:///mnt/sdcard/homepage_flash.swf";
//		mWebView.loadUrl(baseUrl);
		String url = "";
		customHtml = getHtmlString();
		mWebView.loadDataWithBaseURL(null, customHtml, "text/html", "utf-8", null);
//		mWebView.setInitialScale(25);//为25%，最小缩放等级 ，可以在这里根据需求来设置缩放比例.  
		mWebView.setWebChromeClient(new MyWebChromeClient());
		mWebView.setWebViewClient(new MyWebViewClient());
	}
	
	private String getHtmlString(){
		String FILE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath(); ;
		String FileName = "RECORD_RIS.txt";
		File file = new File(FILE_PATH, FileName);
		if (file.exists()) {
			return getFileString(file);
		} else {
			return customHtml;
		}
	}
	
	private String getFileString(File file) {
		BufferedReader reader = null;
		String result = "";
		try {
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			while ((tempString = reader.readLine()) != null) {
				 result = result + "\n" + tempString;
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}
		return result;
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		// 重写方法后，我们就调用父类的方法，这样以便系统的方法可以调用,这句一肯不能忘记
		super.onKeyDown(keyCode, event);
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// 现在返回:true,代表让系统能继续处理此按键的操作
			// 返回false:代表该按键的处理到此结束，不响应系统的处理
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		Log.w(TAG, "------>onPause");
		mWebView.pauseTimers();
		if (isFinishing()) {
			mWebView.loadUrl("about:blank");
			Log.w(TAG, "------>onPause   loadUrl(about:blank)");
			setContentView(new FrameLayout(this));
		}
		callHiddenWebViewMethod("onPause");
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.w(TAG, "------>onDestroy");
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		Log.w(TAG, "------>onResume");
		mWebView.resumeTimers();
		callHiddenWebViewMethod("onResume");
	}
	
	
	private void callHiddenWebViewMethod(String name) {
		if (mWebView != null) {
			try {
				Method method = WebView.class.getMethod(name);
				method.invoke(mWebView);
			} catch (NoSuchMethodException e) {
				Log.e(TAG, e.toString());
			} catch (IllegalAccessException e1) {
				Log.e(TAG, e1.toString());
			} catch (InvocationTargetException e2) {
				Log.e(TAG, e2.toString());
			}
		}
	}
	
	private boolean getFlashPlayerInfo(){
		PackageManager pm = getPackageManager();
		List<PackageInfo> infoList = pm
				.getInstalledPackages(PackageManager.GET_SERVICES);
		PackageInfo flashInfo = null;
		for (PackageInfo info : infoList) {
			if ("com.adobe.flashplayer".equals(info.packageName)) {
				flashInfo = info;
				System.out.println("name" + flashInfo.versionName);
			}
		}
		if (flashInfo == null) {
			return false;
		}
		return true;
	}
	
	private int count = 0; 
	
	boolean pageError;
	
class MyWebChromeClient extends WebChromeClient {
		
		private CustomViewCallback mCustomViewCallback;
		private int mOriginalOrientation = 1;
		@Override
		public void onShowCustomView(View view, CustomViewCallback callback) {
			// TODO Auto-generated method stub
			onShowCustomView(view, mOriginalOrientation, callback);
			super.onShowCustomView(view, callback);
			
		}
		
		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			// TODO Auto-generated method stub
			super.onProgressChanged(view, newProgress);
			Log.i(TAG, "newProgress = " + newProgress);
			if (newProgress == 100) {
				count ++;
//				handler.sendEmptyMessageDelayed(0, 22300);
			}
		}
		
		
		public void onShowCustomView(View view, int requestedOrientation,
				WebChromeClient.CustomViewCallback callback) {
			if (mCustomView != null) {
				callback.onCustomViewHidden();
				return;
			}
			if (getPhoneAndroidSDK() >= 14) {
				mFullscreenContainer.addView(view);
				mCustomView = view;
				mCustomViewCallback = callback;
				mOriginalOrientation = getRequestedOrientation();
				mContentView.setVisibility(View.INVISIBLE);
				mFullscreenContainer.setVisibility(View.VISIBLE);
				mFullscreenContainer.bringToFront();

				setRequestedOrientation(mOriginalOrientation);
			}

		}

		public void onHideCustomView() {
			mContentView.setVisibility(View.VISIBLE);
			if (mCustomView == null) {
				return;
			}
			mCustomView.setVisibility(View.GONE);
			mFullscreenContainer.removeView(mCustomView);
			mCustomView = null;
			mFullscreenContainer.setVisibility(View.GONE);
			try {
				mCustomViewCallback.onCustomViewHidden();
			} catch (Exception e) {
			}
			// Show the content view.

			setRequestedOrientation(mOriginalOrientation);
		}

	}

	class MyWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			Log.i(TAG, "shouldOverrideUrlLoading");
			return super.shouldOverrideUrlLoading(view, url);
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
			Log.i(TAG, "onPageStarted");
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			Log.i(TAG, "onPageFinished");
		}

		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			Log.i(TAG, "onReceivedError");
			pageError = true;
			super.onReceivedError(view, errorCode, description, failingUrl);
		}
		
	}

	public static int getPhoneAndroidSDK() {
		// TODO Auto-generated method stub
		int version = 0;
		try {
			version = Integer.valueOf(android.os.Build.VERSION.SDK);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return version;
	}
}
                                              