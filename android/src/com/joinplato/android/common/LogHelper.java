package com.joinplato.android.common;

import android.util.Log;

public class LogHelper {
	
	public static void logException(String message, Exception exception) {
		Log.e("TheVoxe", message, exception);
	}

}
