package com.joinplato.android.candidates;

import java.util.List;

import android.widget.GridView;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.ItemClick;
import com.googlecode.androidannotations.annotations.OptionsItem;
import com.googlecode.androidannotations.annotations.ViewById;
import com.joinplato.android.R;
import com.joinplato.android.actionbar.ActionBarActivity;
import com.joinplato.android.common.Candidate;
import com.joinplato.android.common.HomeHelper;

@EActivity(R.layout.candidates)
public class CandidateListActivity extends ActionBarActivity {

	@ViewById
	GridView gridview;

	private List<Candidate> candidates;

	@AfterViews
	void mockList() {
		candidates = Candidate.mockCandidates();
		CandidateAdapter adapter = new CandidateAdapter(this, candidates);
		gridview.setAdapter(adapter);
	}
	
	
	@OptionsItem
	public void homeSelected() {
		HomeHelper.backToHome(this);
	}

	@ItemClick
	void gridviewItemClicked(Candidate candidate) {
		CandidateActivity.start(this, candidates, candidates.indexOf(candidate));
	}

}
