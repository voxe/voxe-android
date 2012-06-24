package org.voxe.android.common;

import org.voxe.android.activity.ComparisonActivity;

import android.graphics.Bitmap;
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
		activity.endLoading();
	}

	@Override
	public void onPageStarted(WebView view, String url, Bitmap favicon) {
		LogHelper.log("Started loading url: " + url);
		activity.startLoading();
	}

	@Override
	public boolean shouldOverrideUrlLoading(WebView view, String url) {
		LogHelper.log("Client received loading url: " + url);
		return false;
	}

	@Override
	public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
		activity.loadingError(description);
	}
}
