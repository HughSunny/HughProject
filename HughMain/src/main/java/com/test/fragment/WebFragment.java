package com.test.fragment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.set.R;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

public class WebFragment extends Fragment {
	private static final String TAG = "WebFragment";
	private WebView mWebView;
	private FrameLayout mFullscreenContainer;
	private FrameLayout mContentView;
	private View mCustomView = null;
	private String url;
	private boolean mIsWebViewAvailable = false;
	private int menuIndex;
	Map<Integer, String> urlMap = new HashMap<Integer, String>();
//	private TextView web_notice;
	private boolean flag_load_finish = false;
	String customHtml = "<html>"
			+ "<body><font color='red'>hello baidu!</font>"
			+ "</body>"
			+ "<a target='_blank' href=\"getImages?type=lis&ec=id&id=PBCbNLsoiDLFPjNcoKFS2t7uslWjLRJkmAcTzLwjEAA%3D\"><strong><FONT color=\"red\">查看影像</FONT></strong></a>"
			+ "</html>";
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Bundle bundle = getArguments();
		View view = inflater.inflate(R.layout.activity_web, null);
		mFullscreenContainer = (FrameLayout) view.findViewById(R.id.fullscreen_custom_content);
		mContentView = (FrameLayout) view.findViewById(R.id.web_content);
		mWebView = (WebView) view.findViewById(R.id.webview_player);
//		web_notice = (TextView)view.findViewById(R.id.web_notice);
		WebSettings settings = mWebView.getSettings();
		settings.setJavaScriptEnabled(true);
		settings.setJavaScriptCanOpenWindowsAutomatically(true);
		settings.setPluginState(PluginState.ON);
		settings.setAllowFileAccess(true);
		settings.setLoadWithOverviewMode(true);
		settings.setSupportZoom(true);
		settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);// WebSettings.LOAD_NO_CACHE
		// mWebView.loadUrl(url);
		// mWebView.setInitialScale(25);//�?25%，最小缩放等�? ，可以在这里根据�?求来设置缩放比例.
		mWebView.setWebChromeClient(new MyWebChromeClient());
		mWebView.setWebViewClient(new MyWebViewClient());
		mWebView.loadDataWithBaseURL(null, getHtmlString(), "text/html", "utf-8", null);
		return view;
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
	
//	@Override
//	public void setUserVisibleHint(boolean isVisibleToUser) {
//		super.setUserVisibleHint(isVisibleToUser);
//		Log.e(TAG, "setUserVisibleHint = " + isVisibleToUser); 
//		if (menu != null) {
//			Log.e(TAG, "dataType = " + menu.dataType); 
//		}
//		if (isVisibleToUser) {
//			if (menu != null) {
//				getUrl();
//			}
//		}
//	}

	@Override
	public void onResume() {
		super.onResume();
	}

	
	private void stopLoad(){
		if (!flag_load_finish && mWebView != null) {
			mWebView.pauseTimers();
			mWebView.stopLoading();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		mWebView.pauseTimers();
	}

	@Override
	public void onDestroyView() {
		mIsWebViewAvailable = false;
//		mContentView.removeAllViews();
//        mWebView.stopLoading();
//        mWebView.removeAllViews();
//        mWebView.destroy();
//        mWebView.setVisibility(View.GONE);
//        mContentView = null;
//        mWebView = null;
		super.onDestroyView();
	}

	@Override
	public void onDestroy() {
		if (mWebView != null) {
//			callHiddenWebViewMethod("onDestroy");
			mWebView.clearCache(true);   
			mWebView.clearHistory();  
			mWebView = null;
		}
		super.onDestroy();
	}

	public WebView getWebView() {
		return mIsWebViewAvailable ? mWebView : null;
	}
	boolean pageError;

	class MyWebChromeClient extends WebChromeClient {

		private CustomViewCallback mCustomViewCallback;
		private int mOriginalOrientation = 1;

		@Override
		public void onShowCustomView(View view, CustomViewCallback callback) {
			onShowCustomView(view, mOriginalOrientation, callback);
			super.onShowCustomView(view, callback);

		}

		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			super.onProgressChanged(view, newProgress);
			Log.i(TAG, "newProgress = " + newProgress);
			if (newProgress == 100) {
			}
		}

		public void onShowCustomView(View view, int requestedOrientation, WebChromeClient.CustomViewCallback callback) {
			if (mCustomView != null) {
				callback.onCustomViewHidden();
				return;
			}
//			if (EquipmentInfo.getPhoneAndroidSDK() >= 14) {
				mFullscreenContainer.addView(view);
				mCustomView = view;
				mCustomViewCallback = callback;
				mOriginalOrientation = getActivity().getRequestedOrientation();
				mContentView.setVisibility(View.INVISIBLE);
				mFullscreenContainer.setVisibility(View.VISIBLE);
				mFullscreenContainer.bringToFront();
				getActivity().setRequestedOrientation(mOriginalOrientation);
//			}
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
			getActivity().setRequestedOrientation(mOriginalOrientation);
		}

		@Override
		public void onReceivedTitle(WebView view, String title) {
			super.onReceivedTitle(view, title);
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
			flag_load_finish = true;
		}

		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			Log.i(TAG, "onReceivedError");
			pageError = true;
			super.onReceivedError(view, errorCode, description, failingUrl);
		}
		
		@Override
		public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
			Log.i(TAG, "shouldInterceptRequest = ");
			return super.shouldInterceptRequest(view, request);
		}
		
		
		@Override
		public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
			Log.i(TAG, "shouldInterceptRequest = ");
			return super.shouldInterceptRequest(view, url);
		}
	}
		

}
