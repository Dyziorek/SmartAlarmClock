package com.example.smartalarmclock;

import android.Manifest;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.LocaleList;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.smartalarmclock.classes.LocaleChanger;
import com.example.smartalarmclock.classes.SharedAppCompatActivity;
import com.example.smartalarmclock.helper.Settings;
import com.example.smartalarmclock.netCode.NetCommand;
import com.example.smartalarmclock.netCode.NetSubsystem;
import com.example.smartalarmclock.service.OnAlarmReceiver;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.ActionBar;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.time.LocalDateTime;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalField;
import java.util.Calendar;
import java.util.Locale;

import static com.example.smartalarmclock.helper.Settings.settings;

public class MainActivity extends SharedAppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);

        if (SmartClockStates.netInstance == null) {
            if (settings.getMonitorCalls() && settings.getSmartPlugHost() != null) {
                SmartClockStates.netInstance = NetSubsystem.getInstance(settings.getSmartPlugPort(), settings.getSmartPlugHost());
            }
        }
        final Button buttonOn = findViewById(R.id.powerOn);
        buttonOn.setOnClickListener( v -> powerOn());
        final Button buttonOff = findViewById(R.id.powerOff);
        buttonOff.setOnClickListener( v -> powerOff());

        checkPermissions();
        createNotificationChannel();

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        ActionBar barItem = this.getSupportActionBar();
        if (barItem != null) {
            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
            NavigationUI.setupWithNavController(navView, navController);
        }
        settings.setPrefLanguage("pl");
        LocaleChanger.setLocale(this);

        final TextView monitorStart = findViewById(R.id.editTextStartTime);
        final TextView monitorEnd = findViewById(R.id.editEndTime);

        monitorStart.setText(settings.getMonitorStart());
        monitorEnd.setText(settings.getMonitorEnd());

        final TextView pluginHost = findViewById(R.id.smartPlugHost);
        pluginHost.setText(settings.getSmartPlugHost());
    }

    private void powerOn()
    {
        if (SmartClockStates.netInstance != null && SmartClockStates.netInstance.smartPlugSockets.size() > 0)
            try {
                NetCommand.powerOn(SmartClockStates.netInstance.smartPlugSockets.get(0), 9999, 5000);
            }
            catch (Exception  error)
            {
                Log.e("ACT", "onClick: " + error.getMessage(), error);
            }
    }

    private void powerOff()
    {
        if (SmartClockStates.netInstance != null && SmartClockStates.netInstance.smartPlugSockets.size() > 0)
            try {
                NetCommand.powerOff(SmartClockStates.netInstance.smartPlugSockets.get(0), 9999, 5000);
            }
            catch (Exception  error)
            {
                Log.e("ACT", "onClick: " + error.getMessage(), error);
            }
    }


    public void save()
    {
        final TextView monitorStart = findViewById(R.id.editTextStartTime);
        final TextView monitorEnd = findViewById(R.id.editEndTime);
        final TextView smartPlugHost = findViewById(R.id.smartPlugHost);

        settings.setMonitorStart(monitorStart.getText().toString());
        settings.setMonitorEnd(monitorEnd.getText().toString());

        if (!smartPlugHost.getText().toString().equals(settings.getSmartPlugHost()))
        {
            smartPlugHost.setText(settings.getSmartPlugHost());
        }

        if (SmartClockStates.netInstance == null) {
            if (settings.getMonitorCalls() && settings.getSmartPlugHost() != null) {
                SmartClockStates.netInstance = NetSubsystem.getInstance(settings.getSmartPlugPort(), settings.getSmartPlugHost());
            }
        }
    }

    public void setAlarm()
    {
        if (settings.isAlarmSet() > 0) {
            Intent i = new Intent(this, OnAlarmReceiver.class);
            PendingIntent pi = PendingIntent.getBroadcast(this, 0, i,
                    PendingIntent.FLAG_ONE_SHOT);
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.SECOND, calendar.get(Calendar.SECOND) + 10);
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

            Log.e("DEBUG", "setAlarm: call 10 sec later:" + calendar.getTimeInMillis() + ", from alarmSet:" + settings.isAlarmSet());
            Calendar timerCal = Calendar.getInstance();
            timerCal.setTimeInMillis(settings.isAlarmSet());

            Log.e("DEBUG", "setAlarm: call 10 sec later:" + calendar.toString() + ", from alarmSet:" +  timerCal.toString());
            alarmManager.set(AlarmManager.RTC_WAKEUP, settings.isAlarmSet(), pi);
        }
        else {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent i = new Intent(this, OnAlarmReceiver.class);
            PendingIntent pi = PendingIntent.getBroadcast(this, 0, i,
                    PendingIntent.FLAG_ONE_SHOT);
            alarmManager.cancel(pi);
        }
    }

    @Override
    protected void attachBaseContext(Context base)
    {
        super.attachBaseContext(LocaleChanger.onAttach(base));
    }

    private void checkPermissions()
    {
        if (getApplicationContext().checkSelfPermission(Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission has not been granted, therefore prompt the user to grant permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PHONE_STATE},
                    SmartClockStates.MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
        }

        if (getApplicationContext().checkSelfPermission(Manifest.permission.PROCESS_OUTGOING_CALLS)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission has not been granted, therefore prompt the user to grant permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.PROCESS_OUTGOING_CALLS},
                    SmartClockStates.MY_PERMISSIONS_REQUEST_PROCESS_OUTGOING_CALLS);
        }

        if (getApplicationContext().checkSelfPermission(Manifest.permission.WAKE_LOCK)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission has not been granted, therefore prompt the user to grant permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WAKE_LOCK},
                    SmartClockStates.MY_PERMISSIONS_REQUEST_PROCESS_WAKE_LOCK);
        }



    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel(SmartClockStates.CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


}