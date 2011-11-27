package com.joinplato.android;

import com.joinplato.android.data.StartElectionServiceAlarmReceiver;
import com.joinplato.android.data.UpdateElectionService;

import android.app.Application;

public class TheVoxeApplication extends Application {
	
	@Override
	public void onCreate() {
		super.onCreate();
		StartElectionServiceAlarmReceiver.registerAlarm(this);
		 UpdateElectionService.startUpdate(this);
	}

}
