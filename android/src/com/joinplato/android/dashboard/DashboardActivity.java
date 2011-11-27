package com.joinplato.android.dashboard;

import android.os.Bundle;

import com.googlecode.androidannotations.annotations.App;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.joinplato.android.R;
import com.joinplato.android.TheVoxeApplication;
import com.joinplato.android.actionbar.ActionBarActivity;
import com.joinplato.android.candidates.CandidateListActivity;
import com.joinplato.android.debate.DebateActivity;
import com.joinplato.android.programs.SelectCandidatesActivity;
import com.joinplato.android.quizz.QuizzActivity;

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
