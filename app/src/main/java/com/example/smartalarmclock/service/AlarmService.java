package com.example.smartalarmclock.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationManagerCompat;

import com.example.smartalarmclock.SmartClockStates;
import com.example.smartalarmclock.netCode.NetCommand;

import java.util.Date;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class AlarmService extends WakeIntentService {

    public AlarmService() {
        super("AlarmService");
    }

    @Override
    void doReminderWork(Intent intent) {
        Log.e("ERR", "doReminderWork: tracker");
        Notification alarm = SmartClockStates.buildNotificationAlarm(getApplicationContext(), new Date());
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        notificationManager.notify(SmartClockStates.notificationID, alarm);
        if (Build.MODEL != null && !Build.MODEL.contains("SDK")) {
            NetCommand.powerOnSync(SmartClockStates.netInstance.smartPlugSockets.get(0), 9999, 5000);
            boolean continueWork = true;
            for (int stepTry = 0; stepTry < 5 && continueWork; stepTry++) {
                try {
                    if (NetCommand.isPowerOn(SmartClockStates.netInstance.smartPlugSockets.get(0), 9999, 5000)) {
                        continueWork = false;
                    } else {
                        NetCommand.powerOnSync(SmartClockStates.netInstance.smartPlugSockets.get(0), 9999, 5000);
                    }
                } catch (Exception error) {
                    Log.e("ERR", "doReminderWork: Error", error);
                    continueWork = false;
                }
            }
        }

    }


}
