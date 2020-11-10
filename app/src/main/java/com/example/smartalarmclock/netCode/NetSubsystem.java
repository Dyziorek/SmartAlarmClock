package com.example.smartalarmclock.netCode;

import android.util.Log;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static com.example.smartalarmclock.helper.Settings.settings;

public class NetSubsystem {
    public List<InetAddress> smartPlugSockets;
    private int netAddresses;
    private String localAddress;
    private InetAddress linkAddress;
    private int plugPort;


    public static NetSubsystem getInstance(int plugPort)
    {
        return new NetSubsystem(plugPort);
    }

    public static NetSubsystem getInstance(int plugPort, String localAddress)
    {
        return new NetSubsystem(plugPort, localAddress);
    }

    private NetSubsystem(int port, String host)
    {
        plugPort = port;
        localAddress = host;
        smartPlugSockets = new ArrayList<>();
        Runnable exec = new Runnable() {
            @Override
            public void run() {
                int steps = 0;
                    while (steps < 2) {
                        try {
                            if (steps == 0) {
                                steps++;
                                smartPlugSockets.add(InetAddress.getByName(host));
                                break;
                            } else {
                                smartPlugSockets.add(InetAddress.getLocalHost());
                                steps++;
                            }
                        }
                        catch (UnknownHostException noHost)
                        {
                            Log.e("NET", "run: All Intets not works");
                        }
                    }
            }
        };
        Executors.newSingleThreadExecutor().submit(exec);
    }

    private NetSubsystem(int port)
    {
        plugPort = port;
        detectSubNetMask();
        getNetworkIPs();

    }

    private void detectSubNetMask()
    {

        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();

                if (networkInterface.isLoopback())
                    continue; // Don't want to broadcast to the loopback interface

                for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
                    InetAddress broadcast = interfaceAddress.getBroadcast();

                    // Android seems smart enough to set to null broadcast to
                    //  the external mobile network. It makes sense since Android
                    //  silently drop UDP broadcasts involving external mobile network.
                    if (broadcast == null)
                        continue;

                    linkAddress = interfaceAddress.getAddress();
                    netAddresses = interfaceAddress.getNetworkPrefixLength(); //' is another way to express subnet mask
                    break;
                }
            }
        }
        catch (Exception errExc)
        {
            Log.e("NET", "detectSubNetMask: " + errExc.getMessage(), errExc);
        }
    }

    public void getNetworkIPs() {
        ExecutorService pool = Executors.newFixedThreadPool(10);
        final byte[] ip;
        try {
            List<Future<InetAddress>> futures = new ArrayList<>();
            for(int i=1;i<=254 ;i++) {
                CallInetAddress call = new CallInetAddress(linkAddress, i);
                Future<InetAddress> callReturns = pool.submit(call);
                futures.add(callReturns);
            }

            List<InetAddress> localHosts = new ArrayList<>();
            Log.d("DEBUG", "getNetworkIPs futures: " + futures.size());
            try {
                for(Future<InetAddress> threadReturns : futures)
                {
                    if (threadReturns.get() != null) {
                        localHosts.add(threadReturns.get());
                    }
                }
            } catch (InterruptedException | ExecutionException e) {
                Log.e("NET", "getNetworkIPs: " + e.getMessage(), e);
            }
            Log.d("DEBUG", "getNetworkIPs localhosts: " + localHosts.size());
            for (InetAddress possible : localHosts)
            {
                try {
                    Future<String> returnInfo = NetCommand.info(possible, plugPort, 0);
                    if (returnInfo != null && !returnInfo.isCancelled()) {
                        if (!returnInfo.get().isEmpty()) {
                            if (smartPlugSockets == null) {
                                smartPlugSockets = new ArrayList<>();
                            }
                            smartPlugSockets.add(possible);
                        }
                    }
                }
                catch (Exception error)
                {
                    Log.i("NET", "getNetworkIPs not a plug: " + possible.toString() + ", " + error.getMessage(), error);
                }
            }
            Log.d("DEBUG", "getNetworkIPs cycle ended");
        }
        catch (Exception exc) {
            Log.i("NET", "getNetworkIPs not a plug: " + exc.getMessage(), exc);
        }
        finally {
            if (smartPlugSockets != null)
                Log.d("DEBUG", "getNetworkIPs gotSmarts: " + smartPlugSockets.size());
            else
            {
                Log.d("DEBUG", "getNetworkIPs noSmartPlugs: ");
            }
        }

    }

    public List<InetAddress> getSmartPlugSockets() {
        return smartPlugSockets;
    }


}
