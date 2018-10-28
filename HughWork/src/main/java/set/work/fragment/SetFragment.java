package set.work.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import java.lang.reflect.Field;

import set.work.RunnableManager;
import set.work.handler.ProgressListenHandler;
import set.work.listener.FragmentCallBack;
import set.work.thread.RequestBack;

public abstract class SetFragment extends Fragment implements RequestBack {
	protected ProgressListenHandler handler;
	private boolean networkPauseCancel;
	protected FragmentCallBack fragCallBack;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		handler = new ProgressListenHandler(this.getActivity(), this);
		if (!(getActivity() instanceof FragmentCallBack)) {
			throw new ClassCastException("Hosting Activity must implement FragmentCallBack");
		} else {
			this.fragCallBack = (FragmentCallBack) getActivity();
		}
	}

	/**
	 * 所有继承BackHandledFragment的子类都将在这个方法中实现物理Back键按下后的逻辑
	 * FragmentActivity捕捉到物理返回键点击事件后会首先询问Fragment是否消费该事件
	 * 如果没有Fragment消息时FragmentActivity自己才会消费该事件
	 */
	public abstract boolean onBackPressed();

	@Override
	public void onStart() {
		super.onStart();
		// 告诉FragmentActivity，当前Fragment在栈顶
		fragCallBack.setBackHandledFragment(this);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onPause() {
		super.onPause();
		if (isNetworkPauseCancel()) {
			quit();
		}

	}

	protected void quit() {
		RunnableManager.getInstance().clearAll();
		if (handler != null) {
			handler.quit();
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		Field childFragmentManager;
		try {
			childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
			childFragmentManager.setAccessible(true);
			childFragmentManager.set(this, null);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
	}
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity instanceof FragmentCallBack) {
			fragCallBack = (FragmentCallBack)activity;
		}
	}

	public boolean isNetworkPauseCancel() {
		return networkPauseCancel;
	}
	public void setNetworkPauseCancel(boolean networkPauseCancel) {
		this.networkPauseCancel = networkPauseCancel;
	}

	public void setFragCallBack(FragmentCallBack fragCallBack) {
		this.fragCallBack = fragCallBack;
	}

}