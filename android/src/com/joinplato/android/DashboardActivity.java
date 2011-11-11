package com.joinplato.android;

import android.content.Intent;

import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.joinplato.android.actionbar.ActionBarActivity;

@EActivity(R.layout.dashboard)
public class DashboardActivity extends ActionBarActivity {

	@Click
	void dashboardNewsClicked() {
		startActivity(new Intent(this, NewsActivity_.class));
	}

	@Click
	void dashboardCandidatesClicked() {
		startActivity(new Intent(this, CandidateListActivity_.class));
	}

	@Click
	void dashboardProgramsClicked() {
		startActivity(new Intent(this, ProgramsActivity_.class));
	}

	@Click
	void dashboardQuizzClicked() {
		startActivity(new Intent(this, QuizzActivity_.class));
	}

}
