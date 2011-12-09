package com.joinplato.android;

import android.app.Application;

import com.google.common.base.Optional;
import com.joinplato.android.model.ElectionHolder;

/**
 * A simplified version of {@link TheVoxeApplication}, because we don't use any
 * of this any more.
 * 
 */
public class TheVoxeSimpleApplication extends Application {
	
	private static final String ELECTION_ID_2007 = "4ed1cb0203ad190006000001";
	
	private Optional<ElectionHolder> electionHolder = Optional.absent();

	public Optional<ElectionHolder> getElectionHolder() {
		return electionHolder;
	}
	
	public void setElectionHolder(ElectionHolder electionHolder) {
		this.electionHolder = Optional.of(electionHolder);
	}
	
	public String getElectionId() {
		return ELECTION_ID_2007;
	}

}
