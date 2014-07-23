package org.voxe.android.view;

import java.util.HashSet;
import java.util.List;

import org.voxe.android.R;
import org.voxe.android.activity.SelectTagActivity;
import org.voxe.android.model.Candidate;
import org.voxe.android.model.Election;

import android.content.Context;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EViewGroup;
import com.googlecode.androidannotations.annotations.ViewById;

@EViewGroup(R.layout.select_candidates_button)
public class SelectCandidatesButton extends FrameLayout {

	@ViewById
	ImageView candidate1ImageView;

	@ViewById
	ImageView candidate2ImageView;

	public SelectCandidatesButton(Context context) {
		super(context);
	}

	public void init(Election election, HashSet<String> selectedCandidateIds) {
		List<Candidate> selectedCandidates = election.selectedCandidatesByCandidateIds(selectedCandidateIds);

		Candidate candidate1 = selectedCandidates.get(0);
		candidate1.insertPhoto(candidate1ImageView);

		Candidate candidate2 = selectedCandidates.get(1);
		candidate2.insertPhoto(candidate2ImageView);
	}

	@Click
	void selectCandidatesButtonClicked() {
		SelectTagActivity activity = (SelectTagActivity) getContext();
		activity.selectCandidates();
	}

}
