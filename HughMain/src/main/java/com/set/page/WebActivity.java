package com.set.page;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.set.R;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 浏览器 activity基类
 * @author Hugh
 */
public class WebActivity extends Activity implements OnClickListener {
	private static final String TAG = "WebActivity";
	protected WebView mWebView;
	private FrameLayout mFullscreenContainer;
	private FrameLayout mContentView;
	private View mCustomView = null;
	private String url = "";
	List<String> tltleList = new ArrayList<String>();
	private boolean isHead = true; 
	private RelativeLayout webTop;
	private TextView titleView;
	private ImageView back;
	private ProgressBar progressbar;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_webwithtitle);
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		String title = null;
		if (bundle != null) {
			url = bundle.getString("url");
			title = bundle.getString("title");
		}
		webTop = (RelativeLayout)findViewById(R.id.web_top);
		titleView = (TextView)findViewById(R.id.web_title_text);
		back = (ImageView)findViewById(R.id.img_back);
		mFullscreenContainer = (FrameLayout) findViewById(R.id.fullscreen_custom_content);
		mContentView = (FrameLayout) findViewById(R.id.web_content);
		mWebView = (WebView) findViewById(R.id.webview_player);
		progressbar = (ProgressBar)findViewById(R.id.web_progress);
		back.setOnClickListener(this);
		if (title != null) {
			webTop.setVisibility(View.VISIBLE);
			titleView.setText(title);
		}
		WebSettings settings = mWebView.getSettings();
		settings.setJavaScriptEnabled(true);
		settings.setJavaScriptCanOpenWindowsAutomatically(true);
		settings.setPluginState(PluginState.ON);
		settings.setAllowFileAccess(true);
		settings.setLoadWithOverviewMode(true);
		settings.setSupportZoom(true);
		if (url != null && !url.equals("")) {
			mWebView.loadUrl(url);
		}
//		mWebView.setInitialScale(25);//�?25%，最小缩放等�? ，可以在这里根据�?求来设置缩放比例.  
		mWebView.setWebChromeClient(new MyWebChromeClient());
		mWebView.setWebViewClient(new MyWebViewClient());
	}
	

	@Override
	public void onClick(View v) {
		if (v == back) {
			this.finish();
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 重写方法后，我们就调用父类的方法，这样以便系统的方法可以调用,这句�?肯不能忘�?
		// 现在返回:true,代表让系统能继续处理此按键的操作
		// 返回false:代表该按键的处理到此结束，不响应系统的处�?
		super.onKeyDown(keyCode, event);
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mWebView.canGoBack()) {
				mWebView.goBack();
				tltleList.remove(tltleList.size() - 1);
			} else {
				return true;
			}
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	boolean isExit;
	private void exit() {
        if (!isExit) {
            isExit = true;
            Toast.makeText(getApplicationContext(), "再按一次退出程序",
                    Toast.LENGTH_SHORT).show();
            // 利用handler延迟发送更改状态信息
            mHandler.sendEmptyMessageDelayed(0, 2000);
        } else {
            finish();
            System.exit(0);
        }
    }
	
	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			isExit = false;
		}
	};

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
				progressbar.setVisibility(View.GONE);
			} else {
				if (View.GONE == progressbar.getVisibility()) {
					progressbar.setVisibility(View.VISIBLE);
				}
				progressbar.setProgress(newProgress);
			}
		}
		
		
		public void onShowCustomView(View view, int requestedOrientation,
				CustomViewCallback callback) {
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
		
		@Override
		public void onReceivedTitle(WebView view, String title) {
			super.onReceivedTitle(view, title);
			tltleList.add(title);		
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
                                              