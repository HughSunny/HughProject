package set.work.thread;

import set.work.bean.RequestListBean;
import set.work.bean.ResultBean;
import set.work.handler.ListenHandler;
import set.work.utils.FinalStrings;
import set.work.utils.LogUtil;

public abstract class BaseRequestRunnable extends BaseRunnable{
	protected static int CONNECT_TIMEOUT = 30000;
	public ListenHandler handler;
	protected boolean hadSendMessage;
	public RequestListBean requestBean;
	public abstract void processRunnable(ResultBean result) throws Exception;
	protected static int timeout = CONNECT_TIMEOUT;

	public BaseRequestRunnable(ListenHandler handler, RequestListBean requestBean) {
		this.handler = handler;
		this.requestBean = requestBean;
	}

	@Override
	public void run() {
		if (stop) {
			return;
		}
		ResultBean result = new ResultBean(requestBean,null,this);
		try {
			processRunnable(result);
			if (result.getResultString() == null) {
				result.setResultString(FinalStrings.WEB_WRARING);
			}
		}  catch (Exception e) {
			e.printStackTrace();
			LogUtil.Error("BaseRequestRunnable", e.getMessage());
		}
		sendResult(result);
	}

	protected void sendResult(ResultBean result) {
		if (handler != null) {
			handler.sendResultMessage(result);
			hadSendMessage = true;
		}
	}

	public RequestListBean getRequestBean() {
		return requestBean;
	}
	public void setRequestBean(RequestListBean requestBean) {
		this.requestBean = requestBean;
	}

	public static long getTimeout() {
		return timeout;
	}

	public static void setTimeout(int timeout) {
		BaseRequestRunnable.timeout = timeout;
	}


}
