package org.voxe.android;

import org.voxe.android.model.ElectionsHolder;

import android.app.Application;

import com.google.common.base.Optional;
import com.ubikod.capptain.android.sdk.CapptainAgentUtils;

public class VoxeApplication extends Application {

	private Optional<ElectionsHolder> optionalElectionHolder = Optional.absent();

	@Override
	public void onCreate() {
		super.onCreate();
		if (CapptainAgentUtils.isInDedicatedCapptainProcess(this)) {
			return;
		}
	}

	/**
	 * @param electionHolder
	 *            should not be null
	 */
	public synchronized void setElectionHolder(ElectionsHolder electionHolder) {
		optionalElectionHolder = Optional.of(electionHolder);
	}

	public synchronized Optional<ElectionsHolder> getElectionHolder() {
		return optionalElectionHolder;
	}
}
