package org.voxe.android.common;

import org.voxe.android.actionbar.ActionBarActivity;

//@EActivity
public abstract class ActionBarHomeActivity extends ActionBarActivity {

//	@OptionsItem
	public void homeSelected() {
		HomeHelper.backToHome(this);
	}
	
}
