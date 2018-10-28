package com.set.remote;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Hugh on 2016/8/21.
 * OkHttpClient 管理
 */
public class OkHttpRemoteC {
    protected final String TAG = this.getClass().getSimpleName();
    public static final String ENCODE = "utf-8";
    public static final String MEDIA_TYPE = "application/json";
    public static final String COOKIE = "cookie";

    public static HashMap<String, String> mCookieContainer = new HashMap<String, String>();
    public static OkHttpClient mOkHttpClient;


    public OkHttpRemoteC() {
        if (mOkHttpClient == null) {
            mOkHttpClient = newClient(new OkHttpClient.Builder().
                    connectTimeout(RemoteUtil.TIMEOUT_CONNECT, TimeUnit.SECONDS).
                    readTimeout(RemoteUtil.TIMEOUT_READ, TimeUnit.SECONDS).
                    writeTimeout(RemoteUtil.TIMEOUT_WRITE, TimeUnit.SECONDS).
                    addInterceptor(new ReceivedCookiesInterceptor()));
        }
    }

    public static OkHttpClient getClient() {
        if (mOkHttpClient == null) {
            mOkHttpClient = newClient(new OkHttpClient.Builder().connectTimeout(RemoteUtil.TIMEOUT_CONNECT, TimeUnit.SECONDS).readTimeout(RemoteUtil.TIMEOUT_READ, TimeUnit.SECONDS).writeTimeout(RemoteUtil.TIMEOUT_WRITE, TimeUnit.SECONDS).addInterceptor(new ReceivedCookiesInterceptor()));
        }
        return mOkHttpClient;
    }

    public static OkHttpClient newClient(OkHttpClient.Builder builder){
        if (builder == null) {
            return null;
        }
        return  builder.build();
    }


    public static String getCookieString() {
        StringBuilder sb = new StringBuilder();
        Iterator iter = mCookieContainer.entrySet().iterator();
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

    /**
     * cookies
     *
     */
    public static class ReceivedCookiesInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Response originalResponse = chain.proceed(chain.request());
            List<String> cookieList = originalResponse.headers("Set-Cookie");
            if (cookieList != null) {
                for (String cookie : cookieList) {// Cookie的格式为:cookieName=cookieValue;path=xxx
                    String[] cookievalues = cookie.split(";");
                    for (int j = 0; j < cookievalues.length; j++) {
                        String[] keyPair = cookievalues[j].split("=");
                        String key = keyPair[0].trim();
                        String value = keyPair.length > 1 ? keyPair[1].trim() : "";
                        mCookieContainer.put(key, value);
                    }
                }
            }
            return originalResponse;
        }
    }



    private void test() {
        OkHttpClient mOkHttpClient = new OkHttpClient();
        //创建一个Request
        final Request request = new Request.Builder()
                .url("https://github.com/hongyangAndroid")
                .build();
        //new call
        Call call = mOkHttpClient.newCall(request);
        //请求加入调度
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //String htmlStr =  response.body().string();
            }
        });
    }


}
