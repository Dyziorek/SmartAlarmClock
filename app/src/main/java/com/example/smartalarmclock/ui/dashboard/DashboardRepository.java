package com.example.smartalarmclock.ui.dashboard;

import android.util.Log;
import android.widget.Toast;

import com.example.smartalarmclock.SmartClockStates;
import com.example.smartalarmclock.helper.Result;
import com.example.smartalarmclock.helper.Settings;
import com.example.smartalarmclock.helper.WorkCallBack;
import com.example.smartalarmclock.netCode.NetCommand;
import com.example.smartalarmclock.netCode.NetSubsystem;

import java.net.InetAddress;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class DashboardRepository {

    private InetAddress smartPlugHost;
    private final Executor executor;
    private Settings localSetup;

    public DashboardRepository(Executor exec, Settings setupData){
        executor = exec;
        localSetup = setupData;
    }

    public String getInitIP()
    {
        return localSetup.getSmartPlugHost().toString();
    }

    public Boolean isMonitorCalls()
    {
        return localSetup.getMonitorCalls();
    }

    public Boolean isInitialized()
    {
        return localSetup != null && localSetup.getSmartPlugHost() != null && !localSetup.getSmartPlugHost().isEmpty();
    }

    public Result<InetAddress> findSmartPlugHost(int portNum)
    {
        try
        {
            NetSubsystem netData = NetSubsystem.getInstance(portNum);
            if (netData.getSmartPlugSockets() != null && !netData.getSmartPlugSockets().isEmpty())
            {
                return new Result.Success<>(netData.smartPlugSockets.get(0));
            }
        }
        catch (Exception error)
        {
            return new Result.Error<>(error);
        }
        return new Result.Error<>(new Exception("empty Data"));
    }

    public void asyncFindSmartPlugHost(final int portNum, final WorkCallBack<InetAddress> callback)
    {
        executor.execute(() -> {
            try {
                Result<InetAddress> result = findSmartPlugHost(portNum);
                callback.onCompleted(result);
            } catch (Exception err) {
                Result<InetAddress> errorData = new Result.Error<>(err);
                callback.onCompleted(errorData);
            }
        });
    }


    public void asyncGetInfo(final WorkCallBack<String> callback)
    {
        executor.execute(() -> {
            try {

            if (SmartClockStates.netInstance != null && SmartClockStates.netInstance.smartPlugSockets.size() > 0) {
                Future<String> text = NetCommand.info(SmartClockStates.netInstance.smartPlugSockets.get(0), 9999, 5000);
                callback.onCompleted(new Result.Success<>(text.get(5000, TimeUnit.MILLISECONDS)));
            }
            } catch (Exception err) {
                Result<String> errorData = new Result.Error<>(err);
                callback.onCompleted(errorData);
            }
        });
    }
}
