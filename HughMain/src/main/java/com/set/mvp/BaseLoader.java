package com.set.mvp;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import set.work.bean.RequestListBean;
import set.work.bean.ResultBean;

/**
 * 异步加载基础类
 * Created by wlm on 17-3-3.
 */

public abstract class BaseLoader<T> extends AsyncTaskLoader<ResultBean<T>> {

    protected final String TAG = this.getClass().getSimpleName();
    protected Context context;
    protected RequestListBean requestBean;
    public BaseLoader(Context context) {
        super(context);
        this.context = context;
    }

    public BaseLoader(Context context, RequestListBean requestBean) {
        super(context);
        this.context = context;
        this.requestBean = requestBean;
    }

    @Override
    public ResultBean<T> loadInBackground() {
        return this._doInBack();
    }

    /**
     * 后台操作
     *
     * @return
     */
    protected abstract ResultBean<T> _doInBack();

    @Override
    protected void onStartLoading() {
        super.onStartLoading();

    }

    @Override
    public void deliverResult(ResultBean<T> data) {
        super.deliverResult(data);
    }
}