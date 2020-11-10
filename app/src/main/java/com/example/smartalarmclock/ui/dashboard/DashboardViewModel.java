package com.example.smartalarmclock.ui.dashboard;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.smartalarmclock.R;
import com.example.smartalarmclock.SmartClockStates;
import com.example.smartalarmclock.helper.Result;
import com.example.smartalarmclock.helper.WorkCallBack;

import org.json.JSONObject;

import java.net.InetAddress;

public class DashboardViewModel extends ViewModel {

    private MutableLiveData<Boolean> enableMonitor;
    private MutableLiveData<String> smartPlugIP;
    private MutableLiveData<String> info;
    private final DashboardRepository dashboardRepository;

    public DashboardViewModel(DashboardRepository repository) {
        dashboardRepository = repository;
        enableMonitor = new MutableLiveData<>();
        smartPlugIP = new MutableLiveData<>();
        info = new MutableLiveData<>();
        if (dashboardRepository.isInitialized())
        {
            enableMonitor.setValue(dashboardRepository.isMonitorCalls());
            smartPlugIP.setValue(dashboardRepository.getInitIP());
        }
        else
        {
            enableMonitor.setValue(false);
            smartPlugIP.setValue("0.0.0.0");
        }
    }

    public LiveData<Boolean> getMonitor() {
        return enableMonitor;
    }

    public LiveData<String> getInformation() { return info;}

    public void updateMonitor(Boolean value)
    {
        enableMonitor.setValue(value);
    }

    public LiveData<String> getSmartPlugIP()
    {
        return smartPlugIP;
    }

    public void updateSmartPlugIP(String plugIP)
    {
        smartPlugIP.setValue(plugIP);
        SmartClockStates.netInstance = null;
    }

    public void getSmartPlugHost(int port)
    {
        Log.i("INFO", "getSmartPlugHost: started");
        dashboardRepository.asyncFindSmartPlugHost(port, (result) -> updateDataCallback(result));
    }

    public void getAsyncInfo()
    {
        dashboardRepository.asyncGetInfo( (result -> {
            try {
                if (result instanceof Result.Success) {
                    String jsonString = "{" + ((Result.Success<String>) result).data;
                    JSONObject parsedObj = new JSONObject(jsonString);
                    JSONObject objSysInfo = parsedObj.getJSONObject("system").getJSONObject("get_sysinfo");
                    String hw = objSysInfo.getString("hw_ver");
                    String name = objSysInfo.getString("alias");
                    Boolean active = objSysInfo.getInt("relay_state") == 0 ? false : true;

                    info.postValue(hw+","+name+","+active);
                } else {
                    info.postValue(Integer.toString(R.string.unknown_info));
                }
            }
            catch (Exception error)
            {
                info.postValue(Integer.toString(R.string.unknown_info));
            }
        }));
    }

    private void updateDataCallback(Result<InetAddress> result)
    {
        if (result instanceof Result.Success)
        {
            smartPlugIP.postValue(((Result.Success<InetAddress>) result).data.toString());
        }
        else
        {
            smartPlugIP.postValue(Integer.toString(R.string.error_find_smartPlug));
        }
    }
}