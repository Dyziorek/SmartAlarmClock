package com.example.smartalarmclock.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

import com.example.smartalarmclock.SmartClockStates;

import java.util.Date;

public abstract class WakeIntentService extends IntentService {

    abstract void doReminderWork(Intent intent);

    public static final String LOCK_NAME_STATIC = "smartAlarm:com.android.voodootv.static";
    private static PowerManager.WakeLock lockStatic = null;
    private Notification notification;

    public static void acquireStaticLock(Context context) {
        getLock(context).acquire();
    }

    synchronized private static PowerManager.WakeLock getLock(Context context) {
        if (lockStatic == null) {
            PowerManager powManager = (PowerManager) context
                    .getSystemService(Context.POWER_SERVICE);
            lockStatic = powManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, LOCK_NAME_STATIC);
            lockStatic.setReferenceCounted(true);
        }
        return (lockStatic);
    }

    public WakeIntentService(String name) {
        super(name);
    }

    @Override
    final protected void onHandleIntent(Intent intent) {
        try {
            if (notification == null)
            {
                notification = SmartClockStates.buildNotificationAlarm(getApplicationContext(), new Date());
            }
            startForeground(SmartClockStates.notificationID, notification);
            doReminderWork(intent);
        } finally {
            stopForeground(true);
            getLock(this).release();
        }
    }}