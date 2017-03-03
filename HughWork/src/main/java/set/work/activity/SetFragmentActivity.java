package set.work.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import set.work.app.BaseApp;
import set.work.fragment.SetFragment;
import set.work.listener.BackHandledInterface;
import set.work.utils.ApplicationUtil;
import set.work.utils.LogUtil;

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
			if (fragCanNotBack) {
				//super.onBackPressed();
				this.finish();
			}
			if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
				super.onBackPressed();
			} else {
				getSupportFragmentManager().popBackStack();
			}
		}
	}


	/**
	 *
	 * @param fragment
	 * @param addstack 是否将fragment 加入到回退栈
     */
	protected void changeFragment(SetFragment fragment, boolean addstack,String tag) {
		FragmentTransaction t = getSupportFragmentManager().beginTransaction();
		t.replace(contentId, fragment,tag);
		if (addstack) {
			t.addToBackStack(null);
		}
		t.commit();
	}

	public void switchContent(SetFragment from, SetFragment to, boolean addstack, String tag) {
		if (mBackHandedFragment != to) {
			mBackHandedFragment = to;
			FragmentTransaction transaction = getSupportFragmentManager().beginTransaction()
					.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
			if (!to.isAdded()) { // 先判断是否被add过
				if (addstack) {
					transaction.addToBackStack(null);
				}
				transaction.hide(from).add(contentId, to, tag).commit(); // 隐藏当前的fragment，add下一个到Activity中
			} else {
				transaction.hide(from).show(to).commit(); // 隐藏当前的fragment，显示下一个
			}
		}
	}

	public void setContentId(int contentId) {
		this.contentId = contentId;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		//super.onSaveInstanceState(outState); 总是执行这句代码来调用父类去保存视图层的状态，会导致fragmen重影，不能正常恢复状态
//		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onStop() {
		super.onStop();

		if (!ApplicationUtil.isAppOnForeground(this)) {
			//app 进入后台
			//全局变量isActive = false 记录当前已经进入后台
			BaseApp.isActive = false;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		//if (!isActive) {
		//app 从后台唤醒，进入前台
		//isActive = true;
		//}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onPause(){
		super.onPause();
	}



}
