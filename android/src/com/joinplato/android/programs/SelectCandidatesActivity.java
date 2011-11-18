package com.joinplato.android.programs;

import java.util.List;

import android.widget.GridView;
import android.widget.Toast;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.ItemClick;
import com.googlecode.androidannotations.annotations.OptionsItem;
import com.googlecode.androidannotations.annotations.OptionsMenu;
import com.googlecode.androidannotations.annotations.ViewById;
import com.joinplato.android.R;
import com.joinplato.android.actionbar.ActionBarActivity;
import com.joinplato.android.common.Candidate;
import com.joinplato.android.common.HomeHelper;

@EActivity(R.layout.candidates)
@OptionsMenu(R.menu.candidate_list)
public class SelectCandidatesActivity extends ActionBarActivity {

	@ViewById
	GridView gridview;

	private List<Candidate> candidates;

	@AfterViews
	void mockList() {
		candidates = Candidate.mockCandidates();
		SelectCandidateAdapter adapter = new SelectCandidateAdapter(this, candidates);
		gridview.setAdapter(adapter);
	}

	@ItemClick
	void gridviewItemClicked(Candidate candidate) {
		ProgramsActivity.start(this);
	}
	
	@OptionsItem
	public void homeSelected() {
		HomeHelper.backToHome(this);
	}
	
	@OptionsItem
	public void menuRefreshSelected() {
		Toast.makeText(this, "Refreshing...", Toast.LENGTH_SHORT).show();
	}

}
