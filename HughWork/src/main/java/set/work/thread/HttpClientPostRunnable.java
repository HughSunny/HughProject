package set.work.thread;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;


import android.util.Log;

import set.work.bean.RequestListBean;
import set.work.bean.ResultBean;
import set.work.handler.ListenHandler;

/**
 * HTTPCLIENT 的传输模式
 * @author Hugh
 */
public class HttpClientPostRunnable extends BaseRequestRunnable {
	protected String url;
	private static final String TAG = "HttpClientPostRunnable";
	private static String HEADER_CONTENT_TYPE = "Content-Type";
	private static final String DEFAULT_PARAMS_ENCODING = "utf-8";
	private boolean needCookie = true; 

	public HttpClientPostRunnable(ListenHandler handler, RequestListBean requestBean, String url) {
		super(handler, requestBean);
		this.url = url;
	}
	
	@Override
	public  void processRunnable(ResultBean result) throws Exception {
		HttpEntity entity = null;
		if (requestBean != null) {
			switch (requestBean.type) {
			case RequestListBean.TYPE_FILE:
//				File file = new File(pathToOurFile);
//				entity = new MultipartEntity(); // 文件传输
//				ContentBody cbFile = new FileBody(file);
//				entity.addPart("userfile", cbFile);
				break;
			case RequestListBean.TYPE_STRING:
				entity = new StringEntity(requestBean.getRequestValue(), "utf-8");
				break;
			case RequestListBean.TYPE_URL:
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				Iterator iter = requestBean.getSingleMap().entrySet().iterator();
				while (iter.hasNext()) {
					Map.Entry entry = (Map.Entry) iter.next();
					String key = entry.getKey().toString();
					String val = entry.getValue().toString();
					params.add(new BasicNameValuePair(key, val));
				}
				break;
			default:
				break;
			}
		}
		
//		if (entity == null) {
//			return new ResultBean();
//		}
		HttpPost postMethod = null;
		HttpClient httpclient = null;
		try {
			postMethod = new HttpPost(url);
			if (needCookie) {
				setCookies(postMethod);
			}
			postMethod.setEntity(entity);
			httpclient = new DefaultHttpClient();
			postMethod.addHeader(HEADER_CONTENT_TYPE, getBodyContentType());
			HttpResponse response = httpclient.execute(postMethod); // 执行POST方法
			HttpParams httpparams = httpclient.getParams();
			HttpConnectionParams.setConnectionTimeout(httpparams, CONNECT_TIMEOUT);
			int resCode = response.getStatusLine().getStatusCode();
			String resultString = EntityUtils.toString(response.getEntity(), "utf-8");
			getCookies(response);
			Log.i(TAG, "resCode = " + resCode); // 获取响应码
			Log.i(TAG, "result = " + result); // 获取响应内容
			result.setResultString(resultString);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (httpclient != null) {
				httpclient.getConnectionManager().shutdown();
			}
			if (postMethod != null) {
				postMethod.abort();
			}
		}
	}

	public String getBodyContentType() {
		return "application/json" ;//+ DEFAULT_PARAMS_ENCODING;//charset=
	}
	
	private static HashMap<String,String> cookieContainer = new HashMap<String,String>() ;
	public static void getCookies(HttpResponse response) {
		Header[] cookies = response.getAllHeaders();
		Header[] headers = response.getHeaders("Set-Cookie");
		String headerstr = headers.toString();
		if (headers == null)
			return;
		for (int i = 0; i < headers.length; i++) {
			String cookie = headers[i].getValue();
			String[] cookievalues = cookie.split(";");
			for (int j = 0; j < cookievalues.length; j++) {
				String[] keyPair = cookievalues[j].split("=");
				String key = keyPair[0].trim();
				String value = keyPair.length > 1 ? keyPair[1].trim() : "";
				cookieContainer.put(key, value);
			}
		}
	}
	
	public static void setCookies(HttpRequestBase request) {
		request.addHeader("cookie", getCookieString());
	}
	
	public static String getCookieString(){
		StringBuilder sb = new StringBuilder();
		Iterator iter = cookieContainer.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			String key = entry.getKey().toString();
			String val = entry.getValue().toString();
			sb.append(key);
			sb.append("=");
			sb.append(val);
			sb.append(";");
		}
		return sb.toString();
	}

	public boolean isNeedCookie() {
		return needCookie;
	}

	public void setNeedCookie(boolean needCookie) {
		this.needCookie = needCookie;
	}
	
	public static boolean isCookieEmpty() {
		if (cookieContainer == null){
			return true;
		}
		return cookieContainer.isEmpty();
	}
	
	public static void clearCookie() {
		if (cookieContainer == null){
			cookieContainer = new HashMap<String, String>();
			return ;
		}
		cookieContainer.clear();
	}

}
