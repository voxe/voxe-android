package org.voxe.android.webview;

import static android.content.Intent.ACTION_SEND;
import static android.content.Intent.EXTRA_TEXT;
import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;
import static com.google.common.collect.Iterables.transform;

import java.util.List;

import org.voxe.android.R;
import org.voxe.android.TheVoxeApplication;
import org.voxe.android.actionbar.ActionBarActivity;
import org.voxe.android.common.LogHelper;
import org.voxe.android.common.UIUtils;
import org.voxe.android.model.Candidate;
import org.voxe.android.model.Tag;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.App;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.Extra;
import com.googlecode.androidannotations.annotations.Inject;
import com.googlecode.androidannotations.annotations.OptionsItem;
import com.googlecode.androidannotations.annotations.OptionsMenu;
import com.googlecode.androidannotations.annotations.ViewById;

@EActivity(R.layout.compare)
@OptionsMenu(R.menu.compare)
public class CompareCanditatesActivity extends ActionBarActivity {

	private static final String WEBVIEW_URL_FORMAT = "http://voxe.org/webviews/compare?electionId=%s&candidacyIds=%s&tagId=%s";
	private static final String SELECTED_CANDIDATES_EXTRA = "selectedCandidates";
	private static final String SELECTED_TAG_EXTRA = "selectedTag";

	private static final String FAILING_URL_ARG = "failingUrl";
	private static final String DESCRIPTION_ARG = "description";

	@App
	TheVoxeApplication application;

	@Extra(SELECTED_CANDIDATES_EXTRA)
	List<Candidate> selectedCandidates;

	@Extra(SELECTED_TAG_EXTRA)
	Tag selectedTag;

	@ViewById
	WebView webview;

	@ViewById
	View loadingLayout;
	
	@Inject
	CompareCandidateWebviewClient webviewClient;

	private String failingUrl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(selectedTag.getHackedTagName());
	}

	@AfterViews
	void prepareWebview() {
		WebSettings settings = webview.getSettings();
		// change to true when bug fixed
		settings.setJavaScriptEnabled(false);
		webview.setWebViewClient(webviewClient);
		webview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

		Iterable<String> candidacyIds = transform(selectedCandidates, new Function<Candidate, String>() {
			@Override
			public String apply(Candidate input) {
				return input.candidacyId;
			}
		});

		String candidacyIdsJoined = Joiner.on(',').join(candidacyIds);
		String electionId = application.getElectionId();
		String tagId = selectedTag.id;

		String webviewURL = String.format(WEBVIEW_URL_FORMAT, electionId, candidacyIdsJoined, tagId);

		LogHelper.log("Loading url " + webviewURL);

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
		SelectCandidatesActivity_.intent(this).flags(FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_SINGLE_TOP).start();
		if (!UIUtils.isHoneycomb()) {
			overridePendingTransition(R.anim.home_enter, R.anim.home_exit);
		}
	}

	@OptionsItem
	void menuCandidatesSelected() {
		SelectCandidatesActivity_.intent(this) //
				.flags(FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_SINGLE_TOP) //
				.selectedTheme(selectedTag) //
				.start();
		if (!UIUtils.isHoneycomb()) {
			overridePendingTransition(R.anim.home_enter, R.anim.home_exit);
		}
	}

	@OptionsItem
	void menuShareSelected() {
		Intent sharingIntent = new Intent(ACTION_SEND);
		sharingIntent.setType("text/plain");

		Iterable<String> candidateNames = transform(selectedCandidates, new Function<Candidate, String>() {
			@Override
			public String apply(Candidate input) {
				return input.getName().toString();
			}
		});

		String candidateNamesJoined = Joiner.on(',').join(candidateNames);
		String message = String.format(getString(R.string.share_compare), candidateNamesJoined, selectedTag.getHackedTagName());
		sharingIntent.putExtra(EXTRA_TEXT, message);
		startActivity(Intent.createChooser(sharingIntent, getString(R.string.share_with)));
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
	
	public void showLoadingErrorDialog(String description, String failingUrl) {
		Bundle bundle = new Bundle();
		bundle.putString(FAILING_URL_ARG, failingUrl);
		bundle.putString(DESCRIPTION_ARG, description);
		showDialog(R.id.webview_error_dialog, bundle);
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

}
