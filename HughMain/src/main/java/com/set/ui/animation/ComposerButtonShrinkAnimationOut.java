package com.set.ui.animation;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.ScaleAnimation;

/**
 * 作用&目的：动画类
 * 作者：第三方开源动画
 */
public class ComposerButtonShrinkAnimationOut extends InOutAnimation {

	public ComposerButtonShrinkAnimationOut(int i) {
		super(InOutAnimation.Direction.OUT, i, new View[0]);
	}

	@Override
	protected void addInAnimation(View[] aview) {

	}

	@Override
	protected void addOutAnimation(View[] aview) {
		addAnimation(new ScaleAnimation(1F, 0F, 1F, 0F, 1, 0.5F, 1, 0.5F));
		addAnimation(new AlphaAnimation(1F, 0F));
	}

}
