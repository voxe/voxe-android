package org.voxe.android.tag;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.voxe.android.R;
import org.voxe.android.VoxeApplication;
import org.voxe.android.candidates.SelectCandidatesActivity;
import org.voxe.android.common.AboutDialogHelper;
import org.voxe.android.common.Analytics;
import org.voxe.android.common.ComparisonPref_;
import org.voxe.android.comparison.ComparisonActivity_;
import org.voxe.android.loading.LoadingActivity_;
import org.voxe.android.model.Candidate;
import org.voxe.android.model.Election;
import org.voxe.android.model.ElectionHolder;
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
import com.googlecode.androidannotations.annotations.ItemClick;
import com.googlecode.androidannotations.annotations.OptionsItem;
import com.googlecode.androidannotations.annotations.ViewById;
import com.googlecode.androidannotations.annotations.sharedpreferences.Pref;

/**
 * TODO show selected candidates
 */
@EActivity(R.layout.select_tag_list)
public class SelectTagActivity extends SherlockActivity {

	public static final int BACK_TO_SELECT_CANDIDATES = 1;

	@Pref
	ComparisonPref_ comparisonPref;

	@ViewById
	ListView list;

	private SelectTagAdapter tagAdapter;

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Optional<ElectionHolder> optionalElectionHolder = application.getElectionHolder();
		if (optionalElectionHolder.isPresent()) {
			ElectionHolder electionHolder = optionalElectionHolder.get();
			election = electionHolder.election;

			if (hasSelectedTag()) {
				startComparisonActivity();
			}

		} else {
			LoadingActivity_.intent(this).flags(FLAG_ACTIVITY_CLEAR_TOP).start();
			finish();
		}
	}

	private boolean hasSelectedTag() {
		String selectedTagId = comparisonPref.selectedTagId().get();
		Tag selectedTag = election.tagFromId(selectedTagId);
		return selectedTag != null;
	}

	private void startComparisonActivity() {
		Intent intent = ComparisonActivity_.intent(this).get();
		startActivityForResult(intent, 1);
	}

	@AfterViews
	void initLayout() {
		if (isFinishing()) {
			return;
		}

		tagAdapter = new SelectTagAdapter(this, new ArrayList<Tag>());
		list.setAdapter(tagAdapter);
		tagAdapter.updateTags(election.tags);

		String selectedCandidateIdsAsString = comparisonPref.selectedCandidateIds().get();

		Set<String> selectedCandidateIds = SelectCandidatesActivity.splitCandidateIds(selectedCandidateIdsAsString);

		List<Candidate> selectedCandidates = election.selectedCandidatesByCandidateIds(selectedCandidateIds);

		Candidate candidate1 = selectedCandidates.get(0);
		candidate1.insertPhoto(candidate1ImageView);

		Candidate candidate2 = selectedCandidates.get(1);
		candidate2.insertPhoto(candidate2ImageView);

	}

	@ItemClick
	void listItemClicked(Tag selectedTag) {
		comparisonPref.selectedTagId().put(selectedTag.id);
		analytics.tagSelected(election, selectedTag);
		startComparisonActivity();
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
		comparisonPref.selectedTagId().remove();
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
