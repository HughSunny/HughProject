package com.set.mvp;

import android.content.Context;

import com.set.remote.DownloadRemote;

import set.work.bean.DownloadRequestBean;
import set.work.bean.ResultBean;

/**
 * Created by Hugh on 2017/6/22.
 */

public class DownloadLoader<T> extends BaseLoader{
    private String mUrl;
    public DownloadLoader(Context context, DownloadRequestBean requestBean, String url) {
        super(context, requestBean);
        mUrl = url;
    }

    @Override
    protected ResultBean<T> _doInBack() {
        DownloadRemote<T> remote = new DownloadRemote(null, (DownloadRequestBean)requestBean, mUrl);
        return remote.getRemoteResult();
    }
}
