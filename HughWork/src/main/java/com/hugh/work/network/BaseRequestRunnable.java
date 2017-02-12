package com.hugh.work.network;

import com.hugh.work.bean.RequestListBean;
import com.hugh.work.bean.ResultBean;
import com.hugh.work.handler.ListenHandler;
import com.hugh.work.utils.LogUtil;
import com.hugh.work.utils.FinalStrings;

public abstract class BaseRequestRunnable extends BaseRunnable{
	protected static int CONNECT_TIMEOUT = 15000;
	public ListenHandler handler;
	protected boolean hadSendMessage;
	public RequestListBean requestBean;
	public abstract void processRunnable(ResultBean result) throws Exception;

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
}
