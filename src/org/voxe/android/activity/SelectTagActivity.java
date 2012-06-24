package org.voxe.android.activity;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

import java.util.HashSet;
import java.util.List;

import org.voxe.android.R;
import org.voxe.android.VoxeApplication;
import org.voxe.android.adapter.SelectTagAdapter;
import org.voxe.android.common.AboutDialogHelper;
import org.voxe.android.common.Analytics;
import org.voxe.android.model.Candidate;
import org.voxe.android.model.Election;
import org.voxe.android.model.ElectionsHolder;
import org.voxe.android.model.Tag;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockActivity;
import com.google.common.base.Optional;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.App;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.Extra;
import com.googlecode.androidannotations.annotations.ItemClick;
import com.googlecode.androidannotations.annotations.OptionsItem;
import com.googlecode.androidannotations.annotations.ViewById;

/**
 * TODO show selected candidates
 */
@EActivity(R.layout.select_tag_list)
public class SelectTagActivity extends SherlockActivity {

	public static final int BACK_TO_SELECT_CANDIDATES = 1;

	@ViewById
	ListView list;

	@Bean
	SelectTagAdapter tagAdapter;

	private Election election;

	@Bean
	AboutDialogHelper aboutDialogHelper;

	@Bean
	Analytics analytics;

	@App
	VoxeApplication application;

	@ViewById
	ImageView candidate1ImageView;

	@ViewById
	ImageView candidate2ImageView;

	@Extra
	HashSet<String> selectedCandidateIds;

	@Extra
	int electionIndex;

	@AfterViews
	void initLayout() {
		Optional<ElectionsHolder> optionalElectionHolder = application.getElectionHolder();
		if (optionalElectionHolder.isPresent()) {
			ElectionsHolder electionHolder = optionalElectionHolder.get();
			election = electionHolder.elections.get(electionIndex);

			tagAdapter.init(election.tags);
			list.setAdapter(tagAdapter);

			List<Candidate> selectedCandidates = election.selectedCandidatesByCandidateIds(selectedCandidateIds);

			Candidate candidate1 = selectedCandidates.get(0);
			candidate1.insertPhoto(candidate1ImageView);

			Candidate candidate2 = selectedCandidates.get(1);
			candidate2.insertPhoto(candidate2ImageView);
		} else {
			LoadingActivity_ //
					.intent(this) //
					.flags(FLAG_ACTIVITY_CLEAR_TOP) //
					.start();
			finish();
		}
	}

	@ItemClick
	void listItemClicked(Tag selectedTag) {
		analytics.tagSelected(election, selectedTag);
		Intent intent = ComparisonActivity_ //
				.intent(this) //
				.electionIndex(electionIndex) //
				.selectedCandidateIds(selectedCandidateIds) //
				.selectedTagId(selectedTag.id) //
				.get();
		startActivityForResult(intent, 1);
	}

	@OptionsItem
	public void homeSelected() {
		showDialog(R.id.about_dialog);
	}

	@Override
	protected Dialog onCreateDialog(int id, Bundle args) {
		switch (id) {
		case R.id.about_dialog:
			return aboutDialogHelper.createAboutDialog();
		default:
			return null;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == BACK_TO_SELECT_CANDIDATES) {
			selectCandidatesButtonClicked();
		}
	}

	@Click
	void selectCandidatesButtonClicked() {
		analytics.backToCandidatesFromTag(election);
		setResult(RESULT_CANCELED);
		finish();
	}

	@Override
	protected void onPause() {
		super.onPause();
		analytics.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		analytics.onResume();
	}

}
