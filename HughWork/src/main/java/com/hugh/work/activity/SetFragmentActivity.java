package com.hugh.work.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import com.hugh.work.fragment.SetFragment;
import com.hugh.work.listener.BackHandledInterface;
import com.hugh.work.utils.LogUtil;

/**
 *
 */
public class SetFragmentActivity extends FragmentActivity implements BackHandledInterface {
	private static final String TAG = "SetFragmentActivity";
	protected SetFragment mBackHandedFragment;
	private boolean hadIntercept;
	private int contentId ;
	/** fragment 能不能返回出栈 对应 */
	public boolean fragCanNotBack;
	@Override
	public void setSelectedFragment(Fragment selectedFragment) {
		LogUtil.logE(TAG,">>>>> setSelectedFragment");
		if (selectedFragment instanceof SetFragment ) {
			this.mBackHandedFragment = (SetFragment)selectedFragment;
		}
	}
	@Override
	public void onBackPressed() {
		LogUtil.logE(TAG, ">>>>> onBackPressed : mBackHandedFragment is null ( " + (mBackHandedFragment == null) + ") ; fragCanNotBack = " + fragCanNotBack);
		// onBackPressed =true  切断
		if (mBackHandedFragment != null && !mBackHandedFragment.onBackPressed()) {
			if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
				super.onBackPressed();
			} else {
				getSupportFragmentManager().popBackStack();
			}
			if (fragCanNotBack) {
				//super.onBackPressed();
				this.finish();
			}
		}
	}


	/**
	 *
	 * @param fragment
	 * @param addstack 是否将fragment 加入到回退栈
     */
	protected void changeFragment(SetFragment fragment, boolean addstack) {
		FragmentTransaction t = getSupportFragmentManager().beginTransaction();
		t.replace(contentId, fragment);
		if (addstack) {
			t.addToBackStack(null);
		}
		t.commit();
	}

	public void switchContent(SetFragment from, SetFragment to, boolean addstack) {
		if (mBackHandedFragment != to) {
			mBackHandedFragment = to;
			FragmentTransaction transaction = getSupportFragmentManager().beginTransaction()
					.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
			if (!to.isAdded()) { // 先判断是否被add过
				if (addstack) {
					transaction.addToBackStack(null);
				}
				transaction.hide(from).add(contentId, to).commit(); // 隐藏当前的fragment，add下一个到Activity中
			} else {
				transaction.hide(from).show(to).commit(); // 隐藏当前的fragment，显示下一个
			}
		}
	}

	public void setContentId(int contentId) {
		this.contentId = contentId;
	}

}
