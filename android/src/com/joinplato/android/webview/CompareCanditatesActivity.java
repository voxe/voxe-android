package com.joinplato.android.webview;

import static com.google.common.collect.Iterables.transform;

import java.io.Serializable;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
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
import com.joinplato.android.TheVoxeSimpleApplication;
import com.joinplato.android.actionbar.ActionBarActivity;
import com.joinplato.android.common.UIUtils;
import com.joinplato.android.model.Candidate;
import com.joinplato.android.model.Theme;

@EActivity(R.layout.compare)
@OptionsMenu(R.menu.compare)
public class CompareCanditatesActivity extends ActionBarActivity {

	private static final String WEBVIEW_URL_FORMAT = "http://voxe.org/webviews/compare?electionId=%s&candidateIds=%s&themeId=%s";
	private static final String SELECTED_CANDIDATES_EXTRA = "selectedCandidates";
	private static final String SELECTED_THEME_EXTRA = "selectedTheme";
	
	public static void start(Context context, List<Candidate> selectedCandidates, Theme selectedTheme) {
		Intent intent = new Intent(context, CompareCanditatesActivity_.class);
		intent.putExtra(SELECTED_CANDIDATES_EXTRA, (Serializable) selectedCandidates);
		intent.putExtra(SELECTED_THEME_EXTRA, selectedTheme);
		context.startActivity(intent);
	}
	
	@App
	TheVoxeSimpleApplication application;

	@Extra(SELECTED_CANDIDATES_EXTRA)
	List<Candidate> selectedCandidates;

	@Extra(SELECTED_THEME_EXTRA)
	Theme selectedTheme;

	@ViewById
	WebView webview;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(selectedTheme.name);
	}

	@AfterViews
	void prepareWebview() {
		WebSettings settings = webview.getSettings();
		settings.setJavaScriptEnabled(true);
		settings.setUserAgentString("Toto");
		webview.setWebViewClient(new HelloWebViewClient());
		
		Iterable<String> candidateIds = transform(selectedCandidates, new Function<Candidate, String>() {
			@Override
			public String apply(Candidate input) {
				return input.id;
			}
		});
		
		String candidateIdsJoined = Joiner.on(',').join(candidateIds);
		String electionId = application.getElectionId();
		String themeId = selectedTheme.id;
		
		String webviewURL = String.format(WEBVIEW_URL_FORMAT, electionId, candidateIdsJoined, themeId);
		
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

	private class HelloWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}
	}

	@OptionsItem
	public void homeSelected() {
		SelectCandidatesActivity.start(this);
		if (!UIUtils.isHoneycomb()) {
			overridePendingTransition(R.anim.home_enter, R.anim.home_exit);
		}
	}

	@OptionsItem
	void menuCandidatesSelected() {
		SelectCandidatesActivity.start(this, selectedTheme);
		if (!UIUtils.isHoneycomb()) {
			overridePendingTransition(R.anim.home_enter, R.anim.home_exit);
		}
	}

}
