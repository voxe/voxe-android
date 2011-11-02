package com.joinplato.android;

import java.io.Serializable;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.Extra;
import com.joinplato.android.actionbar.ActionBarActivity;

@EActivity(R.layout.wip)
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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(candidates.get(initialCandidate).getName());
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

}
