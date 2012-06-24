package org.voxe.android.common;

import java.util.Set;

import org.voxe.android.model.Election;
import org.voxe.android.model.Tag;

import android.app.Activity;
import android.os.Bundle;

import com.google.common.collect.Iterables;
import com.googlecode.androidannotations.annotations.AfterInject;
import com.googlecode.androidannotations.annotations.EBean;
import com.googlecode.androidannotations.annotations.RootContext;
import com.ubikod.capptain.android.sdk.CapptainAgent;
import com.ubikod.capptain.android.sdk.CapptainAgentUtils;

@EBean
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
		Bundle bundle = new Bundle();
		bundle.putString("election", election.namespace);
		bundle.putString("selectedTag", selectedTag.namespace);
		CapptainAgent.getInstance(activity).sendSessionEvent("tagSelected", bundle);
	}

	public void backToCandidatesFromTag(Election election) {
		Bundle bundle = new Bundle();
		bundle.putString("election", election.namespace);
		CapptainAgent.getInstance(activity).sendSessionEvent("backToCandidatesFromTag", bundle);
	}

	public void twoCandidatesSelected(Election election, Set<String> selectedCandidateIds) {
		Bundle bundle = new Bundle();
		bundle.putStringArray("selectedCandidateIds", Iterables.toArray(selectedCandidateIds, String.class));
		bundle.putString("election", election.namespace);
		CapptainAgent.getInstance(activity).sendSessionEvent("twoCandidatesSelected", bundle);
	}

	public void backToCandidatesFromComparison(Election election) {
		Bundle bundle = new Bundle();
		bundle.putString("election", election.namespace);
		CapptainAgent.getInstance(activity).sendSessionEvent("backToCandidatesFromComparison", bundle);
	}

	public void backToTagFromComparison(Election election) {
		Bundle bundle = new Bundle();
		bundle.putString("election", election.namespace);
		CapptainAgent.getInstance(activity).sendSessionEvent("backToTagFromComparison", bundle);
	}

	public void electionSelected(Election selectedElection) {
		Bundle bundle = new Bundle();
		bundle.putString("selectedElection", selectedElection.namespace);
		CapptainAgent.getInstance(activity).sendSessionEvent("electionSelected", bundle);
	}

}
