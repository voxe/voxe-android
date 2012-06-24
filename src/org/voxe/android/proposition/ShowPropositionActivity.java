package org.voxe.android.proposition;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

import org.voxe.android.R;
import org.voxe.android.VoxeApplication;
import org.voxe.android.common.AboutDialogHelper;
import org.voxe.android.common.Analytics;
import org.voxe.android.common.ShareManager;
import org.voxe.android.loading.LoadingActivity_;
import org.voxe.android.model.Election;
import org.voxe.android.model.ElectionHolder;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.google.common.base.Optional;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.App;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.Extra;
import com.googlecode.androidannotations.annotations.OptionsItem;
import com.googlecode.androidannotations.annotations.OptionsMenu;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.ViewById;
import com.googlecode.androidannotations.annotations.res.StringRes;

@EActivity(R.layout.show_proposition)
@OptionsMenu(R.menu.proposition)
public class ShowPropositionActivity extends SherlockActivity {

	public static final String SHOW_PROPOSITION_PATH_FRAGMENT = "/webviews/propositions/";

	private static final String WEBVIEW_URL_FORMAT = "http://voxe.org" + SHOW_PROPOSITION_PATH_FRAGMENT + "%s";

	private static final String SHARE_PROPOSITION_URL = "http://voxe.org/%s/propositions/%s";

	private static final String PROPOSITION_ID_EXTRA = "propositionId";

	private static final String ELECTION_NAMESPACE_EXTRA = "electionNamespace";

	public static void startFromUrl(Context context, String url, String electionNamespace) {

		int fragmentIndex = url.indexOf(SHOW_PROPOSITION_PATH_FRAGMENT);
		if (fragmentIndex != -1) {

			int propositionIdIndex = fragmentIndex + SHOW_PROPOSITION_PATH_FRAGMENT.length();

			String propositionId = url.substring(propositionIdIndex);

			start(context, propositionId, electionNamespace);
		}
	}

	public static void start(Context context, String propositionId, String electionNamespace) {
		Intent intent = new Intent(context, ShowPropositionActivity_.class);
		intent.putExtra(PROPOSITION_ID_EXTRA, propositionId);
		intent.putExtra(ELECTION_NAMESPACE_EXTRA, electionNamespace);
		context.startActivity(intent);
	}

	@App
	VoxeApplication application;

	@ViewById
	TextView electionNameTextView;

	@Bean
	ShareManager shareManager;

	@Extra(PROPOSITION_ID_EXTRA)
	String propositionId;

	@Extra(ELECTION_NAMESPACE_EXTRA)
	String electionNamespace;

	@ViewById
	WebView webview;

	@StringRes
	String shareProposition;

	@Bean
	ShowPropositionWebviewClient webviewClient;

	@StringRes
	String propositionWebviewLoadingMessage;

	private String webviewLoadingData;

	@Bean
	Analytics analytics;

	@Bean
	AboutDialogHelper aboutDialogHelper;

	private String webviewURL;

	private Election election;

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
	void prepareWebview() {
		if (isFinishing()) {
			return;
		}

		WebSettings settings = webview.getSettings();
		settings.setJavaScriptEnabled(true);
		webview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		webview.setWebViewClient(webviewClient);

		webviewURL = String.format(WEBVIEW_URL_FORMAT, propositionId);

		webviewLoadingData = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><html><body>" + propositionWebviewLoadingMessage + "</body></html>";

		electionNameTextView.setText(election.name);

		loadUrl();
	}

	public void loadUrl() {
		webview.loadData(webviewLoadingData, "text/html", "UTF-8");
		startLoading();
	}

	@UiThread(delay = 1)
	void startLoading() {
		// getActionBarHelper().setRefreshActionItemState(true);
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

	@OptionsItem
	void menuShareSelected() {

		String sharingUrl = String.format(SHARE_PROPOSITION_URL, electionNamespace, propositionId);

		String message = String.format(shareProposition, sharingUrl);

		shareManager.share(message);
	}

	@OptionsItem
	public void menuRefreshSelected() {
		loadUrl();
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

	public void loadingDone(String url) {

		if (!url.equals(webviewURL)) {
			webview.loadUrl(webviewURL);
		} else {
			// getActionBarHelper().setRefreshActionItemState(false);
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

}
