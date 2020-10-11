package com.example.smartalarmclock.netCode;

import java.net.InetAddress;
import java.util.concurrent.Callable;

public class CallInetAddress implements Callable<InetAddress> {
        final int hostNumber;
        final InetAddress inetAddress;
        public CallInetAddress(InetAddress netAddress,  int number)
        {
            hostNumber = number;
            inetAddress = netAddress;
        }

        public InetAddress call() {
            try {
                byte[] ipv;
                ipv = inetAddress.getAddress();
                ipv[3] = (byte)hostNumber;
                InetAddress address = InetAddress.getByAddress(ipv);
                String output = address.toString().substring(1);
                if (address.isReachable(500)) {
                    return address;
                } else {
                    return null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
}
