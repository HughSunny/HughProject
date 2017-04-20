package set.work.bean;

import set.work.thread.BaseRunnable;
import set.work.thread.RequestBack;

import java.util.Vector;

/**
 * 存放线程的模型
 */
public class RunnableBean {
    private RequestBack back;//用回调作为识别器
    private Vector<BaseRunnable> list = new Vector<BaseRunnable>();

    public RunnableBean(RequestBack back, BaseRunnable runnable) {
        this.back = back;
        list.add(runnable);
    }

    public void addRunnable(BaseRunnable runnable) {
        list.add(runnable);
    }

    public boolean removeRunnable(BaseRunnable runnable) {
        return list.remove(runnable);
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    /**
     * 获取下一个将被执行的Runnable
     *
     * @return
     */
    public synchronized BaseRunnable getNextRunable() {
        BaseRunnable item = null;
        for (int i = 0; i < list.size(); i++) {
            if (!list.get(i).isActive()) {
                item = list.get(i);
                break;
            }
        }
        return item;
    }

    public void stopAll() {
        for (int i = 0; i < list.size(); i++) {
            list.get(i).stopRunnable();
        }
        list.clear();
    }

    public RequestBack getBack() {
        return back;
    }

    public void setBack(RequestBack back) {
        this.back = back;
    }

    public void setList(Vector<BaseRunnable> list) {
        this.list = list;
    }


}
