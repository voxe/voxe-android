package com.joinplato.android;

import android.app.Application;
import android.os.Handler;

import com.google.common.base.Optional;
import com.joinplato.android.data.ElectionAdapter;
import com.joinplato.android.data.StartElectionServiceAlarmReceiver;
import com.joinplato.android.model.ElectionHolder;

public class TheVoxeApplication extends Application {
	
	public static final boolean COPY_TO_SD = true;

	private static final String ELECTION_ID_2007 = "4ed1cb0203ad190006000001";

	private volatile Optional<ElectionHolder> electionHolder = Optional.absent();

	private final Object electionLock = new Object();
	
	private final Handler handler = new Handler();
	
	private UpdateElectionListener updateElectionListener;
	
	public interface UpdateElectionListener {
		public void onElectionUpdate(Optional<ElectionHolder> electionHolder);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		preloadInBackground();
		StartElectionServiceAlarmReceiver.registerAlarm(this);
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
	
	public void postReplaceElectionHolder(final ElectionHolder electionHolder) {
		
		handler.post(new Runnable() {
			
			@Override
			public void run() {
				replaceElectionHolder(electionHolder);
			}
		});
		
	}
		
	public void replaceElectionHolder(final ElectionHolder electionHolder) {
		synchronized (electionLock) {
			this.electionHolder = Optional.fromNullable(electionHolder);
			if (updateElectionListener != null) {
				updateElectionListener.onElectionUpdate(this.electionHolder);
			}
		}
	}
	
	public void setUpdateElectionListener(UpdateElectionListener updateElectionListener) {
		this.updateElectionListener = updateElectionListener;
	}

	public String getElectionId() {
		return ELECTION_ID_2007;
	}
}
