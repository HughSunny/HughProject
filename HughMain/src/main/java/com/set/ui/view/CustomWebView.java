package com.set.ui.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kobjects.base64.Base64;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

/**
 * Created by Hugh on 2017/2/14.
 * 自定义的webview
 */
public class CustomWebView extends WebView {
	private static final String TAG = "CustomWebView";
	private View mCustomView = null;
	private boolean pageError;
	private Context context;
	private List<String> titleList = new ArrayList<String>();
	Map<String, String> extraHeaders = new HashMap<String, String>();
	private boolean canback = true;
	public boolean loadSuccess;
	public int progress;

	private static String HTM_NO_DATA = "<!DOCTYPE html>\n" +
			"<html lang=\"zh-cn\">\n" +
			"<head>\n" +
			"<meta charset=\"utf-8\"/>\n" +
			"<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\" />\n" +
			"<title>无数据</title>\n" +
			"</head>\n" +
			"<body>\n" +
			"\t<h2 align=\"center\">无数据</h2>\n" +
			"</body>\n" +
			"</html>\n";

	public CustomWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		WebSettings settings = getSettings();
		settings.setJavaScriptEnabled(true);
		settings.setJavaScriptCanOpenWindowsAutomatically(true);
		settings.setPluginState(WebSettings.PluginState.ON);
		settings.setAllowFileAccess(true);
		settings.setLoadWithOverviewMode(true);
		settings.setSupportZoom(true);
		settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);// WebSettings.LOAD_NO_CACHE
		setWebChromeClient(new MyWebChromeClient());
		setWebViewClient(new MyWebViewClient());
	}

	/**
	 * 设置是否可以返回操作
	 */
	public void setBackDisable() {
		canback = false;
	}

	/**
	 * 设置webview 请求的消息头
	 *
	 * @param key
	 * @param value
	 */
	public void setWebHeader(String key, String value) {
		if (extraHeaders != null && key != null) {
			extraHeaders.put(key, Base64.encode(value.getBytes()));
		}
	}

	/**
	 * 加载 html 字符串
	 *
	 * @param html
	 */
	public void loadHtml(String html) {
		if (html == null) {
			html = HTM_NO_DATA;
		}
		loadSuccess = false;
		resumeTimers();
		requestFocus();
		getSettings().setDefaultTextEncodingName("utf-8");
		loadDataWithBaseURL(null, html, "text/html", "utf-8", null);
	}

	public void loadWeb(String url) {
		if (url == null) {
			return;
		}
		clearCache(true);
		clearHistory();
		loadSuccess = false;
		loadUrl(url, extraHeaders);
	}

	public void stopLoad() {
		if (!loadSuccess) {
			pauseTimers();
			stopLoading();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (canback && canGoBack()) {
				goBack();// 返回上一页面
				return true;
			} else {
				// System.exit(0);// 退出程序
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	class MyWebChromeClient extends WebChromeClient {

		@Override
		public void onShowCustomView(View view, CustomViewCallback callback) {
			super.onShowCustomView(view, callback);

		}

		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			super.onProgressChanged(view, newProgress);
			Log.i(TAG, "newProgress = " + newProgress);
			if (newProgress == 100) {
				// handler.sendEmptyMessageDelayed(0, 22300);
				progress = newProgress;
			}
		}

		@Override
		public void onReceivedTitle(WebView view, String title) {
			super.onReceivedTitle(view, title);
			titleList.add(title);
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
			loadSuccess = true;
		}

		@Override
		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
			Log.i(TAG, "onReceivedError");
			pageError = true;
			super.onReceivedError(view, errorCode, description, failingUrl);
		}

	}
}
