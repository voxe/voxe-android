package org.voxe.android.webview;

import org.voxe.android.R;
import org.voxe.android.common.LogHelper;

import android.content.Context;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.googlecode.androidannotations.annotations.Enhanced;
import com.googlecode.androidannotations.annotations.RootContext;

@Enhanced
public class CompareCandidateWebviewClient extends WebViewClient {

	@RootContext
	Context context;
	
	private ComparisonView comparisonView;
	
	public void bind(ComparisonView comparisonView) {
		this.comparisonView = comparisonView;
	}

	@Override
	public void onPageFinished(WebView view, String url) {
		LogHelper.log("Finished loading url: " + url);
		comparisonView.endLoading(url);
	}

	@Override
	public boolean shouldOverrideUrlLoading(WebView view, String url) {
		LogHelper.log("Client received loading url: " + url);
		if (url.contains(ShowPropositionActivity.SHOW_PROPOSITION_PATH_FRAGMENT)) {
			ShowPropositionActivity.start(context, url);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
		String header = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>";
		view.loadData(header+String.format(context.getString(R.string.webview_error_message), description), "text/html", "UTF-8");
	}
}
