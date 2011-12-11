package com.joinplato.android.webview;

import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
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
import com.googlecode.androidannotations.annotations.ItemClick;
import com.googlecode.androidannotations.annotations.OptionsItem;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.ViewById;
import com.joinplato.android.R;
import com.joinplato.android.TheVoxeApplication;
import com.joinplato.android.TheVoxeApplication.UpdateElectionListener;
import com.joinplato.android.actionbar.ActionBarActivity;
import com.joinplato.android.model.Candidate;
import com.joinplato.android.model.Election;
import com.joinplato.android.model.ElectionHolder;
import com.joinplato.android.model.Theme;

@EActivity(R.layout.select_candidates)
public class SelectCandidatesActivity extends ActionBarActivity implements UpdateElectionListener {

	private static final String SELECTED_THEME_EXTRA = "selectedTheme";

	public static void start(Context context) {
		Intent intent = new Intent(context, SelectCandidatesActivity_.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		context.startActivity(intent);
	}

	public static void start(Context context, Theme selectedTheme) {
		Intent intent = new Intent(context, SelectCandidatesActivity_.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		intent.putExtra(SELECTED_THEME_EXTRA, selectedTheme);
		context.startActivity(intent);
	}

	@Extra(SELECTED_THEME_EXTRA)
	Theme selectedTheme;

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
		candidates = SelectedCandidate.from(election.candidates);
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
				SelectThemeActivity.start(this, selectedCandidates);
			} else {
				SelectThemeActivity.start(this, selectedCandidates, selectedTheme);
			}
		} else {
			Toast.makeText(this, R.string.select_one_candidate, Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		selectedTheme = (Theme) intent.getSerializableExtra(SELECTED_THEME_EXTRA);
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
			candidates = SelectedCandidate.from(election.candidates);
			adapter.updateCandidates(candidates);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
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
