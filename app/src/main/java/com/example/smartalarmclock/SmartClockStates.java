package com.example.smartalarmclock;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.smartalarmclock.helper.Settings;
import com.example.smartalarmclock.netCode.NetCommand;
import com.example.smartalarmclock.netCode.NetSubsystem;

import java.net.InetAddress;
import java.text.DateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import static com.example.smartalarmclock.helper.Settings.settings;

public class SmartClockStates {
    public static int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE;
    public static int MY_PERMISSIONS_REQUEST_PROCESS_OUTGOING_CALLS;
    public static String CHANNEL_ID = "VARIATIONS";
    public static int notificationID;
    public static NetSubsystem netInstance;


    public static void callReceived(Context ctx, String number, Date start)
    {
        Settings.reInitData(ctx);

        if (settings.getSmartPlugHost() != null)
        {
            ExecutorService execSingle = Executors.newSingleThreadExecutor();
            execSingle.submit(() -> {
                try {
                    if (settings.isAllowedToCall()) {
                        NetCommand.powerOn(InetAddress.getByName(settings.getSmartPlugHost()), settings.getSmartPlugPort(), 5000);
                    }
                }
                catch (Exception excErr)
                {
                    Log.e("ERR", "run: powerOn", excErr);
                }
            });
        }
    }

    public static void sendNotification(Context ctx, String number, Date start)
    {
        Resources res = ctx.getResources();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_plug)
                .setContentTitle(res.getString(R.string.phoneCalled))
                .setContentText(res.getString(R.string.phoneCallAt) + DateFormat.getDateInstance(DateFormat.SHORT).format(start))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(ctx);

// notificationId is a unique int for each notification that you must define
        notificationManager.notify(notificationID, builder.build());
    }

}
