package com.joinplato.android.news;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.Extra;
import com.joinplato.android.R;
import com.joinplato.android.actionbar.ActionBarActivity;
import com.joinplato.android.common.HomeHelper;

@EActivity(R.layout.wip)
public class NewsDetailActivity extends ActionBarActivity {

	private static final String NEWS_ELEMENT_EXTRA = "newsElement";

	@Extra(NEWS_ELEMENT_EXTRA)
	NewsElement newsElement;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(newsElement.getTitle());
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			HomeHelper.backToHome(this);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	public static void start(Activity activity, NewsElement element) {
		Intent intent = new Intent(activity, NewsDetailActivity_.class);
		intent.putExtra(NEWS_ELEMENT_EXTRA, element);
		activity.startActivity(intent);
	}

}
