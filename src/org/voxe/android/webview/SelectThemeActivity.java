package org.voxe.android.webview;

import java.io.Serializable;
import java.util.List;

import org.voxe.android.R;
import org.voxe.android.TheVoxeApplication;
import org.voxe.android.TheVoxeApplication.UpdateElectionListener;
import org.voxe.android.actionbar.ActionBarActivity;
import org.voxe.android.common.UIUtils;
import org.voxe.android.model.Candidate;
import org.voxe.android.model.Election;
import org.voxe.android.model.ElectionHolder;
import org.voxe.android.model.Theme;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.google.common.base.Optional;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.App;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.Extra;
import com.googlecode.androidannotations.annotations.ItemClick;
import com.googlecode.androidannotations.annotations.OptionsItem;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.ViewById;

@EActivity(R.layout.select_theme)
public class SelectThemeActivity extends ActionBarActivity implements UpdateElectionListener {

	private static final String SELECTED_CANDIDATES_EXTRA = "selectedCandidates";

	private static final String SELECTED_THEME_EXTRA = "selectedTheme";

	public static void start(Context context, List<Candidate> selectedCandidates) {
		Intent intent = new Intent(context, SelectThemeActivity_.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		intent.putExtra(SELECTED_CANDIDATES_EXTRA, (Serializable) selectedCandidates);
		context.startActivity(intent);
	}

	public static void start(Context context, List<Candidate> selectedCandidates, Theme selectedTheme) {
		Intent intent = new Intent(context, SelectThemeActivity_.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		intent.putExtra(SELECTED_CANDIDATES_EXTRA, (Serializable) selectedCandidates);
		intent.putExtra(SELECTED_THEME_EXTRA, selectedTheme);
		context.startActivity(intent);
	}

	@App
	TheVoxeApplication application;

	@Extra(SELECTED_CANDIDATES_EXTRA)
	List<Candidate> selectedCandidates;

	@Extra(SELECTED_THEME_EXTRA)
	Theme selectedTheme;

	@ViewById
	ListView list;
	
	@ViewById
	View loadingLayout;

	private Election election;

	private ThemeAdapter themeAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (selectedTheme != null) {
			CompareCanditatesActivity.start(this, selectedCandidates, selectedTheme);
		} else {
			loadElectionHolder();
		}
	}
	
	@AfterViews
	void showLoading() {
		loadingLayout.setVisibility(View.VISIBLE);
		list.setVisibility(View.GONE);
	}

	@Background
	public void loadElectionHolder() {
		Optional<ElectionHolder> electionHolder = application.getElectionHolder();
		if (electionHolder.isPresent()) {
			fillList(electionHolder.get().election);
		}
	}

	@UiThread
	void fillList(Election election) {
		this.election = election;
		themeAdapter = new ThemeAdapter(this, election.themes);
		list.setAdapter(themeAdapter);
		loadingLayout.setVisibility(View.GONE);
		list.setVisibility(View.VISIBLE);
	}

	@ItemClick
	void listItemClicked(Theme selectedTheme) {
		CompareCanditatesActivity.start(this, selectedCandidates, selectedTheme);
	}

	@OptionsItem
	public void homeSelected() {
		SelectCandidatesActivity.start(this);
		if (!UIUtils.isHoneycomb()) {
			overridePendingTransition(R.anim.home_enter, R.anim.home_exit);
		}
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
			themeAdapter.updateThemes(election.themes);
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
