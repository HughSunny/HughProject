package com.set.util;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import set.work.utils.BitmapUtil;

public class SyncImageLoader {
	private static final String TAG = "SyncImageLoader";
	private HashMap<String, SoftReference<Bitmap>> imageCache = new HashMap<String, SoftReference<Bitmap>>();
	
	protected InputStream getStreamFromNetwork(String imageUri, Object extra) throws IOException {  
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet httpRequest = new HttpGet(imageUri);  
		//httpRequest.addHeader("cookie", NetworkManager.getCookieString());
	    HttpResponse response = httpclient.execute(httpRequest);  
	    int resCode = response.getStatusLine().getStatusCode();
	    Log.i(TAG, "getStreamFromNetwork RESPONSECODE = " + resCode);
	    HttpEntity entity = response.getEntity();  
	    BufferedHttpEntity bufHttpEntity = new BufferedHttpEntity(entity);  
//	    ByteArrayEntity  byteArrayEntity= new ByteArrayEntity(entity)
	    return bufHttpEntity.getContent();  
	}  

	public Bitmap getBitmap(final String imageUrl) {
		// 判断缓存中是否有imageUrl这个缓存存在
		if (imageCache.containsKey(imageUrl)) {
			SoftReference<Bitmap> softReference = imageCache.get(imageUrl);
			if (softReference.get() != null) {
				return softReference.get();
			}
		} 
		return null;
	}

	//下载网络图片
	public Bitmap loadDrawable(final String imageUrl,
			final ImageCallback imageCallback) {
		Bitmap drawable = getBitmap(imageUrl);
		if(drawable != null) {
			Log.i(TAG, "loadDrawable cache drawable is not null ");
			return drawable;
		}
		
		imageCallback.onLoadingStarted(imageUrl);
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if(msg.what == 0) {
					Bundle data = msg.getData();
					imageCallback.onLoadComplete((Bitmap) msg.obj,
							data.getString("iconUrl"));
				} else {
					Bundle data = msg.getData();
					imageCallback.onLoadingFailed(data.getString("iconUrl"));;
				}
				
			}
		};
		// 缓存中不存在的话,启动异步线程下载图片
		new Thread() {
			@Override
			public void run() {
				// 将图片放入缓存
				Bitmap bitmap = null;
				try {
					bitmap= BitmapUtil.getFillCenterBitmap(getStreamFromNetwork(imageUrl,null));
//					drawable = Drawable.createFromStream(getStreamFromNetwork(imageUrl,null),
//							"src");
				} catch (IOException e) {
					e.printStackTrace();
				};
				if (bitmap == null) {
					Message msg = handler.obtainMessage(1);
					Bundle data = new Bundle();
					data.putString("iconUrl", imageUrl);
					msg.setData(data);
					handler.sendMessage(msg);
				} else {
					imageCache.put(imageUrl, new SoftReference<Bitmap>(bitmap));
					Message msg = handler.obtainMessage(0, bitmap);
					Bundle data = new Bundle();
					data.putString("iconUrl", imageUrl);
					msg.setData(data);
					handler.sendMessage(msg);
				}
				
				
			}
		}.start();
		return null;
	}

	// 回调函数,图片加载完毕后调用该函数
	public interface ImageCallback {
		public void onLoadingStarted(String iconUrl);
		public void onLoadComplete(Bitmap imageBitmap, String iconUrl);
		public void onLoadingFailed(String iconUrl);
	}
}
