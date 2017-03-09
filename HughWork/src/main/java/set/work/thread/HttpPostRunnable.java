package set.work.thread;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;


import set.work.bean.RequestListBean;
import set.work.bean.ResultBean;
import set.work.handler.ListenHandler;
import set.work.utils.EncodeUtil;
import set.work.utils.LogUtil;

/**
 * HttpURLConnection POST
 */
public class HttpPostRunnable extends BaseRequestRunnable {
	static byte[] temp = new byte[512];
	protected int requestId;
	protected HashMap<String, String> data;
	protected String url;

	public HttpPostRunnable(String url, RequestListBean requestBean,
			ListenHandler handler) {
		super(handler, requestBean);
		requestId = requestBean.getRequestType();
		this.url = url;
	}

	@Override
	public void processRunnable(ResultBean result) throws Exception {
		HttpURLConnection connection = null;
		OutputStreamWriter out = null;
		InputStream is = null;
		InputStreamReader isr = null;
		BufferedReader buffer = null;
		StringBuffer resultData = new StringBuffer("");
		try {
			URL urls = new URL(url);// 创建连接
			connection = (HttpURLConnection) urls.openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setUseCaches(false);
			connection.setInstanceFollowRedirects(true);
			connection.setRequestMethod("POST"); // 设置请求方式
			connection.setRequestProperty("Accept", "application/json"); // 设置接收数据的格�?
			connection.setRequestProperty("Content-Type", "application/json"); // 设置发�?数据的格�?
			connection.setConnectTimeout(CONNECT_TIMEOUT);
			connection.connect();
			// 读取响应
			int length = (int) connection.getContentLength();// 获取长度
			is = connection.getInputStream();
			isr = new InputStreamReader(is);
			buffer = new BufferedReader(isr);
			String inputLine = null;
			while ((inputLine = buffer.readLine()) != null) {
				resultData.append(inputLine);
				resultData.append("\n");
			}
			String returnString = EncodeUtil.decodeUnicode(resultData
					.toString());
			result.setResultString(returnString);
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

			if (is != null) {
				try {
					is.close();
					is = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (connection != null) {
				connection.disconnect();
				connection = null;
			}
		}
	}

}
