package set.work.bean;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;

import set.work.thread.BaseRequestRunnable;

/**
 * Created by Hugh on 2017/9/4.
 */

public abstract class BaseResultBean<T> implements Serializable {
    public Class<T> classType;
    public static final int STATUS_NULL= 0;
    public static final int STATUS_DONE = 1;
    public static final int STATUS_ERROR= 2;
    public static final int STATUS_DOING = 3;
    public static final int TYPE_STR = 1;
    public static final int TYPE_OBJ = 2;
    public static final int TYPE_ = 2;
    public int type = TYPE_STR;//返回类型

    protected T result;
    protected String resultString;
    protected RequestListBean requestBean;
    protected BaseRequestRunnable runnable;
    /** 通过头判断是否属于成功的请求结果 */
    protected boolean success = true;
    protected int retStatus;

    public BaseResultBean(RequestListBean bean, String resultString, BaseRequestRunnable runnable) {
        this.requestBean = bean;
        this.resultString = resultString;
        this.setRunnable(runnable);
        classType = (Class<T>) getClass();
    }

    public T getResult(){
        return result;
    }

    public abstract void parseResult() ;

    public void getJsonResult() {
        Gson gson = new Gson();
        result = gson.fromJson(resultString, classType);
    }


    public String getResultString() {
        return resultString;
    }
    public void setResultString(String resultString) {
        this.resultString = resultString;
    }

    public boolean isSuccess() {
        return success;
    }
    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getRetStatus() {
        return retStatus;
    }
    public void setRetStatus(int retStatus) {
        this.retStatus = retStatus;
    }


    public BaseRequestRunnable getRunnable() {
        return runnable;
    }
    public void setRunnable(BaseRequestRunnable runnable) {
        this.runnable = runnable;
    }
    public RequestListBean getRequestBean() {
        return requestBean;
    }
    public void setRequestBean(RequestListBean requestBean) {
        this.requestBean = requestBean;
    }
    public Object getRequestId() {
        return requestBean.getRequestId();
    }
    public int getRequestType() {
        return requestBean.getRequestType();
    }
    public void clear()  {
        runnable = null;
        resultString = null;
    }
}
