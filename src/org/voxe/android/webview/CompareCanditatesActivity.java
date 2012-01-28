package org.voxe.android.webview;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.voxe.android.R;
import org.voxe.android.VoxeApplication;
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
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.google.common.base.Optional;
import com.google.common.base.Splitter;
import com.google.common.collect.Sets;
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
import com.googlecode.androidannotations.annotations.res.HtmlRes;

@EActivity(R.layout.compare_pager)
@OptionsMenu(R.menu.compare)
public class CompareCanditatesActivity extends ActionBarActivity implements PageController {

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
	VoxeApplication application;

	@HtmlRes
	Spanned aboutContent;

	@Inject
	ShareManager shareManager;

	ComparisonView comparisonView;

	private SelectCandidatesView selectCandidatesView;

	private SelectTagView selectTagView;

	private Election election;

	private int savedCurrentItem;

	private boolean newIntent;

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

		List<Candidate> mainCandidates = election.getMainCandidates();
		selectCandidatesView.updateCandidates(mainCandidates);
		selectTagView.updateTags(election.tags);

		setTitle(election.name);

		viewPager.setCurrentItem(savedCurrentItem);

		handleUriIntent();
	}

	private void handleUriIntent() {
		List<Candidate> mainCandidates = election.getMainCandidates();
		Intent intent = getIntent();
		if (intent.getAction() != null && intent.getAction().equals(Intent.ACTION_VIEW)) {
			Uri data = intent.getData();
			List<String> pathSegments = data.getPathSegments();
			boolean handled = false;
			if (pathSegments.size() == 3) {
				String electionNamespace = pathSegments.get(0).toLowerCase();
				if (electionNamespace.equals(election.namespace)) {
					String candidacyNamespaces = pathSegments.get(1).toLowerCase();
					if (candidacyNamespaces.equals("propositions")) {
						String propositionId = pathSegments.get(2).toLowerCase();
						ShowPropositionActivity.start(this, propositionId, election.namespace);
						handled = true;
					} else {

						Map<String, String> candidateIdByCandidacyNamespace = new HashMap<String, String>();
						for (Candidate candidate : mainCandidates) {
							candidateIdByCandidacyNamespace.put(candidate.candidacyNamespace, candidate.id);
						}

						Iterable<String> candidacyNamespacesSplitted = Splitter.on(',').split(candidacyNamespaces);
						Set<String> selectedCandidateIds = Sets.newHashSet();
						for (String candidacyNamespace : candidacyNamespacesSplitted) {
							if (candidateIdByCandidacyNamespace.containsKey(candidacyNamespace)) {
								selectedCandidateIds.add(candidateIdByCandidacyNamespace.get(candidacyNamespace));
							}
						}

						if (selectedCandidateIds.size() > 0) {
							String tagNamespace = pathSegments.get(2).toLowerCase();

							Tag selectedTag = null;
							for (Tag tag : election.tags) {
								if (tag.namespace.equals(tagNamespace)) {
									selectedTag = tag;
									break;
								}
							}

							if (selectedTag != null) {
								selectCandidatesView.updateSelectedCandidates(selectedCandidateIds);
								selectTagView.updateSelectedTag(selectedTag);

								showComparisonPage();
								handled = true;
							}
						}

					}
				}
			}

			if (!handled) {
				Uri updatedUri = data.buildUpon().authority("www.voxe.org").build();
				startActivity(new Intent(Intent.ACTION_VIEW, updatedUri));
			}
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		setIntent(intent);
		newIntent = true;
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
		TextView textView = (TextView) View.inflate(this, R.layout.about_dialog_text, null);
		textView.setText(aboutContent);
		textView.setMovementMethod(LinkMovementMethod.getInstance());
		textView.setLinkTextColor(Color.RED);
		return new AlertDialog.Builder(this) //
				.setTitle(R.string.about) //
				.setView(textView) //
				.setPositiveButton(R.string.rate_android_market, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=org.voxe.android")));
					}
				}) //
				.setNeutralButton(R.string.share_voxe, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						shareManager.share(getString(R.string.share_voxe_content));
					}
				}) //
				.create();
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

		if (newIntent) {
			newIntent = false;
			handleUriIntent();
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
		int currentItem = viewPager.getCurrentItem();
		outState.putInt("currentPagerItem", currentItem);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		comparisonView.restoreState(savedInstanceState);
		savedCurrentItem = savedInstanceState.getInt("currentPagerItem", COMPARISON_PAGE);
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
		ShowPropositionActivity.startFromUrl(this, url, election.namespace);
	}

}
