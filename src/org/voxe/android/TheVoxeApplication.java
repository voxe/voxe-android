package org.voxe.android;

import android.app.Application;
import android.os.Handler;

import com.google.common.base.Optional;
import org.voxe.android.R;
import org.voxe.android.data.ElectionAdapter;
import org.voxe.android.data.StartElectionServiceAlarmReceiver;
import org.voxe.android.model.ElectionHolder;

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
	
	public void reloadElectionHolder() {
		ElectionAdapter electionAdapter = new ElectionAdapter(this);
		Optional<ElectionHolder> optional = electionAdapter.load();
		if (optional.isPresent()) {
			postReplaceElectionHolder(optional);
		}
	}
	
	private void postReplaceElectionHolder(final Optional<ElectionHolder> electionHolder) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				replaceElectionHolder(electionHolder);
			}
		});
		
	}
		
	private void replaceElectionHolder(Optional<ElectionHolder> electionHolder) {
		synchronized (electionLock) {
			this.electionHolder = electionHolder;
			if (updateElectionListener != null) {
				updateElectionListener.onElectionUpdate(electionHolder);
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
