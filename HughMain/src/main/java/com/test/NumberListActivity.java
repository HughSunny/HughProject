package com.test;


import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;

import com.set.R;
import com.set.R.id;
import com.set.R.layout;
import com.set.dummy.DummyContent;
import com.test.fragment.BottomFragment;
import com.test.fragment.FragAdapter;
import com.test.fragment.NumberDetailFragment;
import com.test.fragment.NumberListFragment;

/**
 * An activity representing a list of Items. This activity has different
 * presentations for handset and tablet-size devices. On handsets, the activity
 * presents a list of items, which when touched, lead to a
 * {@link NumberDetailActivity} representing item details. On tablets, the
 * activity presents the list of items and item details side-by-side using two
 * vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link NumberListFragment} and the item details (if present) is a
 * {@link NumberDetailFragment}.
 * <p>
 * This activity also implements the required
 * {@link NumberListFragment.Callbacks} interface to listen for item selections.
 */
public class NumberListActivity extends FragmentActivity implements
		NumberListFragment.Callbacks {
	private static final String TAG = "NumberListActivity";
	/**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet
	 * device.
	 */
	private boolean mTwoPane;
	private ViewPager vp;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_number_list);

		if (findViewById(R.id.number_detail_container) != null) {
			// The detail container view will be present only in the
			// large-screen layouts (res/values-large and
			// res/values-sw600dp). If this view is present, then the
			// activity should be in two-pane mode.
			mTwoPane = true;
			// In two-pane mode, list items should be given the
			// 'activated' state when touched.
			((NumberListFragment) getSupportFragmentManager().findFragmentById(
					R.id.number_list)).setActivateOnItemClick(true);
			vp = (ViewPager) findViewById(R.id.vp_main);
			initViewPage();
		}

		// TODO: If exposing deep links into your app, handle intents here.
	}
	private FragAdapter adapter;
	/**
     * 初始化 view page的相关数据
     */
    private void initViewPage() {
        List<Fragment> fragments = new ArrayList<Fragment>();
        fragments.add(BottomFragment.newInstance(DummyContent.ITEM_MAP.get("1").getBottom()));
        fragments.add(BottomFragment.newInstance(DummyContent.ITEM_MAP.get("2").getBottom()));
        fragments.add(BottomFragment.newInstance(DummyContent.ITEM_MAP.get("3").getBottom()));
        adapter = new FragAdapter(getSupportFragmentManager(), fragments);
        vp.setAdapter(adapter);
        vp.setCurrentItem(0);
        vp.setOnPageChangeListener(new MyVPageChangeListener());
    }
    
    
	private class MyVPageChangeListener implements OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int arg0) {
            // TODO Auto-generated method stub
        	Log.i(TAG, "onPageScrollStateChanged + " + arg0);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onPageSelected(int location) {
        }
    }


	/**
	 * Callback method from {@link NumberListFragment.Callbacks} indicating that
	 * the item with the given ID was selected.
	 */
	@Override
	public void onItemSelected(String id) {
		if (mTwoPane) {
			// In two-pane mode, show the detail view in this activity by
			// adding or replacing the detail fragment using a
			// fragment transaction.
			Bundle arguments = new Bundle();
			arguments.putString(NumberDetailFragment.ARG_ITEM_ID, id);
			NumberDetailFragment fragment = new NumberDetailFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.number_detail_container, fragment).commit();
			
//			//得到一个fragment 事务（类似sqlite的操作）
//			FragmentTransaction ft = getFragmentManager()
//					.beginTransaction();
//			ft.replace(R.id.details, details);//将得到的fragment 替换当前的viewGroup内容，add则不替换会依次累加
//			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);//设置动画效果
//			ft.commit();//提交
			//TODO  BOTTOM CHANGE
			vp.setCurrentItem(Integer.parseInt(id) - 1);
		} else {
			// In single-pane mode, simply start the detail activity
			// for the selected item ID.
			Intent detailIntent = new Intent(this, NumberDetailActivity.class);
			detailIntent.putExtra(NumberDetailFragment.ARG_ITEM_ID, id);
			startActivity(detailIntent);
		}
	}
}
