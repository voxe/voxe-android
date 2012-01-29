package org.voxe.android.tag;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

import java.util.ArrayList;

import org.voxe.android.R;
import org.voxe.android.VoxeApplication;
import org.voxe.android.actionbar.ActionBarActivity;
import org.voxe.android.common.AboutDialogHelper;
import org.voxe.android.common.ComparisonPref_;
import org.voxe.android.comparison.ComparisonActivity_;
import org.voxe.android.loading.LoadingActivity_;
import org.voxe.android.model.Election;
import org.voxe.android.model.ElectionHolder;
import org.voxe.android.model.Tag;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import com.google.common.base.Optional;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.App;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.Inject;
import com.googlecode.androidannotations.annotations.ItemClick;
import com.googlecode.androidannotations.annotations.OptionsItem;
import com.googlecode.androidannotations.annotations.ViewById;
import com.googlecode.androidannotations.annotations.sharedpreferences.Pref;

/**
 * TODO handle button to reselect candidates
 * 
 * TODO show selected candidates
 */
@EActivity(R.layout.select_tag_list)
public class SelectTagActivity extends ActionBarActivity {

	@Pref
	ComparisonPref_ comparisonPref;

	@ViewById
	ListView list;

	private SelectTagAdapter tagAdapter;

	private Election election;

	@Inject
	AboutDialogHelper aboutDialogHelper;

	@App
	VoxeApplication application;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Optional<ElectionHolder> optionalElectionHolder = application.getElectionHolder();
		if (optionalElectionHolder.isPresent()) {
			ElectionHolder electionHolder = optionalElectionHolder.get();
			election = electionHolder.election;

			if (hasSelectedTag()) {
				startComparisonActivity();
			}

		} else {
			LoadingActivity_.intent(this).flags(FLAG_ACTIVITY_CLEAR_TOP).start();
			finish();
		}
	}

	private boolean hasSelectedTag() {
		String selectedTagId = comparisonPref.selectedTagId().get();
		Tag selectedTag = null;
		if (selectedTagId != "") {
			for (Tag tag : election.tags) {
				if (tag.id.equals(selectedTagId)) {
					selectedTag = tag;
					break;
				}
			}
		}
		return selectedTag != null;
	}

	private void startComparisonActivity() {
		Intent intent = ComparisonActivity_.intent(this).get();
		startActivityForResult(intent, 1);
	}

	@AfterViews
	void initLayout() {
		if (isFinishing()) {
			return;
		}

		tagAdapter = new SelectTagAdapter(this, new ArrayList<Tag>());
		list.setAdapter(tagAdapter);
		tagAdapter.updateTags(election.tags);
	}

	@ItemClick
	void listItemClicked(Tag selectedTag) {
		comparisonPref.selectedTagId().put(selectedTag.id);
		startComparisonActivity();
	}

	@OptionsItem
	public void homeSelected() {
		showDialog(R.id.about_dialog);
	}

	@Override
	protected Dialog onCreateDialog(int id, Bundle args) {
		switch (id) {
		case R.id.about_dialog:
			return aboutDialogHelper.createAboutDialog();
		default:
			return null;
		}
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		comparisonPref.selectedTagId().remove();
	}
	
	@Click
	void selectCandidatesButtonClicked() {
		setResult(RESULT_CANCELED);
		finish();
	}

}
