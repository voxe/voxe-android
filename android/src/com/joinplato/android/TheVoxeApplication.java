package com.joinplato.android;

import android.app.Application;

import com.google.common.base.Optional;
import com.joinplato.android.data.ElectionAdapter;
import com.joinplato.android.data.StartElectionServiceAlarmReceiver;
import com.joinplato.android.model.ElectionHolder;

public class TheVoxeApplication extends Application {

	private static final String ELECTION_ID_2007 = "4ed1cb0203ad190006000001";

	private Optional<ElectionHolder> electionHolder = Optional.absent();

	private final Object electionLock = new Object();

	@Override
	public void onCreate() {
		super.onCreate();
		StartElectionServiceAlarmReceiver.registerAlarm(this);

		preloadInBackground();
	}

	private void preloadInBackground() {
		new Thread() {
			public void run() {
				getElectionHolder();
			};
		}.start();
	}

	public Optional<ElectionHolder> getElectionHolder() {
		synchronized (electionLock) {
			if (!electionHolder.isPresent()) {
				ElectionAdapter electionAdapter = new ElectionAdapter(this);
				electionHolder = electionAdapter.load();
			}
			return electionHolder;
		}
	}

	public String getElectionId() {
		return ELECTION_ID_2007;
	}
}
