package org.voxe.android.common;

import static java.lang.System.currentTimeMillis;
import android.util.Log;

public class LogHelper {

	public static void logException(String message, Exception exception) {
		Log.e("TheVoxe", message, exception);
	}

	public static void log(String message) {
		Log.d("TheVoxe", message);
	}

	public static void logDuration(String message, long startTimestamp) {
		log(message + " Duration in ms: " + (currentTimeMillis() - startTimestamp));
	}

}
