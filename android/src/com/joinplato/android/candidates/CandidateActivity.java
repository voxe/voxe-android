package com.joinplato.android.candidates;

import java.io.Serializable;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.widget.Toast;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.Extra;
import com.googlecode.androidannotations.annotations.OptionsItem;
import com.googlecode.androidannotations.annotations.OptionsMenu;
import com.googlecode.androidannotations.annotations.ViewById;
import com.joinplato.android.R;
import com.joinplato.android.actionbar.ActionBarActivity;
import com.joinplato.android.common.AbstractOnPageChangeListener;
import com.joinplato.android.common.Candidate;
import com.joinplato.android.common.HomeHelper;

@EActivity(R.layout.view_pager)
@OptionsMenu(R.menu.candidate)
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
	
	@OptionsItem
	public void homeSelected() {
		HomeHelper.backToHome(this);
	}
	
	@OptionsItem
	public void menuSearchSelected() {
		Toast.makeText(this, "Work in progress", Toast.LENGTH_SHORT).show();
	}

	public void onPageSelected(int position) {
		setTitle(candidates.get(position).getName());
	}

}
