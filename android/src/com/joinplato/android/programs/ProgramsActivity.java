package com.joinplato.android.programs;

import java.io.Serializable;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.Extra;
import com.googlecode.androidannotations.annotations.OptionsItem;
import com.googlecode.androidannotations.annotations.OptionsMenu;
import com.googlecode.androidannotations.annotations.ViewById;
import com.googlecode.androidannotations.annotations.sharedpreferences.Pref;
import com.joinplato.android.R;
import com.joinplato.android.actionbar.ActionBarActivity;
import com.joinplato.android.common.AbstractOnPageChangeListener;
import com.joinplato.android.common.Candidate;
import com.joinplato.android.common.HomeHelper;

@EActivity(R.layout.programs)
@OptionsMenu(R.menu.programs)
public class ProgramsActivity extends ActionBarActivity {

	private static final String CANDIDATES_EXTRA = "candidates";

	public static void start(Context context, List<SelectedCandidate> candidates) {
		Intent intent = new Intent(context, ProgramsActivity_.class);
		intent.putExtra(CANDIDATES_EXTRA, (Serializable) candidates);
		context.startActivity(intent);
	}

	@ViewById
	ViewPager viewPager;

	@ViewById
	TextView slideAdvice;

	@Pref
	ProgramPref_ programPref;
	
	@Extra(CANDIDATES_EXTRA)
	List<SelectedCandidate> selectedCandidates;

	private final ProgramCategoryAdapter programCategoryAdapter = new ProgramCategoryAdapter(this);

	private List<Candidate> candidates;

	@AfterViews
	void disableSlide() {
		if (programPref.hideAdvice().get()) {
			slideAdvice.setVisibility(View.GONE);
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		candidates = SelectedCandidate.filterSelected(selectedCandidates);
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

	public void onPageSelected(int position) {
		setTitle(candidates.get(position).getName());
		if (position != 0 && !programPref.hideAdvice().get()) {
			slideAdvice.setVisibility(View.GONE);
			programPref.hideAdvice().put(true);
		}
	}

	@OptionsItem
	void homeSelected() {
		HomeHelper.backToHome(this);
	}

	@OptionsItem
	void menuProgramSelected() {
		showDialog(R.id.program_item_dialog);
	}

	@Override
	protected Dialog onCreateDialog(int id, Bundle args) {
		if (id == R.id.program_item_dialog) {
			return new AlertDialog.Builder(this) //
					.setSingleChoiceItems(programCategoryAdapter, 0, new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					}) //
					.create();

		} else {
			return super.onCreateDialog(id, args);
		}
	}

}
