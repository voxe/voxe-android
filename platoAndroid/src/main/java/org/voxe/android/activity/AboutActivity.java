package org.voxe.android.activity;

import static android.content.Intent.ACTION_VIEW;

import org.voxe.android.R;
import org.voxe.android.common.ShareManager;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.FromHtml;
import com.googlecode.androidannotations.annotations.ViewById;
import com.googlecode.androidannotations.annotations.res.StringRes;

@EActivity(R.layout.about)
public class AboutActivity extends SherlockActivity {

	private static final Uri VOXE_MARKET_URI = Uri.parse("market://details?id=org.voxe.android");

	@FromHtml(R.string.about_content)
	@ViewById
	TextView descriptionTextView;

	@Bean
	ShareManager shareManager;

	@StringRes
	String shareVoxeContent;

	@AfterViews
	void init() {
		descriptionTextView.setMovementMethod(LinkMovementMethod.getInstance());
		descriptionTextView.setLinkTextColor(Color.parseColor("#ff3c00"));
	}

	@Click
	void shareVoxeClicked() {
		shareManager.share(shareVoxeContent);
		finish();
	}

	@Click
	void rateAndroidMarketClicked() {
		startActivity(new Intent(ACTION_VIEW, VOXE_MARKET_URI));
		finish();
	}

}
