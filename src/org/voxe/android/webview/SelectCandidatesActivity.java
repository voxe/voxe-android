package org.voxe.android.webview;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;

import java.util.List;

import org.voxe.android.R;
import org.voxe.android.TheVoxeApplication;
import org.voxe.android.TheVoxeApplication.UpdateElectionListener;
import org.voxe.android.actionbar.ActionBarActivity;
import org.voxe.android.common.Analytics;
import org.voxe.android.model.Candidate;
import org.voxe.android.model.Election;
import org.voxe.android.model.ElectionHolder;
import org.voxe.android.model.Tag;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;
import android.widget.Toast;

import com.google.common.base.Optional;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.App;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.Extra;
import com.googlecode.androidannotations.annotations.Inject;
import com.googlecode.androidannotations.annotations.ItemClick;
import com.googlecode.androidannotations.annotations.OptionsItem;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.ViewById;

@EActivity(R.layout.select_candidates)
public class SelectCandidatesActivity extends ActionBarActivity implements UpdateElectionListener {

	private static final String SELECTED_THEME_EXTRA = "selectedTheme";

	@Extra(SELECTED_THEME_EXTRA)
	Tag selectedTheme;

	@App
	TheVoxeApplication application;

	private List<SelectedCandidate> candidates;

	private SelectCandidatesAdapter adapter;

	@ViewById
	GridView gridview;

	@ViewById
	View loadingLayout;

	@ViewById
	View selectLayout;
	
	@Inject
	Analytics analytics;

	Election election;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(R.string.candidates);
		loadElectionHolder();
	}

	@AfterViews
	void showLoading() {
		loadingLayout.setVisibility(View.VISIBLE);
		selectLayout.setVisibility(View.GONE);
	}

	@Background
	public void loadElectionHolder() {
		Optional<ElectionHolder> electionHolder = application.getElectionHolder();
		if (electionHolder.isPresent()) {
			fillCandidateGrid(electionHolder.get().election);
		}
	}

	@UiThread
	void fillCandidateGrid(Election election) {
		this.election = election;
		candidates = SelectedCandidate.from(election.getMainCandidates());
		adapter = new SelectCandidatesAdapter(this, candidates);
		gridview.setAdapter(adapter);
		loadingLayout.setVisibility(View.GONE);
		selectLayout.setVisibility(View.VISIBLE);
	}

	@ItemClick
	void gridviewItemClicked(SelectedCandidate candidate) {
		candidate.toggleSelected();

		int position = candidates.indexOf(candidate);
		View candidateView = gridview.getChildAt(position - gridview.getFirstVisiblePosition());

		adapter.updateCheckbox(candidateView, candidate);
	}

	@Click
	public void compareButtonClicked() {
		List<Candidate> selectedCandidates = SelectedCandidate.filterSelected(candidates);
		if (selectedCandidates.size() > 0) {
			if (selectedTheme == null) {
				SelectTagActivity_ //
						.intent(this) //
						.flags(FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_SINGLE_TOP) //
						.selectedCandidates(selectedCandidates) //
						.start();
			} else {
				SelectTagActivity_ //
						.intent(this) //
						.flags(FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_SINGLE_TOP) //
						.selectedCandidates(selectedCandidates) //
						.selectedTag(selectedTheme) //
						.start();
			}
		} else {
			Toast.makeText(this, R.string.select_one_candidate, Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		selectedTheme = (Tag) intent.getSerializableExtra(SELECTED_THEME_EXTRA);
	}

	@OptionsItem
	public void homeSelected() {
		showDialog(R.id.about_dialog);
	}

	@Override
	protected Dialog onCreateDialog(int id, Bundle args) {
		switch (id) {
		case R.id.about_dialog:
			return createAboutDialog();
		default:
			return null;
		}
	}

	private Dialog createAboutDialog() {
		return new AlertDialog.Builder(this) //
				.setTitle(R.string.about) //
				.setMessage(R.string.about_content) //
				.setPositiveButton(R.string.about_ok, null) //
				.create();
	}

	@Override
	protected void onResume() {
		super.onResume();
		analytics.onResume();
		application.setUpdateElectionListener(this);
		checkElectionChangedInBackground();
	}

	@Background
	void checkElectionChangedInBackground() {
		Optional<ElectionHolder> electionHolder = application.getElectionHolder();
		if (electionHolder.isPresent()) {
			updatedElectionIfNeeded(electionHolder.get().election);
		}
	}

	@UiThread
	void updatedElectionIfNeeded(Election election) {
		if (this.election != election) {
			this.election = election;
			candidates = SelectedCandidate.from(election.getMainCandidates());
			adapter.updateCandidates(candidates);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		analytics.onPause();
		application.setUpdateElectionListener(null);
	}

	@Override
	public void onElectionUpdate(Optional<ElectionHolder> electionHolder) {
		if (electionHolder.isPresent()) {
			Election election = electionHolder.get().election;
			updatedElectionIfNeeded(election);
		}
	}
	
}
