package org.voxe.android.activity;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

import java.util.HashSet;

import org.voxe.android.R;
import org.voxe.android.VoxeApplication;
import org.voxe.android.adapter.SelectCandidatesAdapter;
import org.voxe.android.common.AboutDialogHelper;
import org.voxe.android.common.Analytics;
import org.voxe.android.model.Candidate;
import org.voxe.android.model.Election;
import org.voxe.android.model.ElectionsHolder;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.google.common.base.Optional;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.App;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.Extra;
import com.googlecode.androidannotations.annotations.InstanceState;
import com.googlecode.androidannotations.annotations.ItemClick;
import com.googlecode.androidannotations.annotations.OptionsItem;
import com.googlecode.androidannotations.annotations.ViewById;

@EActivity(R.layout.select_candidates_list)
public class SelectCandidatesActivity extends SherlockActivity {

	@App
	VoxeApplication application;

	@ViewById
	ListView listView;

	@ViewById
	TextView electionNameTextView;

	@Bean
	Analytics analytics;

	@Bean
	AboutDialogHelper aboutDialogHelper;

	private Election election;

	@Bean
	SelectCandidatesAdapter adapter;

	@InstanceState
	HashSet<String> selectedCandidateIds = new HashSet<String>();

	@Extra
	int electionIndex;

	@AfterViews
	void init() {
		Optional<ElectionsHolder> optionalElectionHolder = application.getElectionHolder();
		if (optionalElectionHolder.isPresent()) {
			ElectionsHolder electionHolder = optionalElectionHolder.get();
			election = electionHolder.elections.get(electionIndex);

			adapter.init(election.getMainCandidates(), selectedCandidateIds);

			listView.setAdapter(adapter);

			electionNameTextView.setText(election.name);
		} else {
			LoadingActivity_ //
					.intent(this) //
					.flags(FLAG_ACTIVITY_CLEAR_TOP) //
					.start();
			finish();
		}
	}

	void startSelectTagActivity() {
		Intent intent = SelectTagActivity_. //
				intent(this) //
				.electionIndex(electionIndex) //
				.selectedCandidateIds(selectedCandidateIds) //
				.get();
		startActivityForResult(intent, 1);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		selectedCandidateIds.clear();
		adapter.notifyDataSetChanged();
	}

	@ItemClick
	void listViewItemClicked(int position) {

		Candidate candidate = adapter.getItem(position);

		if (selectedCandidateIds.contains(candidate.id)) {
			selectedCandidateIds.remove(candidate.id);
		} else {
			selectedCandidateIds.add(candidate.id);
		}

		View candidateView = listView.getChildAt(position - listView.getFirstVisiblePosition());

		adapter.updateCheckbox(candidateView, candidate);

		if (selectedCandidateIds.size() == 2) {
			analytics.twoCandidatesSelected(election, selectedCandidateIds);

			startSelectTagActivity();
		}
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
