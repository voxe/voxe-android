package org.voxe.android.activity;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

import java.util.HashSet;

import org.voxe.android.R;
import org.voxe.android.VoxeApplication;
import org.voxe.android.adapter.SelectTagAdapter;
import org.voxe.android.common.Analytics;
import org.voxe.android.model.Election;
import org.voxe.android.model.ElectionsHolder;
import org.voxe.android.model.Tag;
import org.voxe.android.view.SelectCandidatesButton;
import org.voxe.android.view.SelectCandidatesButton_;

import android.content.Intent;
import android.widget.ListView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.google.common.base.Optional;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.App;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.Extra;
import com.googlecode.androidannotations.annotations.ItemClick;
import com.googlecode.androidannotations.annotations.OptionsItem;
import com.googlecode.androidannotations.annotations.ViewById;

@EActivity(R.layout.select_tag_list)
public class SelectTagActivity extends SherlockActivity {

	public static final int BACK_TO_SELECT_CANDIDATES = 1;

	@ViewById
	ListView list;

	@Bean
	SelectTagAdapter tagAdapter;

	private Election election;

	@Bean
	Analytics analytics;

	@App
	VoxeApplication application;

	@Extra
	HashSet<String> selectedCandidateIds;

	@Extra
	int electionIndex;

	@AfterViews
	void initLayout() {
		Optional<ElectionsHolder> optionalElectionHolder = application.getElectionHolder();
		if (optionalElectionHolder.isPresent()) {
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			getSupportActionBar().setHomeButtonEnabled(true);
			ElectionsHolder electionHolder = optionalElectionHolder.get();
			election = electionHolder.elections.get(electionIndex);

			tagAdapter.init(election.tags);
			list.setAdapter(tagAdapter);

			SelectCandidatesButton customTitle = SelectCandidatesButton_.build(this);

			customTitle.init(election, selectedCandidateIds);

			ActionBar actionBar = getSupportActionBar();
			actionBar.setDisplayShowTitleEnabled(false);
			actionBar.setCustomView(customTitle);
			actionBar.setDisplayShowCustomEnabled(true);

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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == BACK_TO_SELECT_CANDIDATES) {
			selectCandidates();
		}
	}

	public void selectCandidates() {
		analytics.backToCandidatesFromTag(election);
		setResult(RESULT_CANCELED);
		finish();
	}

	@OptionsItem
	void homeSelected() {
		SelectElectionActivity_ //
				.intent(this) //
				.flags(FLAG_ACTIVITY_CLEAR_TOP) //
				.start();
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
