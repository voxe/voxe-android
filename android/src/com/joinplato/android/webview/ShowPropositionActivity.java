package com.joinplato.android.webview;

import static android.content.Intent.ACTION_SEND;
import static android.content.Intent.EXTRA_TEXT;
import static com.google.common.collect.Iterables.transform;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.App;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.Extra;
import com.googlecode.androidannotations.annotations.OptionsItem;
import com.googlecode.androidannotations.annotations.OptionsMenu;
import com.googlecode.androidannotations.annotations.ViewById;
import com.joinplato.android.R;
import com.joinplato.android.TheVoxeApplication;
import com.joinplato.android.actionbar.ActionBarActivity;
import com.joinplato.android.common.UIUtils;
import com.joinplato.android.model.Candidate;

@EActivity(R.layout.show_proposition)
@OptionsMenu(R.menu.proposition)
public class ShowPropositionActivity extends ActionBarActivity {

	public static final String SHOW_PROPOSITION_PATH_FRAGMENT = "/webviews/propositions?id=";

	private static final String WEBVIEW_URL_FORMAT = "http://voxe.org/webviews/propositions?id=%s";

	private static final String PROPOSITION_ID_EXTRA = "propositionId";

	public static void start(Context context, String url) {

		int fragmentIndex = url.indexOf(SHOW_PROPOSITION_PATH_FRAGMENT);
		if (fragmentIndex != -1) {

			int propositionIdIndex = fragmentIndex + SHOW_PROPOSITION_PATH_FRAGMENT.length();

			String propositionId = url.substring(propositionIdIndex);

			// TODO Capptain log

			Intent intent = new Intent(context, ShowPropositionActivity_.class);
			intent.putExtra(PROPOSITION_ID_EXTRA, propositionId);
			context.startActivity(intent);
		}

	}
	
	private class ShowPropositionWebViewClient extends WebViewClient {

		@Override
		public void onPageFinished(WebView view, String url) {
			loadingLayout.setVisibility(View.GONE);
			webview.setVisibility(View.VISIBLE);
		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			loadingLayout.setVisibility(View.VISIBLE);
			webview.setVisibility(View.GONE);
			return false;
		}
	}

	@App
	TheVoxeApplication application;

	@ViewById
	View loadingLayout;

	@Extra(PROPOSITION_ID_EXTRA)
	String propositionId;

	@ViewById
	WebView webview;

	@AfterViews
	void prepareWebview() {
		WebSettings settings = webview.getSettings();
		settings.setJavaScriptEnabled(true);
		
		webview.setWebViewClient(new ShowPropositionWebViewClient());

		String webviewURL = String.format(WEBVIEW_URL_FORMAT, propositionId);

		webview.loadUrl(webviewURL);
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
		SelectCandidatesActivity.start(this);
		if (!UIUtils.isHoneycomb()) {
			overridePendingTransition(R.anim.home_enter, R.anim.home_exit);
		}
	}
	
	@OptionsItem
	void menuShareSelected() {
		Intent sharingIntent = new Intent(ACTION_SEND);
		sharingIntent.setType("text/plain");
		
		
		String message = String.format(getString(R.string.share_proposition), propositionId);
		sharingIntent.putExtra(EXTRA_TEXT, message);
		startActivity(Intent.createChooser(sharingIntent, "Partager via"));
	}

}
