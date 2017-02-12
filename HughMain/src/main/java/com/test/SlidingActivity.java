/*
 * Copyright (C) 2012 yueyueniao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.test;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import com.set.R;
import com.set.ui.view.SlidingMenu;
import com.test.fragment.LeftFragment;
import com.test.fragment.RightFragment;
import com.test.fragment.ViewPageFragment;
import com.test.fragment.ViewPageFragment.MyPageChangeListener;

public class SlidingActivity extends FragmentActivity {
	SlidingMenu mSlidingMenu;
	LeftFragment leftFragment;
	RightFragment rightFragment;
	ViewPageFragment viewPageFragment;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.slide_menu_test_main);
		init();
		initListener();

	}

	private void init() {
		mSlidingMenu = (SlidingMenu) findViewById(R.id.slidingMenu);
		mSlidingMenu.setLeftView(getLayoutInflater().inflate(
				R.layout.slide_menu_left_frame, null));
		mSlidingMenu.setRightView(getLayoutInflater().inflate(
				R.layout.slide_menu_right_frame, null));
		mSlidingMenu.setCenterView(getLayoutInflater().inflate(
				R.layout.slide_menu_center_frame, null));

		FragmentTransaction t = this.getSupportFragmentManager()
				.beginTransaction();
		leftFragment = new LeftFragment();
		t.replace(R.id.left_frame, leftFragment);

		rightFragment = new RightFragment();
		t.replace(R.id.right_frame, rightFragment);

		viewPageFragment = new ViewPageFragment();
		t.replace(R.id.center_frame, viewPageFragment);
		t.commit();
	}

	private void initListener() {
		viewPageFragment.setMyPageChangeListener(new MyPageChangeListener() {
			
			@Override
			public void onPageSelected(int position) {
				if(viewPageFragment.isFirst()){
					mSlidingMenu.setCanSliding(true,false);
				}else if(viewPageFragment.isEnd()){
					mSlidingMenu.setCanSliding(false,true);
				}else{
					mSlidingMenu.setCanSliding(false,false);
				}
			}
		});
	}

	public void showLeft() {
		mSlidingMenu.showLeftView();
	}

	public void showRight() {
		mSlidingMenu.showRightView();
	}

}
