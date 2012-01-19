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
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.google.common.base.Optional;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.App;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.Inject;
import com.googlecode.androidannotations.annotations.OptionsItem;
import com.googlecode.androidannotations.annotations.OptionsMenu;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.ViewById;
import com.googlecode.androidannotations.annotations.res.StringRes;

/**
 * TODO show hackedTagName of selected tag in top view
 * 
 * TODO Show Error directly on top of webview instead of a dialog. Only a
 * "reload" button.
 * 
 */
@EActivity(R.layout.compare_pager)
@OptionsMenu(R.menu.compare)
public class CompareCanditatesActivity extends ActionBarActivity implements UpdateElectionListener, PageController {

	private static final int SELECT_CANDIDATES_PAGE = 0;

	private static final int COMPARISON_PAGE = 1;

	private static final int SELECT_TAG_PAGE = 2;

	private static final String DESCRIPTION_DIALOG_ARG = "description";

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

		comparisonView = (ComparisonView) View.inflate(this, R.layout.comparison_view, null);
		selectCandidatesView = (SelectCandidatesView) View.inflate(this, R.layout.select_candidates_view, null);
		selectTagView = (SelectTagView) View.inflate(this, R.layout.select_tag_view, null);

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
		comparisonView.shareComparison();
	}

	public void showLoadingErrorDialog(String description) {
		Bundle bundle = new Bundle();
		bundle.putString(DESCRIPTION_DIALOG_ARG, description);
		showDialog(R.id.webview_error_dialog, bundle);
	}

	@Override
	protected Dialog onCreateDialog(int id, Bundle args) {
		switch (id) {
		case R.id.webview_error_dialog:
			return createWebviewErrorDialog();
		case R.id.about_dialog:
			return createAboutDialog();
		default:
			return null;
		}
	}

	private Dialog createWebviewErrorDialog() {
		return new AlertDialog.Builder(this) //
				.setTitle(R.string.webview_error_dialog) //
				.setMessage("") //
				.setOnCancelListener(new OnCancelListener() {

					@Override
					public void onCancel(DialogInterface dialog) {
						finish();
					}
				}) //
				.setPositiveButton(R.string.retry, new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						comparisonView.reloadComparison();
					}
				}) //
				.setNegativeButton(R.string.cancel, new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				}) //
				.create();
	}

	private Dialog createAboutDialog() {
		return new AlertDialog.Builder(this) //
				.setTitle(R.string.about) //
				.setMessage(R.string.about_content) //
				.setPositiveButton(R.string.about_ok, null) //
				.create();
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog, Bundle bundle) {
		switch (id) {
		case R.id.webview_error_dialog:
			prepareWebviewErrorDialog((AlertDialog) dialog, bundle.getString(DESCRIPTION_DIALOG_ARG));
			break;
		}
	}

	private void prepareWebviewErrorDialog(AlertDialog dialog, String description) {
		dialog.setMessage(String.format(getString(R.string.webview_error_dialog_message), description));
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
			if (this.election != null) {
				Toast.makeText(this, R.string.updated_data, Toast.LENGTH_SHORT).show();
			}
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
	
	@StringRes
	String candidates, appName;

	protected void comparisonPageSelected() {
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

}
