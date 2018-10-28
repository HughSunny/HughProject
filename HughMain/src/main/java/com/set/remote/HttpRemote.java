package com.set.remote;

import set.work.bean.RequestListBean;
import set.work.bean.ResultBean;
import set.work.handler.ListenHandler;

/**
 * Created by Hugh on 2017/6/22.
 * HTTP 请求
 */
public class HttpRemote extends BaseRemote{
    public HttpRemote(ListenHandler handler, RequestListBean requestBean) {
        super(handler, requestBean);
        this.handler = handler;
        this.requestBean = requestBean;
    }

    @Override
    public ResultBean getRemoteResult() {
        return null;
    }

    /**
     * get请求
     */
    public void getRemote() {

    }

    /**
     * post请求
     */
    public void postRemote() {

    }

}
