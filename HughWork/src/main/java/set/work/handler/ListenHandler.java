package set.work.handler;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import set.work.RunnableManager;
import set.work.bean.ResultBean;
import set.work.db.SetDBOperator;
import set.work.thread.RequestBack;
import set.work.utils.LogUtil;
import set.work.utils.FinalStrings;

/**
 * 连接工作线程和UI线程的Handler
 * 实则是代理
 */
public class ListenHandler extends Handler {
    protected static final int MSG_RETURN = 10000;
    protected Context context;
    protected RequestBack callback;
    protected static final String KEY = "data";
    private static final String TAG = "ListenHandler";

    public ListenHandler(Looper looper, Context context, RequestBack back) {
        super(looper);
        this.context = context;
        this.callback = back;
    }

    public ListenHandler(Context context, RequestBack back) {
        super();
        this.context = context;
        this.callback = back;
    }

    public void sendResultMessage(ResultBean resultBean) {
        try {
            Bundle b = new Bundle();
            b.putSerializable(KEY, resultBean);
            Message message = this.obtainMessage();
            message.what = MSG_RETURN;
            message.setData(b);
            this.sendMessage(message);
        } catch (Exception ex) {
            LogUtil.Error(this, ex.toString());
        }
    }
    @Override
    public void handleMessage(Message m) {
        super.handleMessage(m);
        switch (m.what) {
            case MSG_RETURN:
                ResultBean resultBean = (ResultBean) m.getData().getSerializable(KEY);
                CallBack(resultBean);
                break;
        }
    };

    private void CallBack(ResultBean result) {
        if (result == null) {
            Log.e(TAG, "THE RUNNABLE result IS NULL");
            return;
        }
        if (result.getRunnable() != null) {
            boolean success = RunnableManager.getInstance().runnableDone(callback, result.getRunnable());
            if (!success) {// 请求已经被提前结束了
                Log.w(TAG, "THE RUNNABLE HAS COMPLETED");
                return;
            }
        }
        try {
            String returnStr = result.getResultString();
            if (returnStr == null || returnStr.equals(FinalStrings.WEB_WRARING)) {
                SetDBOperator.getInstance(context).insertDataSubmit(
                        result.getRequestType() + "",
                        result.getRequestBean());
                //TODO  超时的网络异常是 getResultString == null
                result.setResultString(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // 捕捉解析的异
            String exceptionStr = TAG + " : " + e.getMessage() + "---";
            if (result != null) {
                exceptionStr = exceptionStr + "\r\n" + result.getRequestType() + "---" + result.getResultString();
            }
            LogUtil.Error(this,exceptionStr);
        } finally {
            if (callback != null) {
                if (callback.onPreRequestBack(result)) {
                    callback.onRequestBack(result);
                }
            }
            if(result != null){
                result.clear();
                result = null;
            }
        }
    }

    public void quit() {
        removeCallbacksAndMessages(null);
    }

    public RequestBack getCallback() {
        return callback;
    }

    public void setCallback(RequestBack callback) {
        this.callback = callback;
    }
}
