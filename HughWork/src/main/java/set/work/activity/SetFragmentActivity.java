package set.work.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Window;

import set.work.app.BaseApp;
import set.work.fragment.SetFragment;
import set.work.listener.FragmentCallBack;
import set.work.utils.ActivityCollector;
import set.work.utils.ApplicationUtil;
import set.work.utils.LogUtil;

/**
 *
 */
public  abstract class SetFragmentActivity extends FragmentActivity implements FragmentCallBack {
	private static final String TAG = "SetFragmentActivity";
	/** 可以返回的fragment ，按返回的时候，先由这个fragment处理，
	 *  不随时消亡，存在该对象更新问题 */
	protected SetFragment mBackHandedFragment;
	/** 当前的fragment */
	protected SetFragment mCurFragment;
	private int contentId ;
	/** fragment 能不能返回出栈 对应 */
	public boolean fragCanNotBack;
	private boolean hadIntercept;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		ActivityCollector.registry(this);
	}

	@Override
	public void setBackHandledFragment(Fragment selectedFragment) {
		LogUtil.logE(TAG,">>>>> setBackHandledFragment");
		if (selectedFragment instanceof SetFragment) {
			this.mBackHandedFragment = (SetFragment)selectedFragment;
		}
	}

	@Override
	public void onFragmentCallBack(Intent intent) {
		// fragment 传递参数给activity
	}

	@Override
	public void onFragmentInteraction(Uri uri) {

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

			FragmentManager fragmentManager = getSupportFragmentManager();
			int backStackCount = fragmentManager.getBackStackEntryCount();
			LogUtil.logW(TAG, "backStackCount = " + backStackCount);
			if (backStackCount == 0) {
				super.onBackPressed();
			} else {
//				FragmentManager.BackStackEntry backStackEntry = fragmentManager.getBackStackEntryAt(backStackCount - 1);
//				LogUtil.logW(TAG, "backStackEntry name " + backStackEntry.getName());
				fragmentManager.popBackStack();
				//不管怎么pop，size一直是最大时候的
				int fragmentSize = getSupportFragmentManager().getFragments().size();
				LogUtil.logW(TAG, "fragmentSize = " + fragmentSize);
			}
		}
	}


	/**
	 *
	 * @param fragment
	 * @param addstack 是否将fragment 加入到回退栈
     */
	protected void changeFragment(SetFragment fragment, boolean addstack, String tag) {
		mBackHandedFragment = fragment;
		FragmentTransaction t = getSupportFragmentManager().beginTransaction();
		t.replace(contentId, fragment,tag);
		if (addstack) {
			t.addToBackStack(tag);
		}
		mCurFragment = fragment;
		t.commit();
	}

	/**
	 * 切换fragment
	 * @param from
	 * @param to
	 * @param addstack
	 * @param tag
	 */
	public void switchContent(SetFragment from, SetFragment to, boolean addstack, String tag) {
		mBackHandedFragment = to;
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction()
				.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
		if (!to.isAdded()) { // 先判断是否被add过
			if (addstack) {
				transaction.addToBackStack(tag);
			}
			transaction.hide(from).add(contentId, to, tag).commit(); // 隐藏当前的fragment，add下一个到Activity中
		} else {
			transaction.hide(from).show(to).commit(); // 隐藏当前的fragment，显示下一个
		}
		mCurFragment = to;
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
		ActivityCollector.deRegistry(this);
	}
	@Override
	protected void onPause(){
		super.onPause();
	}



}
