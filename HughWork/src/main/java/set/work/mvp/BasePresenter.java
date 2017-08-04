package set.work.mvp;

import android.view.View;

/**
 * 基础控制器
 * Created by sxx on 17-2-28.
 */

public interface BasePresenter extends View.OnClickListener {
    /**
     * 开始逻辑处理
     */
    void start();

    /**
     * 结束操作
     */
    void stop();
}

