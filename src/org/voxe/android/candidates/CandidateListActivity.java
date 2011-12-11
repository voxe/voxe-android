package org.voxe.android.candidates;

import java.util.Collections;
import java.util.List;

import org.voxe.android.R;
import org.voxe.android.TheVoxeApplication;
import org.voxe.android.actionbar.ActionBarActivity;
import org.voxe.android.common.HomeHelper;
import org.voxe.android.model.Candidate;
import org.voxe.android.model.ElectionHolder;

import android.content.Context;
import android.content.Intent;
import android.widget.GridView;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.App;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.ItemClick;
import com.googlecode.androidannotations.annotations.OptionsItem;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.ViewById;

@EActivity(R.layout.candidates)
public class CandidateListActivity extends ActionBarActivity {

	public static void start(Context context) {
		context.startActivity(new Intent(context, CandidateListActivity_.class));
	}

	@ViewById
	GridView gridview;

	List<Candidate> candidates;

	@App
	TheVoxeApplication application;

	CandidateAdapter adapter;

	@AfterViews
	void mockList() {
		adapter = new CandidateAdapter(this);
		gridview.setAdapter(adapter);
		loadCandidates();
	}

	@Background
	void loadCandidates() {
		ElectionHolder electionHolder = application.getElectionHolder().get();
		List<Candidate> loadedCandidates = electionHolder.election.candidates;
		Collections.sort(loadedCandidates);
		updateCandidateGrid(loadedCandidates);
	}

	@UiThread
	void updateCandidateGrid(List<Candidate> loadedCandidates) {
		if (!isFinishing()) {
			candidates = loadedCandidates;
			adapter.updateCandidates(candidates);
		}
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
