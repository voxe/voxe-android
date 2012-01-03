package org.voxe.android.webview;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;

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

	@App
	TheVoxeApplication application;

	@Extra("selectedCandidates")
	List<Candidate> selectedCandidates;

	@Extra("selectedTheme")
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
			CompareCanditatesActivity_ //
					.intent(this) //
					.selectedCandidates(selectedCandidates) //
					.selectedTheme(selectedTheme) //
					.start();
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
		CompareCanditatesActivity_ //
				.intent(this) //
				.selectedCandidates(selectedCandidates) //
				.selectedTheme(selectedTheme) //
				.start();
	}

	@OptionsItem
	public void homeSelected() {
		SelectCandidatesActivity_.intent(this).flags(FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_SINGLE_TOP).start();
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
