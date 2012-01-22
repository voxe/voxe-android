package org.voxe.android.webview;

import org.voxe.android.R;

import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.googlecode.androidannotations.annotations.Enhanced;
import com.googlecode.androidannotations.annotations.RootContext;

@Enhanced
public class ShowPropositionWebviewClient extends WebViewClient {
	
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
		String header = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>";
		view.loadData(header+String.format(activity.getString(R.string.webview_error_message), description), "text/html", "UTF-8");
	}
}
