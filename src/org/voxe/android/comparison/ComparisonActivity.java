package org.voxe.android.comparison;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static com.google.common.collect.Iterables.transform;

import java.util.List;
import java.util.Set;

import org.voxe.android.R;
import org.voxe.android.VoxeApplication;
import org.voxe.android.actionbar.ActionBarActivity;
import org.voxe.android.candidates.SelectCandidatesActivity;
import org.voxe.android.common.AboutDialogHelper;
import org.voxe.android.common.Analytics;
import org.voxe.android.common.ComparisonPref_;
import org.voxe.android.common.LogHelper;
import org.voxe.android.common.ShareManager;
import org.voxe.android.loading.LoadingActivity_;
import org.voxe.android.model.Candidate;
import org.voxe.android.model.Election;
import org.voxe.android.model.ElectionHolder;
import org.voxe.android.model.Tag;
import org.voxe.android.proposition.ShowPropositionActivity;
import org.voxe.android.tag.SelectTagActivity;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.App;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.Inject;
import com.googlecode.androidannotations.annotations.OptionsItem;
import com.googlecode.androidannotations.annotations.OptionsMenu;
import com.googlecode.androidannotations.annotations.UiThreadDelayed;
import com.googlecode.androidannotations.annotations.ViewById;
import com.googlecode.androidannotations.annotations.res.StringRes;
import com.googlecode.androidannotations.annotations.sharedpreferences.Pref;

@EActivity(R.layout.comparison)
@OptionsMenu(R.menu.compare)
public class ComparisonActivity extends ActionBarActivity {

	private static final Joiner CANDIDACY_JOINER = Joiner.on(',');

	private static final String WEBVIEW_URL_FORMAT = "http://voxe.org/webviews/comparisons?electionId=%s&candidacyIds=%s&tagId=%s";

	@Pref
	ComparisonPref_ comparisonPref;

	@Inject
	AboutDialogHelper aboutDialogHelper;

	@App
	VoxeApplication application;

	@Inject
	Analytics analytics;

	@ViewById
	WebView webview;

	@ViewById
	TextView selectedTagName;

	@ViewById
	ImageView selectedTagIcon;

	@Inject
	ComparisonWebviewClient webviewClient;

	@StringRes
	String shareCompare;

	@Inject
	ShareManager shareManager;

	@StringRes
	String comparisonWebviewLoadingMessage;

	@ViewById
	ImageView candidate1ImageView;

	@ViewById
	ImageView candidate2ImageView;

	private Election election;

	private String currentLoadedUrl;

	boolean loading = false;

	private String webviewLoadingData;

	private List<Candidate> selectedCandidates;

	private Tag selectedTag;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Optional<ElectionHolder> optionalElectionHolder = application.getElectionHolder();
		if (optionalElectionHolder.isPresent()) {
			ElectionHolder electionHolder = optionalElectionHolder.get();
			election = electionHolder.election;

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

		setTitle(election.name);

		WebSettings settings = webview.getSettings();
		// change to true when bug fixed
		settings.setJavaScriptEnabled(false);
		webview.setWebViewClient(webviewClient);
		webview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		webviewLoadingData = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><html><body>" + comparisonWebviewLoadingMessage + "</body></html>";

		String selectedCandidateIdsAsString = comparisonPref.selectedCandidateIds().get();
		String selectedTagId = comparisonPref.selectedTagId().get();

		Set<String> selectedCandidateIds = SelectCandidatesActivity.splitCandidateIds(selectedCandidateIdsAsString);

		selectedCandidates = election.selectedCandidatesByCandidateIds(selectedCandidateIds);

		selectedTag = election.tagFromId(selectedTagId);

		Candidate candidate1 = selectedCandidates.get(0);
		candidate1.insertPhoto(candidate1ImageView);

		Candidate candidate2 = selectedCandidates.get(1);
		candidate2.insertPhoto(candidate2ImageView);

		selectedTagName.setText(selectedTag.getName());
		selectedTagIcon.setImageBitmap(selectedTag.icon.bitmap);

		Iterable<String> candidacyIds = transform(selectedCandidates, new Function<Candidate, String>() {
			@Override
			public String apply(Candidate input) {
				return input.candidacyId;
			}
		});

		String candidacyIdsJoined = CANDIDACY_JOINER.join(candidacyIds);

		String electionId = application.getElectionId();

		String webviewURL = String.format(WEBVIEW_URL_FORMAT, electionId, candidacyIdsJoined, selectedTag.id);

		LogHelper.log("Loading url " + webviewURL);

		currentLoadedUrl = webviewURL;
		loadUrl();

	}

	private void loadUrl() {
		String candidateNamesJoined = joinCandidatesNames();
		webview.loadData(String.format(webviewLoadingData, candidateNamesJoined, selectedTag.getName()), "text/html", "UTF-8");

		loading = true;
		showLoading();
	}

	@UiThreadDelayed(0)
	void showLoading() {
		getActionBarHelper().setRefreshActionItemState(true);
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

	@OptionsItem
	void menuShareSelected() {

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
		shareManager.share(message);
	}

	@OptionsItem
	public void menuRefreshSelected() {
		if (!loading && currentLoadedUrl != null) {
			loadUrl();
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		webview.saveState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		webview.restoreState(savedInstanceState);
	}

	public void endLoading(String url) {
		if (url.equals(currentLoadedUrl)) {
			loading = false;
			getActionBarHelper().setRefreshActionItemState(false);
		} else {
			webview.loadUrl(currentLoadedUrl);
		}
	}

	public void showProposition(String url) {
		ShowPropositionActivity.startFromUrl(this, url, election.namespace);
	}

	@Click
	void selectCandidatesButtonClicked() {
		analytics.backToCandidatesFromComparison(election);
		setResult(SelectTagActivity.BACK_TO_SELECT_CANDIDATES);
		finish();
	}

	@Click
	void selectTagButtonClicked() {
		analytics.backToTagFromComparison(election);
		setResult(SelectTagActivity.RESULT_CANCELED);
		finish();
	}

}
