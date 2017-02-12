package com.hugh.work.network;


public abstract class BaseRunnable implements Runnable {
    protected boolean stop = false;
    protected String time;//请求时间
    protected int priority;//优先级
    protected boolean isActive;//是否是被激活
    /** 是否同步 异步可以同时多个请求发送*/
    private boolean isSync;

    public void stopRunnable() {
        stop = true;
    }
    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isSync() {
        return isSync;
    }

    public void setSync(boolean sync) {
        isSync = sync;
    }


}
