package com.set.network.request_interface;

import retrofit2.Callback;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

/**
 * Created by Hugh on 2016/9/17.
 */
public interface GetMobileService {
    // path
    // @GET("/users/{user}/repos")
    // List<Repo> listRepos(@Path("user") String user);

    // Query
    //设置头
    @Headers({
            "Accept: application/vnd.github.v3.full+json",
            "User-Agent: Retrofit-Sample-App"
    })
    @GET("/api/patient/GetMobile")
    String getMobile(@Query("id") String id);

    // Query map
    //    @GET("/group/{id}/users")
    //    List<User> groupList(@Path("id") int groupId, @QueryMap Map<String, String> options);

    @GET("/api/patient/GetMobile")
    String getMobile(@Query("id") String id, Callback<String> cb);
}
