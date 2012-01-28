package org.voxe.android;

import org.voxe.android.model.ElectionHolder;

import android.app.Application;

import com.google.common.base.Optional;
import com.ubikod.capptain.android.sdk.CapptainAgentUtils;

public class VoxeApplication extends Application {

	private static final String ELECTION_ID_2007 = "4ef479f8bc60fb0004000001";

	private Optional<ElectionHolder> optionalElectionHolder = Optional.absent();

	@Override
	public void onCreate() {
		super.onCreate();
		if (CapptainAgentUtils.isInDedicatedCapptainProcess(this))
			return;
	}

	/**
	 * @param electionHolder
	 *            should not be null
	 */
	public synchronized void setElectionHolder(ElectionHolder electionHolder) {
		optionalElectionHolder = Optional.of(electionHolder);
	}

	public synchronized Optional<ElectionHolder> getElectionHolder() {
		return optionalElectionHolder;
	}

	public String getElectionId() {
		return ELECTION_ID_2007;
	}
}
