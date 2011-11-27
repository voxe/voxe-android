package com.joinplato.android.common;

import com.joinplato.android.actionbar.ActionBarActivity;

//@EActivity
public abstract class ActionBarHomeActivity extends ActionBarActivity {

//	@OptionsItem
	public void homeSelected() {
		HomeHelper.backToHome(this);
	}
	
}
