package org.voxe.android.common;

import static java.lang.System.currentTimeMillis;
import static org.voxe.android.common.Config.LOG_TO_CONSOLE;
import android.util.Log;

public class LogHelper {

	public static void logException(String message, Exception exception) {
		if (LOG_TO_CONSOLE) {
			Log.e("TheVoxe", message, exception);
		}
	}

	public static void log(String message) {
		if (LOG_TO_CONSOLE) {
			Log.d("TheVoxe", message);
		}
	}

	public static void logDuration(String message, long startTimestamp) {
		if (LOG_TO_CONSOLE) {
			log(message + " Duration in ms: " + (currentTimeMillis() - startTimestamp));
		}
	}

}
