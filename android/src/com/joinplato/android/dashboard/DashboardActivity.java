package com.joinplato.android.dashboard;

import android.content.Intent;

import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.joinplato.android.R;
import com.joinplato.android.actionbar.ActionBarActivity;
import com.joinplato.android.candidates.CandidateListActivity_;
import com.joinplato.android.debate.DebateActivity;
import com.joinplato.android.programs.SelectCandidatesActivity;
import com.joinplato.android.quizz.QuizzActivity_;

@EActivity(R.layout.dashboard)
public class DashboardActivity extends ActionBarActivity {

	@Click
	void debatesClicked() {
		DebateActivity.start(this);
	}

	@Click
	void candidatesClicked() {
		startActivity(new Intent(this, CandidateListActivity_.class));
	}

	@Click
	void programsClicked() {
		SelectCandidatesActivity.start(this);
	}

	@Click
	void quizzClicked() {
		startActivity(new Intent(this, QuizzActivity_.class));
	}

}
