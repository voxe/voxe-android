package com.joinplato.android;

import android.app.Application;

import com.joinplato.android.data.ElectionAdapter;
import com.joinplato.android.data.StartElectionServiceAlarmReceiver;
import com.joinplato.android.model.ElectionHolder;

public class TheVoxeApplication extends Application {
	
	private ElectionHolder electionHolder;
	
	private final Object electionLock = new Object();

	@Override
	public void onCreate() {
		super.onCreate();
		StartElectionServiceAlarmReceiver.registerAlarm(this);
	}
	
	public ElectionHolder getElectionHolder() {
		synchronized(electionLock) {
			if (electionHolder == null) {
				ElectionAdapter electionAdapter = new ElectionAdapter(this);
				electionHolder = electionAdapter.load().orNull();
			}
			return electionHolder;
		}
	}

}
