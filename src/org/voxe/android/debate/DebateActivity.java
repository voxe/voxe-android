package org.voxe.android.debate;

import org.voxe.android.R;
import org.voxe.android.actionbar.ActionBarActivity;
import org.voxe.android.common.HomeHelper;

import android.content.Context;
import android.content.Intent;

import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.OptionsItem;

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
