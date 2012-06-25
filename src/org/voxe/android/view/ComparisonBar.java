package org.voxe.android.view;

import java.util.HashSet;
import java.util.List;

import org.voxe.android.R;
import org.voxe.android.activity.ComparisonActivity;
import org.voxe.android.model.Candidate;
import org.voxe.android.model.Election;
import org.voxe.android.model.Tag;

import android.content.Context;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EViewGroup;
import com.googlecode.androidannotations.annotations.ViewById;

@EViewGroup(R.layout.comparison_bar)
public class ComparisonBar extends FrameLayout {

	@ViewById
	ImageView candidate1ImageView;

	@ViewById
	ImageView candidate2ImageView;

	@ViewById
	ImageView selectedTagIcon;

	public ComparisonBar(Context context) {
		super(context);
	}

	public void init(Election election, Tag selectedTag, HashSet<String> selectedCandidateIds) {
		List<Candidate> selectedCandidates = election.selectedCandidatesByCandidateIds(selectedCandidateIds);

		Candidate candidate1 = selectedCandidates.get(0);
		candidate1.insertPhoto(candidate1ImageView);

		Candidate candidate2 = selectedCandidates.get(1);
		candidate2.insertPhoto(candidate2ImageView);

		selectedTagIcon.setImageBitmap(selectedTag.icon.bitmap);
	}

	@Click
	void selectCandidatesButtonClicked() {
		ComparisonActivity activity = (ComparisonActivity) getContext();
		activity.selectCandidates();
	}

	@Click
	void selectedTagIconClicked() {
		ComparisonActivity activity = (ComparisonActivity) getContext();
		activity.selectTag();
	}

}
