package com.example.smartalarmclock.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.Log;

import androidx.preference.PreferenceManager;

import com.example.smartalarmclock.R;
import com.example.smartalarmclock.classes.SharedAppCompatActivity;

import java.net.InetAddress;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;


public class Settings {
    public static Settings settings;
    private SharedPreferences saveSharedData;

    public static String KEY_MONITOR_CALLS;
    public static String KEY_MONITOR_START_TIME;
    public static String KEY_MONITOR_END_TIME;
    public static String KEY_MONITOR_DAY_WEEKS;
    public static String KEY_SMARTPLUG_HOST;
    public static String KEY_SMARTPLUG_PORT;
    public static String KEY_PREF_LANGUAGE;


    public Settings(Context appContext)
    {
        saveSharedData = PreferenceManager.getDefaultSharedPreferences(appContext);
        loadStrings(appContext.getResources());
    }

    public static void reInitData(Context context) {
        if (settings == null)
        {
            settings = new Settings(context);
        }
    }

    private void loadStrings(Resources res)
    {
        KEY_MONITOR_CALLS = res.getString(R.string.monitor_calls);
        KEY_MONITOR_END_TIME = res.getString(R.string.monitor_end_time);
        KEY_MONITOR_START_TIME = res.getString(R.string.monitor_start_time);
        KEY_MONITOR_DAY_WEEKS = res.getString(R.string.monitor_days_weeks);
        KEY_SMARTPLUG_HOST = res.getString(R.string.smartplug_host);
        KEY_SMARTPLUG_PORT = res.getString(R.string.smartplug_port);
        KEY_PREF_LANGUAGE = res.getString(R.string.pref_key_language);
    }

    public boolean isAllowedToCall()
    {
        if (getMonitorCalls())
        {
            ZonedDateTime nowZoned = ZonedDateTime.now();
            ZonedDateTime monitorStartTime = LocalTime.parse(getMonitorStart(), DateTimeFormatter.ofPattern("H:mm")).atDate(nowZoned.toLocalDate()).atZone(nowZoned.getZone());
            ZonedDateTime monitorEndTime = LocalTime.parse(getMonitorEnd(), DateTimeFormatter.ofPattern("H:mm")).atDate(nowZoned.toLocalDate()).atZone(nowZoned.getZone());

            // first verify if end time is before start time to adjust end time
            if (Duration.between(monitorStartTime, monitorEndTime).isNegative())
            {
                // end time before start - means it should be next day
                monitorEndTime = monitorEndTime.plusDays(1);
            }
            Duration minutes = Duration.between(monitorStartTime, monitorEndTime);
            if (!Duration.between(monitorStartTime, nowZoned).isNegative() && Duration.between(monitorStartTime, nowZoned).getSeconds() < minutes.getSeconds())
            {
                return true;
            }
        }

        return false;
    }

    public void setMonitorCalls(boolean monitorCalls)
    {
        saveSetting(KEY_MONITOR_CALLS, new Boolean(monitorCalls).toString());
    }

    public boolean getMonitorCalls()
    {
        if (getSettings(KEY_MONITOR_CALLS).isEmpty())
        {
            return false;
        }
        return Boolean.parseBoolean(getSettings(KEY_MONITOR_CALLS));
    }

    public String getMonitorStart()
    {
        if(getSettings(KEY_MONITOR_START_TIME).isEmpty())
        {
            return "";
        }
        else return getSettings(KEY_MONITOR_START_TIME);
    }

    public void setMonitorStart(String timeStart)
    {
        saveSetting(KEY_MONITOR_START_TIME, timeStart);
    }

    public String getMonitorEnd()
    {
        if(getSettings(KEY_MONITOR_END_TIME).isEmpty())
        {
            return "";
        }
        else return getSettings(KEY_MONITOR_END_TIME);
    }

    public void setMonitorEnd(String timeStart)
    {
        saveSetting(KEY_MONITOR_END_TIME, timeStart);
    }

    public int getSmartPlugPort()
    {
        if (getSettings(KEY_SMARTPLUG_PORT).isEmpty())
        {
            return 9999;
        }
        return Integer.parseInt(getSettings(KEY_SMARTPLUG_PORT));
    }

    public void setSmartPlugPort(int port)
    {
        saveSetting(KEY_SMARTPLUG_PORT, new Integer(port).toString());
    }

    public void setSmartPlugHost(String ipHost)
    {
        saveSetting(KEY_SMARTPLUG_HOST, ipHost);
    }

    public String getSmartPlugHost()
    {
        if (getSettings(KEY_SMARTPLUG_HOST).isEmpty())
        {
            return null;
        }
        return getSettings(KEY_SMARTPLUG_HOST);
    }

    public String getPrefLanguage()
    {
        return saveSharedData.getString(KEY_PREF_LANGUAGE, "pl");
    }

    public void setPrefLanguage(String language)
    {
        saveSetting(KEY_PREF_LANGUAGE, language);
    }

    private String getSettings(String settingName)
    {
        return saveSharedData.getString(settingName, "");
    }

    private void saveSetting(String settingName, String value)
    {
        saveSharedData.edit().putString(settingName, value).apply();
    }

    public void registerListener(SharedPreferences.OnSharedPreferenceChangeListener listener)
    {
        saveSharedData.registerOnSharedPreferenceChangeListener(listener);
    }

    public void unregisterListener(SharedPreferences.OnSharedPreferenceChangeListener listener)
    {
        saveSharedData.unregisterOnSharedPreferenceChangeListener(listener);
    }
}
