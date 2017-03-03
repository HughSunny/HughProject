package set.work.handler;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import set.work.listener.ProgressLis;
import set.work.thread.RequestBack;

public class ProgressListenHandler extends ListenHandler {
	private static final int MSG_CLOSE_WRANING = 10001;
	private static final int MSG_SHOW_PROGRESS = 10002;
	private static final int MSG_DISMISS_PROGRESS = 10003;
	private static final String TAG = "ProgressListenHandler";
	private ProgressLis pListener;
	public ProgressListenHandler(Context context, RequestBack back, ProgressLis listener) {
		super(context,back);
		this.context = context;
		this.callback = back;
		pListener = listener;
	}

	public ProgressListenHandler(Context context, RequestBack back) {
		super(context,back);
		this.context = context;
		this.callback = back;
	}

	@Override
	public void handleMessage(Message m) {
		switch (m.what) {
			case MSG_RETURN:
				super.handleMessage(m);
//				if (RunnableManager.getInstance().isEmpty()) {
//					dismissProgress();
//				}
				break;
			case MSG_CLOSE_WRANING:
				break;
			case MSG_SHOW_PROGRESS:
				showProgress(true);
				break;
			case MSG_DISMISS_PROGRESS:
				dismissProgress();
				break;
		}
	}

	protected AlertDialog dialog;
	public void showProgress() {
		if (Thread.currentThread() != Looper.getMainLooper().getThread()){
			this.sendEmptyMessage(MSG_SHOW_PROGRESS);
		} else {
			showProgress(true);
		}
	}

	public void showProgress(boolean cancelable) {
		if (pListener != null  && pListener.onShowProgress() != null) {
			dialog = pListener.onShowProgress();
			dialog.setCancelable(cancelable);
			dialog.setCanceledOnTouchOutside(cancelable);
			if(!dialog.isShowing()){
				dialog.show();
			}
			return;
		}
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
		}
		dialog = new ProgressDialog(context);
		dialog.setCancelable(cancelable);
		dialog.setCanceledOnTouchOutside(cancelable);
		dialog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface arg0) {
				// TODO 要取消回调，不过只有在等待的情况才能让用户等待，故这边可以不采取行动
				Log.i(TAG, "User cancel the progress");
			}
		});
		dialog.show();
	}

	protected AlertDialog errorDialog;
	public void showWarningProgress(String error) {
		if (pListener != null) {
			errorDialog = pListener.onShowWarningPgs(error);
			if (errorDialog != null && !errorDialog.isShowing()) {
				errorDialog.show();
				return;
			}
		}
		if (errorDialog != null && errorDialog.isShowing()) {
			errorDialog.dismiss();
		}

		Builder builder = new Builder(context);
		builder.setMessage(error);
		builder.setTitle("提示");
		builder.setPositiveButton("确认", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (errorDialog != null && errorDialog.isShowing()) {
					errorDialog.dismiss();
				}
				removeMessages(MSG_CLOSE_WRANING);
			}
		});
		errorDialog =  builder.create();
		errorDialog.show();
		Message messageFinish = Message.obtain();
		messageFinish.what = MSG_CLOSE_WRANING;
		sendMessageDelayed(messageFinish, 5000);
	}

	/**
	 * 使得进度条消失
	 */
	public void dismissProgress() {
//		boolean isMainLoop = (Thread.currentThread() == Looper.getMainLooper().getThread());
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
			dialog = null;
		}
	}

	public void dismissWarningProgress() {
		if (errorDialog != null && errorDialog.isShowing()) {
			errorDialog.dismiss();
		}
		errorDialog = null;
	}

	public void quit() {
		super.quit();
		dismissProgress();
		dismissWarningProgress();
	}
}
