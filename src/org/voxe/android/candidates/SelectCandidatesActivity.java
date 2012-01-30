package org.voxe.android.candidates;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

import java.util.List;
import java.util.Set;

import org.voxe.android.R;
import org.voxe.android.VoxeApplication;
import org.voxe.android.actionbar.ActionBarActivity;
import org.voxe.android.common.AboutDialogHelper;
import org.voxe.android.common.Analytics;
import org.voxe.android.common.ComparisonPref_;
import org.voxe.android.loading.LoadingActivity_;
import org.voxe.android.model.Election;
import org.voxe.android.model.ElectionHolder;
import org.voxe.android.tag.SelectTagActivity_;
import org.voxe.android.webview.SelectedCandidate;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.base.Splitter;
import com.google.common.collect.Sets;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.App;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.Inject;
import com.googlecode.androidannotations.annotations.ItemClick;
import com.googlecode.androidannotations.annotations.OptionsItem;
import com.googlecode.androidannotations.annotations.ViewById;
import com.googlecode.androidannotations.annotations.sharedpreferences.Pref;

@EActivity(R.layout.select_candidates_list)
public class SelectCandidatesActivity extends ActionBarActivity {
	
	private static final Splitter SELECTED_CANDIDATE_IDS_SPLITTER = Splitter.on(',').omitEmptyStrings();
	private static final Joiner SELECTED_CANDIDATE_IDS_JOINER = Joiner.on(',');

	public static Set<String> splitCandidateIds(String selectedCandidateIds) {
		return Sets.newHashSet(SELECTED_CANDIDATE_IDS_SPLITTER.split(selectedCandidateIds));
	}
	
	@App
	VoxeApplication application;

	@ViewById
	ListView listView;

	@ViewById
	TextView electionNameTextView;

	@Pref
	ComparisonPref_ comparisonPref;

	@Inject
	Analytics analytics;

	@Inject
	AboutDialogHelper aboutDialogHelper;

	private Election election;

	private SelectCandidatesAdapter adapter;

	private List<SelectedCandidate> candidates;

	private Set<String> selectedCandidateIds;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Optional<ElectionHolder> optionalElectionHolder = application.getElectionHolder();
		if (optionalElectionHolder.isPresent()) {
			ElectionHolder electionHolder = optionalElectionHolder.get();
			election = electionHolder.election;
			String selectedCandidateIdsAsString = comparisonPref.selectedCandidateIds().get();
			selectedCandidateIds = splitCandidateIds(selectedCandidateIdsAsString);
		} else {
			LoadingActivity_.intent(this).flags(FLAG_ACTIVITY_CLEAR_TOP).start();
			finish();
		}
	}

	@AfterViews
	void initLayout() {
		if (isFinishing()) {
			return;
		}

		adapter = new SelectCandidatesAdapter(this);
		listView.setAdapter(adapter);

		candidates = SelectedCandidate.from(election.getMainCandidates());

		adapter.updateCandidates(candidates, selectedCandidateIds);

		electionNameTextView.setText(election.name);

		if (selectedCandidateIds.size() == 2) {
			startSelectTagActivity();
		}
	}

	void startSelectTagActivity() {
		Intent intent = SelectTagActivity_.intent(this).get();
		startActivityForResult(intent, 1);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		selectedCandidateIds.clear();
		saveSelectedCandidateIds();
		adapter.updateCandidates(candidates, selectedCandidateIds);
	}

	@ItemClick
	void listViewItemClicked(SelectedCandidate candidate) {
		candidate.toggleSelected();

		String candidateId = candidate.getCandidate().id;
		if (candidate.isSelected()) {
			selectedCandidateIds.add(candidateId);
		} else {
			selectedCandidateIds.remove(candidateId);
		}

		saveSelectedCandidateIds();

		int position = candidates.indexOf(candidate);
		View candidateView = listView.getChildAt(position - listView.getFirstVisiblePosition());

		adapter.updateCheckbox(candidateView, candidate);

		if (selectedCandidateIds.size() == 2) {
			analytics.twoCandidatesSelected(election, selectedCandidateIds);
			
			startSelectTagActivity();
		}
	}

	private void saveSelectedCandidateIds() {
		String selectedCandidateIdsAsString = SELECTED_CANDIDATE_IDS_JOINER.join(selectedCandidateIds);
		comparisonPref.selectedCandidateIds().put(selectedCandidateIdsAsString);
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
