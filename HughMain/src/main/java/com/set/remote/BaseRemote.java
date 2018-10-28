package com.set.remote;

import set.work.bean.RequestListBean;
import set.work.bean.ResultBean;
import set.work.handler.ListenHandler;

/**
 * Created by Hugh on 2017/6/22.
 *
 * 基本的处理方法
 */

public abstract class BaseRemote<T> {
    public ListenHandler handler;
    public RequestListBean requestBean;

    public BaseRemote(ListenHandler handler, RequestListBean requestBean) {
        this.handler = handler;
        this.requestBean = requestBean;
    }

    protected void sendResult(ResultBean<T> result) {
        if (handler != null) {
            handler.sendResultMessage(result);
        }
    }

    public abstract ResultBean<T> getRemoteResult();

}
