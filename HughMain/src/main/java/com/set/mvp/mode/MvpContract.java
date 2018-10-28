package com.set.mvp.mode;

import java.util.Map;

import set.work.mvp.BaseView;

/**
 * Created by Hugh on 2017/9/4.
 */

public class MvpContract {
    public interface ReportView extends BaseView<MvpPersenter> {
        /**
         * 网页加载内容
         *
         * @param url
         */
        void loadUrl(String url, Map<String, String> headers);
    }

}
