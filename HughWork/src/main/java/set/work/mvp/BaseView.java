package set.work.mvp;

/**
 * 基础视图
 * Created by wlm on 17-2-28.
 */

public interface BaseView<T> {
    /**
     * 设置控制器
     *
     * @param presenter
     */
    void setPresenter(T presenter);
}
