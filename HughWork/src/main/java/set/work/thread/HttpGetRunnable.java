package set.work.thread;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;


import set.work.bean.RequestListBean;
import set.work.bean.ResultBean;
import set.work.handler.ListenHandler;
import set.work.utils.EncodeUtil;
import set.work.utils.LogUtil;

/**
 * HttpURLConnection get
 */
public class HttpGetRunnable extends BaseRequestRunnable {
	private static final String TAG = "HttpGetRunnable";
	protected int responseCode = -1;
	protected String urlString = "";
	protected InputStream input;
	protected String returnString ;

	public HttpGetRunnable(String url, RequestListBean requestBean, ListenHandler handler) {
		super(handler, requestBean);
		urlString = url;
	}
	@Override
	public void processRunnable(ResultBean result) throws Exception {
		HttpURLConnection conn = null;
		URL url = null;
		InputStreamReader isr = null;
		BufferedReader buffer = null;
		StringBuffer resultData = new StringBuffer("");
		try {
			url = new URL(urlString);
			conn = (HttpURLConnection) url.openConnection();
			setHeader(conn);
			conn.connect();
			responseCode = conn.getResponseCode();
			if (responseCode == 200) {
				input = conn.getInputStream();
				isr = new InputStreamReader(input);
				buffer = new BufferedReader(isr);
				String inputLine = null;
				while ((inputLine = buffer.readLine()) != null) {
					resultData.append(inputLine);
					resultData.append("\n");
				}
				returnString = EncodeUtil.decodeUnicode(resultData.toString());
				result.setResultString(returnString);
			}
		} catch (IOException e) {
			e.printStackTrace();
			LogUtil.Debug(this, e.toString());
		} finally {
			try {
				if (buffer != null) {
					buffer.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				if (isr != null)
					isr.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (input != null) {
				try {
					input.close();
					input = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (conn != null) {
				conn.disconnect();
				conn = null;
			}
		}
	}
	

	private void setHeader(URLConnection conn) {
		// 设置连接超时时间30000ms
		conn.setConnectTimeout(CONNECT_TIMEOUT);
		conn.setReadTimeout(CONNECT_TIMEOUT);
		conn.setRequestProperty(
				"User-Agent",
				"Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.3) Gecko/2008092510 Ubuntu/8.04 (hardy) Firefox/3.0.3");
		conn.setRequestProperty("Accept-Language", "en-us,en;q=0.7,zh-cn;q=0.3");
		conn.setRequestProperty("Accept-Encoding", "aa");
		conn.setRequestProperty("Accept-Charset",
				"ISO-8859-1,utf-8;q=0.7,*;q=0.7");
		conn.setRequestProperty("Cache-Control", "max-age=0");
		conn.setRequestProperty("Connection", "Close");
	}

}
