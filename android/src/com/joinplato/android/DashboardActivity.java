package com.joinplato.android;

import android.content.Intent;

import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.joinplato.android.actionbar.ActionBarActivity;
import com.joinplato.android.candidates.CandidateListActivity_;
import com.joinplato.android.programs.ProgramsActivity_;
import com.joinplato.android.quizz.QuizzActivity_;

@EActivity(R.layout.dashboard)
public class DashboardActivity extends ActionBarActivity {

	@Click
	void debatesClicked() {
//		startActivity(new Intent(this, NewsActivity_.class));
	}

	@Click
	void candidatesClicked() {
		startActivity(new Intent(this, CandidateListActivity_.class));
	}

	@Click
	void programsClicked() {
		startActivity(new Intent(this, ProgramsActivity_.class));
	}

	@Click
	void quizzClicked() {
		startActivity(new Intent(this, QuizzActivity_.class));
	}

}
