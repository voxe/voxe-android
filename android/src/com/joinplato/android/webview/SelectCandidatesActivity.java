package com.joinplato.android.webview;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;
import android.widget.Toast;

import com.google.common.base.Optional;
import com.googlecode.androidannotations.annotations.App;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.Extra;
import com.googlecode.androidannotations.annotations.ItemClick;
import com.googlecode.androidannotations.annotations.OptionsItem;
import com.googlecode.androidannotations.annotations.OptionsMenu;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.ViewById;
import com.joinplato.android.R;
import com.joinplato.android.TheVoxeApplication;
import com.joinplato.android.actionbar.ActionBarActivity;
import com.joinplato.android.model.Candidate;
import com.joinplato.android.model.Election;
import com.joinplato.android.model.ElectionHolder;
import com.joinplato.android.model.Theme;

@EActivity(R.layout.select_candidates)
@OptionsMenu(R.menu.select_candidates)
public class SelectCandidatesActivity extends ActionBarActivity {

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		loadElectionHolder();
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
		candidates = SelectedCandidate.from(election.candidates);
		adapter = new SelectCandidatesAdapter(this, candidates);
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
	public void menuOkSelected() {
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

}
