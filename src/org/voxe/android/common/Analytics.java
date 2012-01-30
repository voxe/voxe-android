package org.voxe.android.common;

import java.util.Set;

import org.voxe.android.model.Election;
import org.voxe.android.model.Tag;

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

	public void tagSelected(Election election, Tag selectedTag) {
		
	}

	public void backToCandidatesFromTag(Election election) {
		
	}

	public void twoCandidatesSelected(Election election, Set<String> selectedCandidateIds) {
		
	}

	public void backToCandidatesFromComparison(Election election) {
		
	}

	public void backToTagFromComparison(Election election) {
		
	}

}
