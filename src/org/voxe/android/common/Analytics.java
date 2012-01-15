package org.voxe.android.common;

import android.app.Activity;

import com.googlecode.androidannotations.annotations.AfterInject;
import com.googlecode.androidannotations.annotations.Enhanced;
import com.googlecode.androidannotations.annotations.RootContext;
import com.ubikod.capptain.android.sdk.CapptainAgent;
import com.ubikod.capptain.android.sdk.CapptainAgentUtils;

@Enhanced
public class Analytics {

	@RootContext
	Activity activity;
	
	String activityNameOnCapptain;

	@AfterInject
	void buildActivityName() {
		activityNameOnCapptain = CapptainAgentUtils.buildCapptainActivityName(activity.getClass());
	}

	public void onResume() {
		CapptainAgent.getInstance(activity).startActivity(activity, activityNameOnCapptain, null);
	}

	public void onPause() {
		CapptainAgent.getInstance(activity).endActivity();
	}

}
