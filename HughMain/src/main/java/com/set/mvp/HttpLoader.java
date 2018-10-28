package com.set.mvp;

import android.content.Context;

import set.work.bean.RequestListBean;
import set.work.bean.ResultBean;

/**
 * Created by Hugh on 2017/8/16.
 */

public class HttpLoader<T> extends BaseLoader<T> {

    public HttpLoader(Context context, RequestListBean requestBean) {
        super(context, requestBean);
    }

    @Override
    protected ResultBean<T> _doInBack() {
        return null;
    }
}
