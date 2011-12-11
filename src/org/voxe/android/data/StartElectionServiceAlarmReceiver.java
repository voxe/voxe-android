package org.voxe.android.data;

import org.voxe.android.common.AlarmHelper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class StartElectionServiceAlarmReceiver extends BroadcastReceiver {

	private static final int UPDATE_EVERY_DAY = 1000 * 60 * 60 * 24;

	public static void registerAlarm(Context context) {
		AlarmHelper.registerAlarmReceiver(context, StartElectionServiceAlarmReceiver.class, UPDATE_EVERY_DAY);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		UpdateElectionService.startUpdate(context);
	}

}
