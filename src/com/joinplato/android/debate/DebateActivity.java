package com.joinplato.android.debate;

import android.content.Context;
import android.content.Intent;

import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.OptionsItem;
import com.joinplato.android.R;
import com.joinplato.android.actionbar.ActionBarActivity;
import com.joinplato.android.common.HomeHelper;

@EActivity(R.layout.debate)
public class DebateActivity extends ActionBarActivity {

	public static void start(Context context) {
		context.startActivity(new Intent(context, DebateActivity_.class));
	}
	
	@OptionsItem
	public void homeSelected() {
		HomeHelper.backToHome(this);
	}

}
