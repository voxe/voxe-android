package org.voxe.android.comparison;

import org.voxe.android.R;
import org.voxe.android.common.LogHelper;
import org.voxe.android.proposition.ShowPropositionActivity;

import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.googlecode.androidannotations.annotations.EBean;
import com.googlecode.androidannotations.annotations.RootContext;

@EBean
public class ComparisonWebviewClient extends WebViewClient {

	@RootContext
	ComparisonActivity activity;

	@Override
	public void onPageFinished(WebView view, String url) {
		LogHelper.log("Finished loading url: " + url);
		activity.endLoading(url);
	}

	@Override
	public boolean shouldOverrideUrlLoading(WebView view, String url) {
		LogHelper.log("Client received loading url: " + url);
		if (url.contains(ShowPropositionActivity.SHOW_PROPOSITION_PATH_FRAGMENT)) {
			activity.showProposition(url);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
		String header = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>";
		view.loadData(header + String.format(activity.getString(R.string.webview_error_message), description), "text/html", "UTF-8");
	}
}
