package com.set.remote;

import set.work.bean.RequestListBean;
import set.work.bean.ResultBean;
import set.work.handler.ListenHandler;
import set.work.thread.SoapRunnable;

/**
 * Created by Hugh on 2017/7/6.
 */

public class SoapRemote<T> extends BaseRemote<T> {
    private String name_space;
    protected String methodName;
    protected String urlString;
    public SoapRemote(ListenHandler handler, RequestListBean requestBean ,String urlString, String namespace, String methodName) {
        super(handler, requestBean);
        name_space = namespace;
        this.methodName = methodName;
        this.urlString = urlString;
    }

    @Override
    public ResultBean<T> getRemoteResult() {
        SoapRunnable soapRunnable = new SoapRunnable(name_space, methodName, handler, requestBean, urlString);
        ResultBean<T> result = soapRunnable.getRunnableResult();
//        result.parseResult();
        return result;
    }

}
