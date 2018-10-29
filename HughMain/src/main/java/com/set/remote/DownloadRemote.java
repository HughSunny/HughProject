package com.set.remote;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import set.work.bean.DownloadRequestBean;
import set.work.bean.ResultBean;
import set.work.handler.ListenHandler;
import set.work.utils.FinalStrings;
import set.work.utils.android.LogUtil;

/**
 * Created by Hugh on 2017/6/22.
 * 下载
 */

public class DownloadRemote<T> extends BaseRemote{
    private static final String TAG = DownloadRemote.class.getSimpleName();
    protected String mUrl;
    private DownloadRequestBean downRequestBean;
    private byte[] TEMP_BUFFER = new byte[1024 * 20];
    private static int TIMES_RETRY = 3;
    private int tryTimes = 0;

    public DownloadRemote(ListenHandler handler, DownloadRequestBean requestBean, String url) {
        super(handler,requestBean);
        this.handler = handler;
        this.downRequestBean = requestBean;
        mUrl = url;
    }

    @Override
    public ResultBean<T> getRemoteResult() {
        final OkHttpClient client = OkHttpRemoteC.getClient();
        Request request = null;

        ResultBean expResult = new ResultBean(downRequestBean, FinalStrings.WEB_WRARING, null);
        try{
            request = new Request.Builder().url(mUrl).build();
        }catch (Exception e){
            LogUtil.Error(this.getClass().getSimpleName(), e.getMessage());
            return expResult;
        }
        if (request == null) {
            return expResult;
        }
        tryTimes = 0;

        while (tryTimes < TIMES_RETRY) {
            boolean hasExp = false;
            boolean success = false;
            try {
                Response response =  client.newCall(request).execute();
                if (response != null) {
                    InputStream is = null;
                    int len = 0;
                    FileOutputStream fos = null;

                    is = response.body().byteStream();
                    long total = response.body().contentLength();
                    File path = new File(downRequestBean.getFilePath());
                    if (!path.exists()) {
                        path.mkdirs();
                    }
                    //下载的地址
                    File file = new File(path, downRequestBean.getFileName());
                    fos = new FileOutputStream(file, false);
                    long sum = 0;
                    while ((len=is.read(TEMP_BUFFER))!=-1){
                        fos.write(TEMP_BUFFER,0,len);
                        sum+=len;
                        int progress = (int)(sum*1.0f/total*100);
                        //sendmessage
                    }
                    success = true;
                }
            } catch (IOException e) {
                e.printStackTrace();
                hasExp = true;
                Throwable throwable = e.getCause();
                if(tryTimes < TIMES_RETRY &&  (throwable == null || throwable.equals(SocketTimeoutException.class))){
                    //如果超时并未超过指定次数，则重新连接
                    tryTimes++;
                } else {
                    LogUtil.Error("DownloadRemote", e.getMessage());
                    ResultBean result = new ResultBean(downRequestBean, FinalStrings.WEB_WRARING,null);
                    return result;
                }
            } finally {
                if (success) {
                    ResultBean result = new ResultBean(downRequestBean, "1", null);
                    return result;
                }
            }
        }
        return  expResult;
    }

    //执行 有回调的那种
    public void enqueueDownload() {
        final OkHttpClient client = OkHttpRemoteC.getClient();
        Request request = null;
        try{
            request = new Request.Builder().url(mUrl).build();
        }catch (Exception e){
            LogUtil.Error(TAG, e.getMessage());
            return;
        }
        if (request == null) {
            ResultBean result = new ResultBean(downRequestBean, FinalStrings.WEB_WRARING,null);
            sendResult(result);
            return;
        }
        tryTimes = 0;
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                e.printStackTrace();
                Throwable throwable = e.getCause();
                if(tryTimes < TIMES_RETRY &&  (throwable == null || throwable.equals(SocketTimeoutException.class))){
                    //如果超时并未超过指定次数，则重新连接
                    tryTimes++;
                    client.newCall(call.request()).enqueue(this);
                } else {
                    LogUtil.Error(TAG, e.getMessage());
                    ResultBean result = new ResultBean(downRequestBean, FinalStrings.WEB_WRARING,null);
                    sendResult(result);
                }
            }

            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {
                boolean success = false;
                boolean hasExp = false;
                InputStream is = null;
                int len = 0;
                FileOutputStream fos = null;
                try{
                    is = response.body().byteStream();
                    long total = response.body().contentLength();
                    File path = new File(downRequestBean.getFilePath());
                    if (!path.exists()) {
                        path.mkdirs();
                    }
                    //下载的地址
                    File file = new File(path, downRequestBean.getFileName());
                    fos = new FileOutputStream(file, false);
                    long sum = 0;
                    while ((len=is.read(TEMP_BUFFER))!=-1){
                        fos.write(TEMP_BUFFER,0,len);
                        sum+=len;
                        int progress = (int)(sum*1.0f/total*100);
                        //sendmessage
                    }
                    success = true;
                }catch (Exception e){
                    e.printStackTrace();
                    hasExp = true;
                    Throwable throwable = e.getCause();
                    if( tryTimes < TIMES_RETRY &&  (throwable == null || throwable instanceof SocketTimeoutException) ){
                        //如果超时并未超过指定次数，则重新连接
                        tryTimes++;
                        client.newCall(call.request()).enqueue(this);
                    } else {
                        LogUtil.Error(TAG, e.getMessage());
                        ResultBean result = new ResultBean(downRequestBean, FinalStrings.WEB_WRARING,null);
                        sendResult(result);
                    }
                } finally {
                    try {
                        if (is != null)
                            is.close();
                    } catch (IOException e) {
                    }
                    try {
                        if (fos != null)
                            fos.close();
                    } catch (IOException e) {
                    }
                    if (success) {//网络异常在异常捕捉中返回
                        ResultBean result = new ResultBean(downRequestBean, "1", null);
                        sendResult(result);
                    }
                }
            }
        });
    }




}
