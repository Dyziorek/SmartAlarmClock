package com.example.smartalarmclock.netCode;

import android.util.Log;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class NetSubsystem {
    public List<InetAddress> smartPlugSockets;
    private int netAddresses;
    private InetAddress localAddress;
    private int plugPort;

    public NetSubsystem(int port)
    {
        detectSubNetMask();
        getNetworkIPs();
        plugPort = port;
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

                    localAddress = interfaceAddress.getAddress();
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

        } catch (Exception e) {
            return;     // exit method, otherwise "ip might not have been initialized"
        }

        List<Future<InetAddress>> futures = new ArrayList<>();

        for(int i=1;i<=254 ;i++) {
            CallInetAddress call = new CallInetAddress(localAddress, i);
            Future<InetAddress> callReturns = pool.submit(call);
            futures.add(callReturns);
        }

        List<InetAddress> localHosts = new ArrayList<>();

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

        for (InetAddress possible : localHosts)
        {
            try {
                String returnInfo = NetCommand.info(possible, plugPort);
                if (returnInfo != null && !returnInfo.isEmpty()) {
                    smartPlugSockets.add(possible);
                }
            }
            catch (Exception error)
            {
                Log.i("NET", "getNetworkIPs not a plug: " + possible.toString() + ", " + error.getMessage(), error);
            }
        }

    }

    public List<InetAddress> getSmartPlugSockets() {
        return smartPlugSockets;
    }
}
