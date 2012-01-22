package org.voxe.android.webview;

import java.util.Arrays;
import java.util.List;

import org.voxe.android.R;
import org.voxe.android.TheVoxeApplication;
import org.voxe.android.TheVoxeApplication.UpdateElectionListener;
import org.voxe.android.actionbar.ActionBarActivity;
import org.voxe.android.common.AbstractOnPageChangeListener;
import org.voxe.android.common.Analytics;
import org.voxe.android.common.SimplePagerAdapter;
import org.voxe.android.model.Candidate;
import org.voxe.android.model.Election;
import org.voxe.android.model.ElectionHolder;
import org.voxe.android.model.Tag;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;

import com.google.common.base.Optional;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.App;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.Inject;
import com.googlecode.androidannotations.annotations.OptionsItem;
import com.googlecode.androidannotations.annotations.OptionsMenu;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.UiThreadDelayed;
import com.googlecode.androidannotations.annotations.ViewById;

@EActivity(R.layout.compare_pager)
@OptionsMenu(R.menu.compare)
public class CompareCanditatesActivity extends ActionBarActivity implements UpdateElectionListener, PageController {

	private static final int SELECT_CANDIDATES_PAGE = 0;

	private static final int COMPARISON_PAGE = 1;

	private static final int SELECT_TAG_PAGE = 2;

	@Inject
	Analytics analytics;

	@ViewById
	ViewPager viewPager;

	@ViewById
	View loadingLayout;

	@App
	TheVoxeApplication application;

	ComparisonView comparisonView;

	private SelectCandidatesView selectCandidatesView;

	private SelectTagView selectTagView;

	private Election election;

	@AfterViews
	void initPager() {

		comparisonView = (ComparisonView) View.inflate(this, R.layout.comparison, null);
		selectCandidatesView = (SelectCandidatesView) View.inflate(this, R.layout.select_candidates, null);
		selectTagView = (SelectTagView) View.inflate(this, R.layout.select_tag, null);

		selectCandidatesView.setPageController(this);
		selectTagView.setPageController(this);
		comparisonView.setPageController(this);

		List<View> pagerViews = Arrays.<View> asList(selectCandidatesView, comparisonView, selectTagView);

		PagerAdapter adapter = new SimplePagerAdapter(pagerViews);
		viewPager.setAdapter(adapter);

		viewPager.setOnPageChangeListener(new AbstractOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				if (position == COMPARISON_PAGE) {
					comparisonPageSelected();
				}
			}
		});
	}
	
	@AfterViews
	void showLoading() {
		loadingLayout.setVisibility(View.VISIBLE);
		viewPager.setVisibility(View.GONE);
		loadElectionHolder();
	}

	@Background
	public void loadElectionHolder() {
		Optional<ElectionHolder> electionHolder = application.getElectionHolder();
		if (electionHolder.isPresent()) {
			electionLoaded(electionHolder.get().election);
		}
	}

	@UiThread
	void electionLoaded(Election election) {
		this.election = election;

		loadingLayout.setVisibility(View.GONE);
		viewPager.setVisibility(View.VISIBLE);

		selectCandidatesView.updateCandidates(election.getMainCandidates());
		selectTagView.updateTags(election.tags);

		setTitle(election.name);

		showComparisonPage();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		boolean handled = false;
		if (comparisonViewActive()) {
			handled = comparisonView.handleKeyDown(keyCode);
		}
		if (!handled) {
			return super.onKeyDown(keyCode, event);
		} else {
			return true;
		}
	}

	private boolean comparisonViewActive() {
		return viewPager.getCurrentItem() == COMPARISON_PAGE;
	}

	@OptionsItem
	public void homeSelected() {
		showDialog(R.id.about_dialog);
	}

	@OptionsItem
	void menuShareSelected() {
		comparisonView.shareComparison(election);
	}
	
	@OptionsItem
	public void menuRefreshSelected() {
		comparisonView.reloadComparison();
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
	protected void onPause() {
		super.onPause();
		analytics.onPause();
		application.setUpdateElectionListener(null);
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
			selectCandidatesView.updateCandidates(election.getMainCandidates());
			selectTagView.updateTags(election.tags);
		}
	}

	@Override
	public void onElectionUpdate(Optional<ElectionHolder> electionHolder) {
		if (electionHolder.isPresent()) {
			Election election = electionHolder.get().election;
			updatedElectionIfNeeded(election);
		}
	}

	@UiThreadDelayed(300)
	void comparisonPageSelected() {
		List<Candidate> selectedCandidates = selectCandidatesView.getSelectedCandidates();
		if (selectedCandidates.size() == 0) {
			showSelectedCandidatesPage();
		} else {
			Tag selectedTag = selectTagView.getSelectedTag();
			if (selectedTag == null) {
				showSelectTagPage();
			} else {
				comparisonView.showComparison(selectedCandidates, selectedTag);
			}
		}
	}

	@Override
	public void showComparisonPage() {
		viewPager.setCurrentItem(COMPARISON_PAGE);
	}

	@Override
	public void showSelectedCandidatesPage() {
		viewPager.setCurrentItem(SELECT_CANDIDATES_PAGE);
	}

	@Override
	public void showSelectTagPage() {
		viewPager.setCurrentItem(SELECT_TAG_PAGE);
	}

	@Override
	public void updateSelectedCandidate(List<Candidate> selectedCandidates) {
		comparisonView.updateSelectedCandidate(selectedCandidates);
	}

	@Override
	public void updateSelectedTag(Tag selectedTag) {
		comparisonView.updateSelectedTag(selectedTag);
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		comparisonView.onSaveInstanceState(outState);
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		comparisonView.restoreState(savedInstanceState);
	}

	@Override
	public void startLoading() {
		getActionBarHelper().setRefreshActionItemState(true);		
	}

	@Override
	public void endLoading() {
		getActionBarHelper().setRefreshActionItemState(false);		
	}

	public void showProposition(String url) {
		ShowPropositionActivity.start(this, election.namespace, url);		
	}

}
