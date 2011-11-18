package com.joinplato.android.programs;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.OptionsItem;
import com.googlecode.androidannotations.annotations.OptionsMenu;
import com.googlecode.androidannotations.annotations.ViewById;
import com.joinplato.android.R;
import com.joinplato.android.actionbar.ActionBarActivity;
import com.joinplato.android.common.AbstractOnPageChangeListener;
import com.joinplato.android.common.Candidate;
import com.joinplato.android.common.HomeHelper;

@EActivity(R.layout.programs)
@OptionsMenu(R.menu.main)
public class ProgramsActivity extends ActionBarActivity {
	
	public static void start(Context context) {
		context.startActivity(new Intent(context, ProgramsActivity_.class));
	}

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


	@OptionsItem
	public void homeSelected() {
		HomeHelper.backToHome(this);
	}
	
	public void onPageSelected(int position) {
		setTitle(candidates.get(position).getName());
	}

}
