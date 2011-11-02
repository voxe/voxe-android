package com.joinplato.android;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.ItemClick;
import com.googlecode.androidannotations.annotations.ViewById;
import com.joinplato.android.actionbar.ActionBarActivity;

@EActivity(R.layout.news)
public class NewsActivity extends ActionBarActivity {

	@ViewById
	ListView list;

	@AfterViews
	void mockList() {
		NewsAdapter adapter = new NewsAdapter(this, NewsElement.mockNews());
		list.setAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.news, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			HomeHelper.backToHome(this);
			break;
		case R.id.menu_refresh:
			Toast.makeText(this, "Refreshing...", Toast.LENGTH_SHORT).show();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@ItemClick
    void listItemClicked(NewsElement newsElement) {
    	Toast.makeText(this, newsElement.getTitle(), Toast.LENGTH_SHORT).show();
    }
}
