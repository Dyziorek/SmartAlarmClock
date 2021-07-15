package com.example.smartalarmclock.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<String> alarmTime;
    private MutableLiveData<Boolean> alarmSetup;
    private MutableLiveData<Boolean> debugFlag;

    public HomeViewModel() {
        alarmTime = new MutableLiveData<>();
        alarmSetup = new MutableLiveData<>();
        debugFlag = new MutableLiveData<>();
    }

    public LiveData<String> getAlarmTime() {
        return alarmTime;
    }

    public LiveData<Boolean> getAlarmSet() { return alarmSetup; }

    public void updateAlarmTime(String alarmText) { alarmTime.setValue(alarmText);}

    public void updateAlarmSet(boolean alarmSet) { alarmSetup.setValue(alarmSet);}

    public void updateDebug(boolean debugSwitch) { debugFlag.setValue(debugSwitch);}

    public LiveData<Boolean> getDebugFlag() { return debugFlag; }
}