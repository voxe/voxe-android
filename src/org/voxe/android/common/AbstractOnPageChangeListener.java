package org.voxe.android.common;

import android.support.v4.view.ViewPager.OnPageChangeListener;

/**
 * Abstract class helper to avoid implementing all callbacks in
 * {@link OnPageChangeListener}
 */
public abstract class AbstractOnPageChangeListener implements OnPageChangeListener {

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int arg0) {

	}

}
