package org.voxe.android.webview;

import static android.content.Intent.ACTION_SEND;
import static android.content.Intent.EXTRA_TEXT;
import static com.google.common.collect.Iterables.transform;

import java.util.List;

import org.voxe.android.R;
import org.voxe.android.TheVoxeApplication;
import org.voxe.android.common.LogHelper;
import org.voxe.android.model.Candidate;
import org.voxe.android.model.Tag;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.App;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EViewGroup;
import com.googlecode.androidannotations.annotations.Inject;
import com.googlecode.androidannotations.annotations.ViewById;
import com.googlecode.androidannotations.annotations.res.StringRes;

@EViewGroup(R.layout.comparison_content)
public class ComparisonView extends RelativeLayout {

	private static final String WEBVIEW_URL_FORMAT = "http://voxe.org/webviews/comparisons?electionId=%s&candidacyIds=%s&tagId=%s";

	@ViewById
	WebView webview;

	@ViewById
	TextView selectedCandidatesNumber, selectedTagName;

	@ViewById
	ImageView selectedTagIcon;

	@Inject
	CompareCandidateWebviewClient webviewClient;

	@StringRes
	String shareWith;

	@StringRes
	String shareCompare;

	@ViewById
	View reloadButtonImage;

	@ViewById
	View reloadingProgress;

	@App
	TheVoxeApplication application;

	private PageController pageController;

	private List<Candidate> selectedCandidates;

	private Tag selectedTag;

	private String currentLoadedUrl;

	boolean loading = false;

	public ComparisonView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@AfterViews
	void prepareWebview() {
		webviewClient.bind(this);
		WebSettings settings = webview.getSettings();
		// change to true when bug fixed
		settings.setJavaScriptEnabled(false);
		webview.setWebViewClient(webviewClient);
		webview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
	}

	/**
	 * Call this to show a comparison in the webviews
	 */
	public void showComparison(List<Candidate> selectedCandidates, Tag selectedTag) {

		if (notNewComparison(selectedCandidates, selectedTag)) {
			LogHelper.log("Comparison already loaded");
			return;
		}

		this.selectedCandidates = selectedCandidates;
		this.selectedTag = selectedTag;
		Iterable<String> candidacyIds = transform(selectedCandidates, new Function<Candidate, String>() {
			@Override
			public String apply(Candidate input) {
				return input.candidacyId;
			}
		});

		String candidacyIdsJoined = Joiner.on(',').join(candidacyIds);
		String electionId = application.getElectionId();
		String tagId = selectedTag.id;

		String webviewURL = String.format(WEBVIEW_URL_FORMAT, electionId, candidacyIdsJoined, tagId);

		LogHelper.log("Loading url " + webviewURL);

		currentLoadedUrl = webviewURL;
		loadUrl();

	}

	private void loadUrl() {
		webview.loadUrl(currentLoadedUrl);
		startLoading();
	}

	private boolean notNewComparison(List<Candidate> selectedCandidates, Tag selectedTag) {

		final boolean NOT_NEW = true;
		final boolean NEW = false;

		if (this.selectedTag == null) {
			return NEW;
		}

		if (this.selectedCandidates == null) {
			return NEW;
		}

		if (!selectedTag.id.equals(this.selectedTag.id)) {
			return NEW;
		}

		if (selectedCandidates.size() != this.selectedCandidates.size()) {
			return NEW;
		}

		for (int i = 0; i < selectedCandidates.size(); i++) {
			Candidate thisCandidate = this.selectedCandidates.get(i);
			Candidate newCandidate = selectedCandidates.get(i);
			if (!thisCandidate.id.equals(newCandidate.id)) {
				return NEW;
			}
		}

		return NOT_NEW;
	}

	public void shareComparison() {
		if (selectedCandidates != null && selectedTag != null) {
			Intent sharingIntent = new Intent(ACTION_SEND);
			sharingIntent.setType("text/plain");

			Iterable<String> candidateNames = transform(selectedCandidates, new Function<Candidate, String>() {
				@Override
				public String apply(Candidate input) {
					return input.getName().toString();
				}
			});

			String candidateNamesJoined = Joiner.on(',').join(candidateNames);
			String message = String.format(shareCompare, candidateNamesJoined, selectedTag.getHackedTagName());
			sharingIntent.putExtra(EXTRA_TEXT, message);
			getContext().startActivity(Intent.createChooser(sharingIntent, shareWith));
		}
	}

	/**
	 * Key downs should be delegated here when this view is shown
	 */
	public boolean handleKeyDown(int keyCode) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && webview.canGoBack()) {
			webview.goBack();
			return true;
		} else {
			return false;
		}
	}

	public void endLoading(String url) {
		if (url.equals(currentLoadedUrl)) {
			loading = false;
			reloadButtonImage.setVisibility(VISIBLE);
			reloadingProgress.setVisibility(GONE);
		}
	}

	public void startLoading() {
		loading = true;
		reloadButtonImage.setVisibility(GONE);
		reloadingProgress.setVisibility(VISIBLE);
	}

	@Click
	void reloadComparisonClicked() {
		if (!loading && currentLoadedUrl != null) {
			loadUrl();
		}
	}

	@Click
	void selectCandidatesButtonClicked() {
		pageController.showSelectedCandidatesPage();
	}

	@Click
	void selectTagButtonClicked() {
		pageController.showSelectTagPage();
	}

	public void setPageController(PageController pageController) {
		this.pageController = pageController;
	}

	public void updateSelectedCandidate(List<Candidate> selectedCandidates) {
		selectedCandidatesNumber.setText("" + selectedCandidates.size());
		if (selectedCandidates.size() == 0) {
			selectedCandidatesNumber.setTextColor(Color.RED);
		} else {
			selectedCandidatesNumber.setTextColor(Color.BLACK);
		}
	}

	public void updateSelectedTag(Tag selectedTag) {
		selectedTagName.setText(selectedTag.getHackedTagName());
		selectedTagIcon.setImageBitmap(selectedTag.icon.bitmap);
	}

	public void onSaveInstanceState(Bundle outState) {
		webview.saveState(outState);
	}

	public void restoreState(Bundle savedInstanceState) {
		webview.restoreState(savedInstanceState);
	}

}
