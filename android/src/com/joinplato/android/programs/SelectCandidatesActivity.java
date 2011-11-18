package com.joinplato.android.programs;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.View;
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
import com.joinplato.android.common.HomeHelper;

@EActivity(R.layout.select_candidates)
@OptionsMenu(R.menu.select_candidates)
public class SelectCandidatesActivity extends ActionBarActivity {
	
	public static void start(Context context) {
		context.startActivity(new Intent(context, SelectCandidatesActivity_.class));
	}

	@ViewById
	GridView gridview;

	private List<SelectedCandidate> candidates;
	
	private SelectCandidateAdapter adapter;

	@AfterViews
	void mockList() {
		candidates = SelectedCandidate.mockSelectedCandidates();
		adapter = new SelectCandidateAdapter(this, candidates);
		gridview.setAdapter(adapter);
	}

	@ItemClick
	void gridviewItemClicked(SelectedCandidate candidate) {
		candidate.toggleSelected();
		
		int position = candidates.indexOf(candidate);
		View candidateView = gridview.getChildAt(position - gridview.getFirstVisiblePosition());
		
		adapter.updateCheckbox(candidateView, candidate);
	}
	
	@OptionsItem
	public void homeSelected() {
		HomeHelper.backToHome(this);
	}
	
	@OptionsItem
	public void menuOkSelected() {
		for (SelectedCandidate candidate : candidates) {
			if (candidate.isSelected()) {
				ProgramsActivity.start(this, candidates);
				return;
			}
		}
		Toast.makeText(this, R.string.select_one_candidate, Toast.LENGTH_SHORT).show();
	}

}
