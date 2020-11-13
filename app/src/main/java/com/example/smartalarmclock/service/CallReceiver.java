package com.example.smartalarmclock.service;

import android.content.Context;

import com.example.smartalarmclock.SmartClockStates;

import java.util.Date;

import static com.example.smartalarmclock.helper.Settings.settings;

public class CallReceiver extends PhoneCallReceiver {
    @Override
    protected void onIncomingCallReceived(Context ctx, String number, Date start) {
        if (settings.isAllowedToCall())
        {
            SmartClockStates.callReceived(ctx, number, start);
            SmartClockStates.sendNotification(ctx, number, start);
        }
    }

    @Override
    protected void onIncomingCallAnswered(Context ctx, String number, Date start) {

    }

    @Override
    protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end) {

    }

    @Override
    protected void onOutgoingCallStarted(Context ctx, String number, Date start) {

    }

    @Override
    protected void onOutgoingCallEnded(Context ctx, String number, Date start, Date end) {

    }

    @Override
    protected void onMissedCall(Context ctx, String number, Date start) {

    }
}
