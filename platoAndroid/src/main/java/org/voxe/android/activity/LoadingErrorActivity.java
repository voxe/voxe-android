package org.voxe.android.activity;

import org.voxe.android.R;

import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.Extra;
import com.googlecode.androidannotations.annotations.ViewById;

@EActivity(R.layout.loading_error)
public class LoadingErrorActivity extends SherlockActivity {

	@Extra
	String description;

	@ViewById
	TextView descriptionTextView;

	@AfterViews
	void init() {
		descriptionTextView.setText(description);
	}

	@Click
	void retryClicked() {
		setResult(RESULT_OK);
		finish();
	}

	@Click
	void cancelClicked() {
		setResult(RESULT_CANCELED);
		finish();
	}

}
