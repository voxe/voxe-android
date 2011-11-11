package com.joinplato.android;

import java.util.List;

import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.ViewById;
import com.joinplato.android.actionbar.ActionBarActivity;

@EActivity(R.layout.programs)
public class ProgramsActivity extends ActionBarActivity {

	@ViewById
	ListView list;

	@ViewById
	ViewPager viewPager;

	private final List<Candidate> candidates = Candidate.mockCandidates();

	@AfterViews
	void buildList() {
		ProgramCategoryAdapter adapter = new ProgramCategoryAdapter(this);
		list.setAdapter(adapter);

		float scale = getResources().getDisplayMetrics().density;
		final int miniWidth = (int) (60 * scale);
		final int maxiWidth = (int) (200 * scale);

		list.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (scrollState == SCROLL_STATE_IDLE) {
					LayoutParams params = list.getLayoutParams();
					params.width = miniWidth;
					list.setLayoutParams(params);
				} else {
					LayoutParams params = list.getLayoutParams();
					params.width = maxiWidth;
					list.setLayoutParams(params);
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

			}
		});
	}

	@AfterViews
	void buildPager() {
		ProgramPagerAdapter adapter = new ProgramPagerAdapter(this, candidates);
		viewPager.setAdapter(adapter);
		viewPager.setOnPageChangeListener(new AbstractOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				ProgramsActivity.this.onPageSelected(position);
			}
		});
		viewPager.setCurrentItem(0);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
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

	public void onPageSelected(int position) {
		setTitle(candidates.get(position).getName());
	}

}
