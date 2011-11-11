package com.joinplato.android;

import java.io.Serializable;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.Extra;
import com.googlecode.androidannotations.annotations.ViewById;
import com.joinplato.android.actionbar.ActionBarActivity;

@EActivity(R.layout.view_pager)
public class CandidateActivity extends ActionBarActivity {

	private static final String CANDIDATE_LIST_EXTRA = "candidateList";

	private static final String INITIAL_CANDIDATE_EXTRA = "initialCandidate";

	public static void start(Activity activity, List<Candidate> candidates, int initialCandidate) {
		Intent intent = new Intent(activity, CandidateActivity_.class);
		intent.putExtra(CANDIDATE_LIST_EXTRA, (Serializable) candidates);
		intent.putExtra(INITIAL_CANDIDATE_EXTRA, initialCandidate);
		activity.startActivity(intent);
	}

	@Extra(CANDIDATE_LIST_EXTRA)
	List<Candidate> candidates;

	@Extra(INITIAL_CANDIDATE_EXTRA)
	int initialCandidate;

	@ViewById
	ViewPager viewPager;

	@AfterViews
	void mockCandidates() {
		CandidatePagerAdapter adapter = new CandidatePagerAdapter(this, candidates);
		viewPager.setAdapter(adapter);
		viewPager.setOnPageChangeListener(new AbstractOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				CandidateActivity.this.onPageSelected(position);
			}
		});
		viewPager.setCurrentItem(initialCandidate);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.candidate, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			HomeHelper.backToHome(this);
			break;
		case R.id.menu_search:
			Toast.makeText(this, "Work in progress", Toast.LENGTH_SHORT).show();
			break;

		}
		return super.onOptionsItemSelected(item);
	}

	public void onPageSelected(int position) {
		setTitle(candidates.get(position).getName());
	}

}
