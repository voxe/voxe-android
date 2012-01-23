package org.voxe.android.webview;

import static android.content.Intent.ACTION_SEND;
import static android.content.Intent.EXTRA_TEXT;

import org.voxe.android.R;
import org.voxe.android.TheVoxeApplication;
import org.voxe.android.actionbar.ActionBarActivity;
import org.voxe.android.common.Analytics;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.App;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.Extra;
import com.googlecode.androidannotations.annotations.Inject;
import com.googlecode.androidannotations.annotations.OptionsItem;
import com.googlecode.androidannotations.annotations.OptionsMenu;
import com.googlecode.androidannotations.annotations.UiThreadDelayed;
import com.googlecode.androidannotations.annotations.ViewById;
import com.googlecode.androidannotations.annotations.res.StringRes;

@EActivity(R.layout.show_proposition)
@OptionsMenu(R.menu.proposition)
public class ShowPropositionActivity extends ActionBarActivity {

	public static final String SHOW_PROPOSITION_PATH_FRAGMENT = "/webviews/propositions/";

	private static final String WEBVIEW_URL_FORMAT = "http://voxe.org" + SHOW_PROPOSITION_PATH_FRAGMENT + "%s";
	
	private static final String SHARE_PROPOSITION_URL = "http://voxe.org/%s/propositions/%s";

	private static final String PROPOSITION_ID_EXTRA = "propositionId";
	
	private static final String ELECTION_NAMESPACE_EXTRA = "electionNamespace";

	public static void start(Context context, String url, String electionNamespace) {

		int fragmentIndex = url.indexOf(SHOW_PROPOSITION_PATH_FRAGMENT);
		if (fragmentIndex != -1) {

			int propositionIdIndex = fragmentIndex + SHOW_PROPOSITION_PATH_FRAGMENT.length();

			String propositionId = url.substring(propositionIdIndex);

			// TODO Capptain log

			Intent intent = new Intent(context, ShowPropositionActivity_.class);
			intent.putExtra(PROPOSITION_ID_EXTRA, propositionId);
			intent.putExtra(ELECTION_NAMESPACE_EXTRA, electionNamespace);
			context.startActivity(intent);
		}

	}

	@App
	TheVoxeApplication application;

	@ViewById
	View loadingLayout;

	@Extra(PROPOSITION_ID_EXTRA)
	String propositionId;
	
	@Extra(ELECTION_NAMESPACE_EXTRA)
	String electionNamespace;

	@ViewById
	WebView webview;
	
	@StringRes
	String shareProposition;
	
	@StringRes
	String shareWith;

	@Inject
	ShowPropositionWebviewClient webviewClient;
	
	@StringRes
	String propositionWebviewLoadingMessage;
	
	private String webviewLoadingData;

	@Inject
	Analytics analytics;

	private String webviewURL;

	@AfterViews
	void prepareWebview() {
		WebSettings settings = webview.getSettings();
		settings.setJavaScriptEnabled(true);
		webview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		webview.setWebViewClient(webviewClient);

		webviewURL = String.format(WEBVIEW_URL_FORMAT, propositionId);
		
		webviewLoadingData = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><html><body>" + propositionWebviewLoadingMessage + "</body></html>";

		loadUrl();
	}
	
	
	public void loadUrl() {
		webview.loadData(webviewLoadingData, "text/html", "UTF-8");
		loadUrlDelayed();
		startLoading();
	}
	
	@UiThreadDelayed(0)
	void startLoading() {
		getActionBarHelper().setRefreshActionItemState(true);
	}
	
	@UiThreadDelayed(100)
	void loadUrlDelayed() {
		webview.loadUrl(webviewURL);
		webview.clearHistory();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && webview.canGoBack()) {
			webview.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@OptionsItem
	public void homeSelected() {
		finish();
	}
	
	@OptionsItem
	void menuShareSelected() {
		
		String sharingUrl = String.format(SHARE_PROPOSITION_URL, electionNamespace, propositionId);
		
		String message = String.format(shareProposition, sharingUrl);
		
		Intent sharingIntent = new Intent(ACTION_SEND);
		sharingIntent.setType("text/plain");
		sharingIntent.putExtra(EXTRA_TEXT, message);
		startActivity(Intent.createChooser(sharingIntent, shareWith));
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
		webview.clearHistory();
		getActionBarHelper().setRefreshActionItemState(false);
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
