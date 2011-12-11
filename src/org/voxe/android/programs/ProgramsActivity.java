package org.voxe.android.programs;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.voxe.android.R;
import org.voxe.android.actionbar.ActionBarActivity;
import org.voxe.android.common.AbstractOnPageChangeListener;
import org.voxe.android.common.Candidate;
import org.voxe.android.common.HomeHelper;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.Extra;
import com.googlecode.androidannotations.annotations.OptionsItem;
import com.googlecode.androidannotations.annotations.OptionsMenu;
import com.googlecode.androidannotations.annotations.ViewById;
import com.googlecode.androidannotations.annotations.sharedpreferences.Pref;

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

	private SelectCandidateDialogAdapter selectCandidateDialogAdapter;

	private ProgramPagerAdapter programPagerAdapter;

	@AfterViews
	void disableSlide() {
		if (programPref.hideAdvice().get()) {
			slideAdvice.setVisibility(View.GONE);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		candidates = SelectedCandidate.filterSelected(selectedCandidates);
	}

	@AfterViews
	void buildPager() {
		programPagerAdapter = new ProgramPagerAdapter(this, candidates);
		viewPager.setAdapter(programPagerAdapter);
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
	void menuProgramSelected() {
		showDialog(R.id.program_item_dialog);
	}

	@OptionsItem
	void menuCandidatesSelected() {
		showDialog(R.id.select_candidates_dialog);
	}

	@OptionsItem
	public void homeSelected() {
		HomeHelper.backToHome(this);
	}

	@Override
	protected Dialog onCreateDialog(int id, Bundle args) {

		switch (id) {
		case R.id.program_item_dialog:
			return new AlertDialog.Builder(this) //
					.setTitle(R.string.select_a_theme) //
					.setSingleChoiceItems(programCategoryAdapter, 0, new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					}) //
					.create();
		case R.id.select_candidates_dialog: {
			final GridView gridView = (GridView) View.inflate(this, R.layout.select_candidates, null);
			selectCandidateDialogAdapter = new SelectCandidateDialogAdapter(this, new ArrayList<SelectedCandidate>());
			gridView.setAdapter(selectCandidateDialogAdapter);

			gridView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
					SelectedCandidate candidate = selectCandidateDialogAdapter.getItem(position);

					candidate.toggleSelected();

					View candidateView = gridView.getChildAt(position - gridView.getFirstVisiblePosition());

					selectCandidateDialogAdapter.updateCheckbox(candidateView, candidate);
				}
			});
			return new AlertDialog.Builder(this) //
					.setTitle(R.string.select_compare) //
					.setView(gridView) //
					.setPositiveButton(android.R.string.ok, new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							selectedCandidates = selectCandidateDialogAdapter.getSelectedCandidates();
							candidates = SelectedCandidate.filterSelected(selectedCandidates);
							programPagerAdapter.updateCandidates(candidates);
						}
					}) //
					.setNegativeButton(android.R.string.cancel, null).create();
		}

		default:
			return super.onCreateDialog(id, args);
		}

	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog, Bundle args) {

		switch (id) {
		case R.id.select_candidates_dialog: {
			List<SelectedCandidate> candidates = SelectedCandidate.cloneSelected(selectedCandidates);
			selectCandidateDialogAdapter.updateSelectedCandidates(candidates);
		}
		default:
			super.onPrepareDialog(id, dialog, args);
		}

	}

}
