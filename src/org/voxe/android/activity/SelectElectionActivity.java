package org.voxe.android.activity;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

import org.voxe.android.R;
import org.voxe.android.VoxeApplication;
import org.voxe.android.adapter.ElectionAdapter;
import org.voxe.android.common.Analytics;
import org.voxe.android.data.ElectionDAO;
import org.voxe.android.model.Election;
import org.voxe.android.model.ElectionsHolder;

import android.content.Intent;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockActivity;
import com.google.common.base.Optional;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.App;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.ItemClick;
import com.googlecode.androidannotations.annotations.OptionsItem;
import com.googlecode.androidannotations.annotations.OptionsMenu;
import com.googlecode.androidannotations.annotations.ViewById;
import com.googlecode.androidannotations.annotations.res.HtmlRes;

@EActivity(R.layout.select_elections_list)
@OptionsMenu(R.menu.select_election)
public class SelectElectionActivity extends SherlockActivity {

	@HtmlRes
	CharSequence voxeOrgTitle;

	@App
	VoxeApplication application;

	@Bean
	ElectionAdapter adapter;

	@ViewById
	ListView listView;

	@Bean
	Analytics analytics;

	@AfterViews
	void init() {
		setTitle(voxeOrgTitle);

		Optional<ElectionsHolder> optionalElectionHolder = application.getElectionHolder();
		if (optionalElectionHolder.isPresent()) {

			ElectionsHolder electionHolder = optionalElectionHolder.get();

			adapter.init(electionHolder.elections);

			listView.setAdapter(adapter);
		} else {
			LoadingActivity_ //
					.intent(this) //
					.flags(FLAG_ACTIVITY_CLEAR_TOP) //
					.start();
			finish();
		}
	}

	@OptionsItem
	void aboutSelected() {
		AboutActivity_.intent(this).start();
	}

	@OptionsItem
	void refreshSelected() {
		application.setElectionHolder(null);
		ElectionDAO electionDAO = new ElectionDAO(this);
		electionDAO.clearData();
		LoadingActivity_ //
				.intent(this) //
				.flags(FLAG_ACTIVITY_CLEAR_TOP) //
				.start();
		finish();
	}

	@ItemClick
	void listViewItemClicked(int position) {

		Election selectedElection = adapter.getItem(position);

		analytics.electionSelected(selectedElection);

		Intent intent = SelectCandidatesActivity_ //
				.intent(this) //
				.electionIndex(position) //
				.get();

		startActivityForResult(intent, 1);
	}
}
