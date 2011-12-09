package com.joinplato.android.common;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import android.app.Activity;
import android.content.Intent;

import com.joinplato.android.R;
import com.joinplato.android.dashboard.DashboardActivity_;

public class HomeHelper {

	public static void backToHome(Activity activity) {
		Intent intent = new Intent(activity, DashboardActivity_.class);
		intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
		activity.startActivity(intent);
		if (!UIUtils.isHoneycomb()) {
			activity.overridePendingTransition(R.anim.home_enter, R.anim.home_exit);
		}
	}

}
