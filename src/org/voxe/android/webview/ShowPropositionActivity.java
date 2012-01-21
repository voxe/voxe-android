package org.voxe.android.webview;

import org.voxe.android.R;
import org.voxe.android.TheVoxeApplication;
import org.voxe.android.actionbar.ActionBarActivity;
import org.voxe.android.common.Analytics;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
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
import com.googlecode.androidannotations.annotations.ViewById;

@EActivity(R.layout.show_proposition)
@OptionsMenu(R.menu.proposition)
public class ShowPropositionActivity extends ActionBarActivity {

	public static final String SHOW_PROPOSITION_PATH_FRAGMENT = "/webviews/propositions/";

	private static final String WEBVIEW_URL_FORMAT = "http://voxe.org" + SHOW_PROPOSITION_PATH_FRAGMENT + "%s";

	private static final String PROPOSITION_ID_EXTRA = "propositionId";

	private static final String FAILING_URL_ARG = "failingUrl";

	private static final String DESCRIPTION_ARG = "description";

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

	@App
	TheVoxeApplication application;

	@ViewById
	View loadingLayout;

	@Extra(PROPOSITION_ID_EXTRA)
	String propositionId;

	@ViewById
	WebView webview;

	@Inject
	ShowPropositionWebviewClient webviewClient;

	@Inject
	Analytics analytics;

	private String failingUrl;

	private String webviewURL;

	@AfterViews
	void prepareWebview() {
		WebSettings settings = webview.getSettings();
		settings.setJavaScriptEnabled(true);
		webview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		webview.setWebViewClient(webviewClient);

		webviewURL = String.format(WEBVIEW_URL_FORMAT, propositionId);

		webview.loadUrl(webviewURL);
		getActionBarHelper().setRefreshActionItemState(true);
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
	public void menuRefreshSelected() {
		webview.loadUrl(webviewURL);
		getActionBarHelper().setRefreshActionItemState(true);
	}

	@Override
	protected Dialog onCreateDialog(int id, Bundle args) {
		switch (id) {
		case R.id.webview_error_dialog:
			return createWebviewErrorDialog();
		default:
			return null;
		}
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog, Bundle bundle) {
		switch (id) {
		case R.id.webview_error_dialog:
			prepareWebviewErrorDialog((AlertDialog) dialog, bundle.getString(FAILING_URL_ARG), bundle.getString(DESCRIPTION_ARG));
			break;
		}
	}

	private Dialog createWebviewErrorDialog() {
		return new AlertDialog.Builder(this) //
				.setTitle(R.string.webview_error_dialog) //
				.setMessage("") //
				.setOnCancelListener(new OnCancelListener() {

					@Override
					public void onCancel(DialogInterface dialog) {
						finish();
					}
				}) //
				.setPositiveButton(R.string.retry, new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						webview.loadUrl(failingUrl);
					}
				}) //
				.setNegativeButton(R.string.cancel, new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				}) //
				.create();
	}

	private void prepareWebviewErrorDialog(AlertDialog dialog, String failingUrl, String description) {
		this.failingUrl = failingUrl;
		dialog.setMessage(String.format(getString(R.string.webview_error_dialog_message), description));
	}

	public void showLoadingErrorDialog(String description, String failingUrl2) {
		Bundle bundle = new Bundle();
		bundle.putString(FAILING_URL_ARG, failingUrl);
		bundle.putString(DESCRIPTION_ARG, description);
		showDialog(R.id.webview_error_dialog, bundle);
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

	public void loadingDone() {
		getActionBarHelper().setRefreshActionItemState(false);
	}

}
