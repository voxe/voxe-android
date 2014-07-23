package org.voxe.android.common;

import org.voxe.android.BuildConfig;

public abstract class Config {

	public static final boolean LOG_TO_CONSOLE = true && BuildConfig.DEBUG;

	private Config() {
	}

}
