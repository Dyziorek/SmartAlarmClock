package com.example.smartalarmclock;

import android.app.Notification;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.smartalarmclock.helper.Settings;
import com.example.smartalarmclock.netCode.NetCommand;
import com.example.smartalarmclock.netCode.NetSubsystem;
import com.example.smartalarmclock.persistence.NotifyEntity;
import com.example.smartalarmclock.persistence.NotifyDatabase;

import java.net.InetAddress;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.Completable;
import io.reactivex.disposables.CompositeDisposable;

import static com.example.smartalarmclock.helper.Settings.settings;

public class SmartClockStates {
    public static int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE;
    public static int MY_PERMISSIONS_REQUEST_PROCESS_OUTGOING_CALLS;
    public static int MY_PERMISSIONS_REQUEST_PROCESS_WAKE_LOCK;
    public static String CHANNEL_ID = "VARIATIONS";
    public static int notificationID;
    public static NetSubsystem netInstance;
    private final CompositeDisposable mDisposable = new CompositeDisposable();

    public static void callReceived(Context ctx, String number, Date start)
    {
        Settings.reInitData(ctx);

        if (settings.getSmartPlugHost() != null)
        {
            ExecutorService execSingle = Executors.newSingleThreadExecutor();
            execSingle.submit(() -> {
                try {
                    NetCommand.powerOn(InetAddress.getByName(settings.getSmartPlugHost()), settings.getSmartPlugPort(), 5000);
                    boolean Result = NetCommand.isPowerOn(InetAddress.getByName(settings.getSmartPlugHost()), settings.getSmartPlugPort(), 5000);
                    sendNotificationPhone(ctx, Boolean.toString(Result), start);
                }
                catch (Exception excErr)
                {
                    Log.e("ERR", "run: powerOn", excErr);
                }
            });
        }
    }

    public static void sendNotificationPhone(Context ctx, String number, Date start)
    {
        Resources res = ctx.getResources();
        String phoneCallAt = res.getString(R.string.phone_call_at);
        String plugState = res.getString(R.string.plugOn);
        if (!"true".equalsIgnoreCase(number))
        {
            plugState = res.getString(R.string.powerOff);
        }
        DateFormat fmt = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_plug)
                .setContentTitle(res.getString(R.string.phone_called))
                .setContentText(String.format(phoneCallAt, fmt.format(start),plugState))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(ctx);

        NotifyEntity newNotifyEntity = new NotifyEntity();
        newNotifyEntity.setNotifyID(Calendar.getInstance().getTimeInMillis());
        newNotifyEntity.setNotify_text(String.format(phoneCallAt, fmt.format(start),plugState));
        newNotifyEntity.setNotify_title(res.getString(R.string.phoneCalled));
        Completable completableAction = NotifyDatabase.getInstance(ctx).notifyDao().insertNotify(newNotifyEntity);
        completableAction.blockingAwait();
// notificationId is a unique int for each notification that you must define
        notificationManager.notify(notificationID, builder.build());
    }


    public static Notification buildNotificationAlarm(Context ctx, Date start)
    {
        Resources res = ctx.getResources();
        String phoneCallAt = res.getString(R.string.alarm_call);
        DateFormat fmt = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_alarmclock)
                .setContentTitle(res.getString(R.string.phone_called))
                .setContentText(String.format(phoneCallAt, fmt.format(start)))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(ctx);

// notificationId is a unique int for each notification that you must define
        return builder.build();
    }
}
