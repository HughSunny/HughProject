package set.work.thread;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import set.work.bean.DownloadRequestBean;
import set.work.bean.ResultBean;
import set.work.handler.ListenHandler;
import set.work.utils.EquipmentInfo;
import set.work.utils.FileUtil;

/**
 * 下载
 */
public class DownloadRunnable extends BaseRequestRunnable {

	private static final String TAG = "Downloader";
	private String reqUrl;// 下载路径
	private String savePath;// 保存路径
	private String fileName;// 文件名字
	private boolean needVerify; // 需要验证 
	private String lastModify;
	private long fileSize = -1;
	private long lastModifyTime;
	private long startPosition = 0;
	private int reconnectTimes = 3;
	private static byte[] BUF = new byte[1024 * 128];
	private static String TEMP = ".temp";
	private File file;
	private static int sleepDelta = 100;
	public static long EXTRA_SD_SPACE = 100*1024*1024;
	public Handler updateHandler;
	public DownloadRunnable(String url, ListenHandler handler, DownloadRequestBean requestBean) {
		super(handler, requestBean);
		reqUrl = url;
		savePath = requestBean.getFilePath();
		fileName = requestBean.getFileName();
		lastModify = requestBean.getLastModify();
		needVerify = requestBean.isNeedVerify();
		updateHandler = requestBean.getUpdateHandler();
	}

	public void setTmpFilePort(boolean override) {
		File fileDir = new File(savePath);
		if (!fileDir.exists()) {
			fileDir.mkdirs();
		}
		file = new File(savePath, fileName + TEMP);
		if (!file.exists()) {
				try {
					file.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
		} else {
			if (override) {
				file.delete();
				startPosition = 0;
			} else {
				startPosition = FileUtil.getFileSize(savePath, fileName + TEMP);
				return;
			}
		}
		
	}
	
	boolean retry = true;
	boolean abandoned = false;
	private boolean hasException = false;
	private int response = 0;
	@Override
	public void processRunnable(ResultBean bean) throws Exception {
		InputStream inStream = null;
		HttpURLConnection conn = null;
		URL url = null;
		RandomAccessFile randomAccessFile = null;
		while (retry) {
			try {
				if (abandoned) {
					bean.setSuccess(false);
					return;
				}
				url = new URL(reqUrl);
				conn = (HttpURLConnection) url.openConnection();
				if (lastModify != null) {
					conn.setRequestProperty("If-Modified-Since", lastModify);
				}
				setHeader(conn);
				conn.connect();
				response = conn.getResponseCode();
				if (response == HttpURLConnection.HTTP_OK
						|| response == HttpURLConnection.HTTP_PARTIAL) {
					lastModifyTime = conn.getLastModified();
					Log.i("DOWNLOADER", fileName + " 's  lastModifyTime is"
							+ lastModifyTime);
					int connectLength = conn.getContentLength();
					if (EquipmentInfo.getAvailableStorage() < connectLength
							+ EXTRA_SD_SPACE) {// 小文件申请磁盘空间
						Log.e(TAG,
								"THE DISK IS NOT HAVE ENOUGH SPACE TO DOWNLOAD SMALL FILE, THE FILE NAME IS "
										+ fileName);
						if (file != null && startPosition == 0 && file.exists()) {// tmp文件删除
							file.delete();
						}
						retry = false;
						reconnectTimes = 0;
						return ;
					}

					fileSize = connectLength + startPosition;// 得到文件的大小
					if (fileSize <= 0) {
						reconnectTimes--;
						if (reconnectTimes <= 0) {
							startPosition = fileSize;
							retry = false;
							bean = null;
							return ;
						} else {
							continue;
						}
					}
					inStream = conn.getInputStream();
					randomAccessFile = new RandomAccessFile(file, "rwd");
					randomAccessFile.seek(startPosition);// 这里设置线程从哪个地方开始写入数据,这里是与网上获取数据是一样的
					int length = 0;
					while (true) {
						if (abandoned) {
							retry = false;
							return ;
						}
						length = inStream.read(BUF, 0, BUF.length);
						Log.i(TAG, "DOWNLOAD READ BUFFER " + length);
						if (length == -1 || length == 0) {
							break;
						}
						randomAccessFile.write(BUF, 0, length);
						startPosition += length;// 累加已经下载的长度
						if (updateHandler != null) {
							// 用消息将对进度条进行更新
							Message msg = Message.obtain();
							msg.what = 0;//DownloadInfo.UPDATE_ID;
							msg.arg1 = (int) startPosition;
							msg.arg2 = (int) connectLength;
							updateHandler.sendMessage(msg);
						}
						try {
							Thread.sleep(sleepDelta);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				} else if (response == HttpURLConnection.HTTP_NOT_MODIFIED) {
					// 检测已经是最新
					if (file != null) {// tmp文件删除
						file.delete();
					}
					fileSize = 0;
				} else if (response == HttpURLConnection.HTTP_NOT_FOUND) {
					// 没有发现文件或者是没有发现终端
					if (file != null) {// tmp文件删除
						file.delete();
					}
					startPosition = -1;
				} else if (response == 416) { // 416 Requested Range Not
												// Satisfiable
												// 服务器不能满足客户在请求中指定的Range
					fileSize = startPosition;
				} else {// 400。600
					if (file != null && startPosition <= 0) {// tmp文件删除
						file.delete();
					}
				}
				retry = false;
				reconnectTimes = 0;
				hasException = false;
			} catch (SocketTimeoutException e) {// 超时包括connect 和 read
				Log.w(TAG, "DOWNLOAD EXCEPTION");
				hasException = true;
				e.printStackTrace();
			} catch (IOException e) {// 其他异常
				Log.w(TAG, "DOWNLOAD EXCEPTION");
				hasException = true;
				e.printStackTrace();
			} finally {// 最后关闭connect 和 文件
				if (abandoned) {// 线程终止
					if (file.exists() && startPosition <= 0) {// tmp文件删除
						file.delete();
					}
					retry = false;
				} else {
					reconnectTimes--;
					if (reconnectTimes <= 0) {
						retry = false;
						if (file != null && startPosition <= 0) {// 如果temp文件没有内容
																	// tmp文件删除
							file.delete();
						}
					} else {
						Log.w(TAG, "CONTIUNE! reConnectTime =="
								+ reconnectTimes);
						continue;
					}
				}
				if (conn != null) {
					conn.disconnect();
					conn = null;
				}
				try {
					if (randomAccessFile != null) {
						randomAccessFile.close();
					}
					if (inStream != null) {
						inStream.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static int CONNECT_TIME_OUT = 20000;
	public static int READ_TIME_OUT = 2 * 60 * 1000;
	private void setHeader(HttpURLConnection conn) throws IOException {
		conn.setConnectTimeout(CONNECT_TIME_OUT);// 一定要设置连接超时。这里定为10秒
		conn.setReadTimeout(READ_TIME_OUT);
		conn.setRequestMethod("GET");// 采用GET方式提交
		conn.setRequestProperty("Referer", reqUrl);
		conn.setRequestProperty("Range", "bytes=" + startPosition + "-");// 设置获取实体数据的范围
		conn.setRequestProperty(
				"Accept",
				"image/gif, image/jpeg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*");
		conn.setRequestProperty("Accept-Language", "zh-CN");
		conn.setRequestProperty("Connection", "Close");
	}
}
