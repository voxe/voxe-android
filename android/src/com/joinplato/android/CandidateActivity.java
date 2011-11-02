package com.joinplato.android;

import android.view.Menu;

import com.googlecode.androidannotations.annotations.EActivity;
import com.joinplato.android.actionbar.ActionBarActivity;

@EActivity(R.layout.wip)
public class CandidateActivity extends ActionBarActivity {
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }
    
}
