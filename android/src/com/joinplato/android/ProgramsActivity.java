package com.joinplato.android;

import android.view.Menu;
import android.view.MenuItem;

import com.googlecode.androidannotations.annotations.EActivity;
import com.joinplato.android.actionbar.ActionBarActivity;

@EActivity(R.layout.wip)
public class ProgramsActivity extends ActionBarActivity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
            	HomeHelper.backToHome(this);
            break;

        }
        return super.onOptionsItemSelected(item);
    }
	
}
