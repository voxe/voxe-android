package com.joinplato.android;

import java.util.List;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.ItemClick;
import com.googlecode.androidannotations.annotations.ViewById;
import com.joinplato.android.actionbar.ActionBarActivity;

@EActivity(R.layout.list)
public class CandidateListActivity extends ActionBarActivity {

	@ViewById
	ListView list;

	private List<Candidate> candidates;

	@AfterViews
	void mockList() {
		candidates = Candidate.mockCandidates();
		CandidateAdapter adapter = new CandidateAdapter(this, candidates);
		list.setAdapter(adapter);
	}

	@ItemClick
	void listItemClicked(Candidate candidate) {
		CandidateActivity.start(this, candidates, candidates.indexOf(candidate));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.candidate_list, menu);
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

}
