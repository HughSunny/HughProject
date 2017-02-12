package com.set.network;

import com.hugh.work.utils.EquipmentInfo;
import com.hugh.work.utils.NetworkUtil;
import com.set.app.MainApp;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Hugh on 2016/9/17.
 */
public class MyRetrofitManager {
    private static OkHttpClient mOkHttpClient;
    //短缓存有效期为1秒钟
    public static final int    CACHE_STALE_SHORT = 1;
    //长缓存有效期为7天
    public static final int    CACHE_STALE_LONG  = 60 * 60 * 24 * 7;
    private String BASE_NIUPAI_URL = "";

    private MyRetrofitManager() {
        initOkHttpClient();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_NIUPAI_URL)
                .client(mOkHttpClient)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
//                .addConverterFactory(new MyCustomFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        //添加接口回调
        //mJtmlService = retrofit.create(JtmlServer.class);
    }

    private void initOkHttpClient() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        if (mOkHttpClient == null) {
            synchronized (MyRetrofitManager.class) {
                if (mOkHttpClient == null) {
                    // 指定缓存路径,缓存大小100Mb
                    Cache cache = new Cache(new File(EquipmentInfo.getSDCardSavePath(), "HttpCache"),
                            1024 * 1024 * 100);

                    mOkHttpClient = new OkHttpClient.Builder()
                            .cache(cache)
                            .addInterceptor(mRewriteCacheControlInterceptor)
                            .addNetworkInterceptor(mRewriteCacheControlInterceptor)
                            .addInterceptor(interceptor)
                            .retryOnConnectionFailure(true)
                            .connectTimeout(15, TimeUnit.SECONDS)
                            .build();
                }
            }
        }
    }
    private Interceptor mRewriteCacheControlInterceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            if (!NetworkUtil.isNetworkAvailable(MainApp.getInstance())) {
                request = request.newBuilder().cacheControl(CacheControl.FORCE_CACHE).build();
            }
            Response originalResponse = chain.proceed(request);
            if (!NetworkUtil.isNetworkAvailable(MainApp.getInstance())) {
                //有网的时候读接口上的@Headers里的配置，你可以在这里进行统一的设置
                String cacheControl = request.cacheControl().toString();
                return originalResponse.newBuilder().header("Cache-Control", cacheControl).removeHeader("Pragma").build();
            } else {
                return originalResponse.newBuilder().header("Cache-Control", "public, only-if-cached, max-stale=" + CACHE_STALE_LONG).removeHeader("Pragma").build();
            }
        }
    };

//    RequestInterceptor requestInterceptor = new RequestInterceptor() {
//        @Override
//        public void intercept(RequestFacade request) {
//            request.addHeader("User-Agent", "Retrofit-Sample-App");
//        }
//    };

}
