package org.voxe.android.activity;

import static android.content.Intent.ACTION_SEND;
import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static com.google.common.collect.Iterables.transform;

import java.util.HashSet;
import java.util.List;

import org.voxe.android.R;
import org.voxe.android.VoxeApplication;
import org.voxe.android.common.Analytics;
import org.voxe.android.common.ComparisonWebviewClient;
import org.voxe.android.model.Candidate;
import org.voxe.android.model.Election;
import org.voxe.android.model.ElectionsHolder;
import org.voxe.android.model.Tag;
import org.voxe.android.view.ComparisonBar;
import org.voxe.android.view.ComparisonBar_;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.actionbarsherlock.widget.ShareActionProvider;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.App;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.Extra;
import com.googlecode.androidannotations.annotations.OptionsItem;
import com.googlecode.androidannotations.annotations.OptionsMenu;
import com.googlecode.androidannotations.annotations.ViewById;
import com.googlecode.androidannotations.annotations.res.StringRes;

@EActivity(R.layout.comparison)
@OptionsMenu(R.menu.compare)
public class ComparisonActivity extends SherlockActivity {

	private static final Joiner CANDIDACY_JOINER = Joiner.on(',');

	private static final String WEBVIEW_URL_FORMAT = "http://voxe.org/webviews/comparisons?electionId=%s&candidacyIds=%s&tagId=%s";

	@App
	VoxeApplication application;

	@Bean
	Analytics analytics;

	@ViewById
	WebView webview;

	@Bean
	ComparisonWebviewClient webviewClient;

	@StringRes
	String shareCompare;

	@StringRes
	String comparisonWebviewLoadingMessage;

	@ViewById
	ImageView candidate1ImageView;

	@ViewById
	ImageView candidate2ImageView;

	@ViewById
	TextView loading;

	private Election election;

	private List<Candidate> selectedCandidates;

	private Tag selectedTag;

	@Extra
	HashSet<String> selectedCandidateIds;

	@Extra
	int electionIndex;

	@Extra
	String selectedTagId;

	private String webviewURL;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
	}

	@AfterViews
	void initLayout() {
		Optional<ElectionsHolder> optionalElectionHolder = application.getElectionHolder();
		if (optionalElectionHolder.isPresent()) {
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			getSupportActionBar().setHomeButtonEnabled(true);
			ElectionsHolder electionHolder = optionalElectionHolder.get();
			election = electionHolder.elections.get(electionIndex);

			setTitle(election.name);

			WebSettings settings = webview.getSettings();
			// change to true when bug fixed
			settings.setJavaScriptEnabled(false);
			webview.setWebViewClient(webviewClient);
			webview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

			selectedCandidates = election.selectedCandidatesByCandidateIds(selectedCandidateIds);

			selectedTag = election.tagFromId(selectedTagId);

			Iterable<String> candidacyIds = transform(selectedCandidates, new Function<Candidate, String>() {
				@Override
				public String apply(Candidate input) {
					return input.candidacyId;
				}
			});

			String candidacyIdsJoined = CANDIDACY_JOINER.join(candidacyIds);

			webviewURL = String.format(WEBVIEW_URL_FORMAT, election.id, candidacyIdsJoined, selectedTag.id);

			String loadingMessageFormat = getString(R.string.comparison_webview_loading_message);
			String loadingMessageString = String.format(loadingMessageFormat, joinCandidatesNames(), selectedTag.name);
			CharSequence loadingMessageHtml = Html.fromHtml(loadingMessageString);

			loading.setText(loadingMessageHtml);

			webview.loadUrl(webviewURL);

			ComparisonBar customTitle = ComparisonBar_.build(this);

			customTitle.init(election, selectedTag, selectedCandidateIds);

			ActionBar actionBar = getSupportActionBar();
			actionBar.setDisplayShowTitleEnabled(false);
			actionBar.setCustomView(customTitle);
			actionBar.setDisplayShowCustomEnabled(true);

		} else {
			LoadingActivity_.intent(this).flags(FLAG_ACTIVITY_CLEAR_TOP).start();
			finish();
		}

	}

	private String joinCandidatesNames() {
		List<String> candidateNames = Lists.newArrayList(transform(selectedCandidates, new Function<Candidate, String>() {
			@Override
			public String apply(Candidate input) {
				return input.getName().toString();
			}
		}));
		String candidateNamesJoined;
		if (candidateNames.size() > 1) {

			String lastCandidateName = candidateNames.remove(candidateNames.size() - 1);

			candidateNamesJoined = Joiner.on(", ").join(candidateNames) + " et " + lastCandidateName;
		} else {
			candidateNamesJoined = candidateNames.get(0);
		}
		return candidateNamesJoined;
	}

	@OptionsItem
	void homeSelected() {
		SelectElectionActivity_ //
				.intent(this) //
				.flags(FLAG_ACTIVITY_CLEAR_TOP) //
				.start();
		finish();
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		/*
		 * Sharing the comparison
		 */

		Iterable<String> candidacyNamespaces = transform(selectedCandidates, new Function<Candidate, String>() {
			@Override
			public String apply(Candidate input) {
				return input.candidacyNamespace;
			}
		});
		String candidacyNamespacesJoined = CANDIDACY_JOINER.join(candidacyNamespaces);

		String url = String.format("http://voxe.org/%s/%s/%s", election.namespace, candidacyNamespacesJoined, selectedTag.namespace);

		String candidateNamesJoined = joinCandidatesNames();
		String message = String.format(shareCompare, candidateNamesJoined, selectedTag.getName(), url);

		MenuItem actionItem = menu.findItem(R.id.menu_share);
		ShareActionProvider actionProvider = (ShareActionProvider) actionItem.getActionProvider();
		actionProvider.setShareHistoryFileName(ShareActionProvider.DEFAULT_SHARE_HISTORY_FILE_NAME);

		Intent shareIntent = new Intent(ACTION_SEND);
		shareIntent.setType("text/plain");
		shareIntent.putExtra(Intent.EXTRA_TEXT, message);

		actionProvider.setShareIntent(shareIntent);

		return super.onCreateOptionsMenu(menu);
	}

	public void startLoading() {
		loading.setVisibility(View.VISIBLE);
		setSupportProgressBarIndeterminateVisibility(true);
	}

	public void endLoading() {
		loading.setVisibility(View.GONE);
		setSupportProgressBarIndeterminateVisibility(false);
	}

	public void loadingError(String description) {

		String message = String.format(getString(R.string.webview_error_message), description);

		Intent intent = LoadingErrorActivity_ //
				.intent(this) //
				.description(message) //
				.get();

		startActivityForResult(intent, R.id.loading_error_request);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == R.id.loading_error_request) {
			if (resultCode == RESULT_OK) {
				webview.reload();
			} else {
				setResult(RESULT_CANCELED);
				finish();
			}
		}
	}

	public void selectCandidates() {
		analytics.backToCandidatesFromComparison(election);
		setResult(SelectTagActivity.BACK_TO_SELECT_CANDIDATES);
		finish();
	}

	public void selectTag() {
		analytics.backToTagFromComparison(election);
		setResult(Activity.RESULT_CANCELED);
		finish();
	}

	@Override
	public void onBackPressed() {
		if (webview.canGoBack()) {
			webview.goBack();
		} else {
			super.onBackPressed();
		}
	}

}
