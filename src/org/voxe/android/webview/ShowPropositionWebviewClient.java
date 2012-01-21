package org.voxe.android.webview;

import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.googlecode.androidannotations.annotations.Enhanced;
import com.googlecode.androidannotations.annotations.RootContext;
import com.googlecode.androidannotations.annotations.ViewById;

@Enhanced
public class ShowPropositionWebviewClient extends WebViewClient {
	
	@ViewById
	WebView webview;

	@RootContext
	ShowPropositionActivity activity;
	
	@Override
	public void onPageFinished(WebView view, String url) {
		activity.loadingDone();
	}

	@Override
	public boolean shouldOverrideUrlLoading(WebView view, String url) {
		return false;
	}

	@Override
	public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
		activity.showLoadingErrorDialog(description, failingUrl);
	}
}
