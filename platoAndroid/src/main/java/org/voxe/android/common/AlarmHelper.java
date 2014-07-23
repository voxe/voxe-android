package org.voxe.android.common;

import static android.app.AlarmManager.ELAPSED_REALTIME_WAKEUP;
import static android.content.Context.ALARM_SERVICE;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

public class AlarmHelper {

	/**
	 * This should be called when the application first starts
	 */
	public static void registerAlarmReceiver(Context context, Class<?> receiverClass, long interval) {
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);

		Intent alarmIntent = new Intent(context, receiverClass);
		PendingIntent pendingItent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);

		registerAlarmAndCancelPrevious(alarmManager, pendingItent, interval);
	}

	private static void registerAlarmAndCancelPrevious(AlarmManager alarmManager, PendingIntent pendingItent, long interval) {
		long nextExecutionTimestamp = SystemClock.elapsedRealtime();

		alarmManager.setRepeating(ELAPSED_REALTIME_WAKEUP, nextExecutionTimestamp, interval, pendingItent);
	}

}
