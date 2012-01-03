package org.voxe.android.webview;

import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.googlecode.androidannotations.annotations.Enhanced;
import com.googlecode.androidannotations.annotations.RootContext;
import com.googlecode.androidannotations.annotations.ViewById;

@Enhanced
public class CompareCandidateWebviewClient extends WebViewClient {
	
	@ViewById
	WebView webview;

	@ViewById
	View loadingLayout;
	
	@RootContext
	CompareCanditatesActivity activity;
	
	@Override
	public void onPageFinished(WebView view, String url) {
		loadingLayout.setVisibility(View.GONE);
		webview.setVisibility(View.VISIBLE);
	}

	@Override
	public boolean shouldOverrideUrlLoading(WebView view, String url) {
		if (url.contains(ShowPropositionActivity.SHOW_PROPOSITION_PATH_FRAGMENT)) {
			ShowPropositionActivity.start(activity, url);
			return true;
		} else {
			loadingLayout.setVisibility(View.VISIBLE);
			webview.setVisibility(View.GONE);
			return false;
		}
	}

	@Override
	public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
		activity.showLoadingErrorDialog(description, failingUrl);
	}
}
