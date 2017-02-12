package com.set.network.request_interface;

import com.set.network.bean.LoginBean;

import retrofit2.Callback;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by Hugh on 2016/9/17.
 */
public interface PostLoginService {
    @POST("/emrbrowser/app/login")
    void login(@Body LoginBean user, Callback<LoginBean> cb);
}
