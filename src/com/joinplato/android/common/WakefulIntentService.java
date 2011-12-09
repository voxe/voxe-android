package com.joinplato.android.common;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

/**
 * Class based on code from the "Busy Coder's Guide to Advanced Android
 * Development" ( http://commonsware.com/AdvAndroid/)
 * 
 * Through the {@link #acquireStaticLock(Context)} method, we acquire a
 * {@link WakeLock} before calling the intent that executes the service, and
 * then release this lock once the service is executed.
 * 
 * WARNING: Never forget to call {@link #acquireStaticLock(Context)} before
 * starting this service, otherwise an under-locked exception will occur.
 */
public abstract class WakefulIntentService extends IntentService {

	private static final String LOCK_NAME = "com.joinplato.android.WAKE_LOCK";
	private static PowerManager.WakeLock lockStatic = null;

	public static void acquireStaticLock(Context context) {
		WakeLock lock = getLock(context);
		lock.acquire();
	}

	private static synchronized PowerManager.WakeLock getLock(Context context) {
		if (lockStatic == null) {
			initLock(context);
		}

		return lockStatic;
	}

	private static void initLock(Context context) {
		PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		lockStatic = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, LOCK_NAME);
		lockStatic.setReferenceCounted(true);
	}

	public WakefulIntentService(String name) {
		super(name);
	}

	@Override
	protected final void onHandleIntent(Intent intent) {
		try {
			doWakefulWork(intent);
		} finally {
			try {
				WakeLock lock = getLock(this);
				lock.release();
			} catch (RuntimeException e) {
			}
		}
	}

	protected abstract void doWakefulWork(Intent intent);
}
