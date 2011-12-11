package org.voxe.android.dashboard;

import android.os.Bundle;

import com.googlecode.androidannotations.annotations.App;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import org.voxe.android.R;
import org.voxe.android.TheVoxeApplication;
import org.voxe.android.actionbar.ActionBarActivity;
import org.voxe.android.candidates.CandidateListActivity;
import org.voxe.android.debate.DebateActivity;
import org.voxe.android.programs.SelectCandidatesActivity;
import org.voxe.android.quizz.QuizzActivity;

@EActivity(R.layout.dashboard)
public class DashboardActivity extends ActionBarActivity {
	
	@App
	TheVoxeApplication application;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		preloadElectionData();
	}
	
	@Background
	void preloadElectionData() {
		application.getElectionHolder();
	}

	@Click
	void debatesClicked() {
		DebateActivity.start(this);
	}

	@Click
	void candidatesClicked() {
		CandidateListActivity.start(this);
	}

	@Click
	void programsClicked() {
		SelectCandidatesActivity.start(this);
	}

	@Click
	void quizzClicked() {
		QuizzActivity.start(this);
	}

}
