package com.example.smartalarmclock.netCode;

import android.util.Log;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class NetCommand {

    private static Map<String, String> commands = initCommands();

    private static Map<String, String> initCommands() {
        Map<String, String> commandsInit = new HashMap<>();
        commandsInit.put("info", "{\"system\":{\"get_sysinfo\":{}}}");
        commandsInit.put("on", "{\"system\":{\"set_relay_state\":{\"state\":1}}}");
        commandsInit.put("off", "{\"system\":{\"set_relay_state\":{\"state\":0}}}");
        commandsInit.put("ledoff", "{\"system\":{\"set_led_off\":{\"off\":1}}}");
        commandsInit.put("ledon", "{\"system\":{\"set_led_off\":{\"off\":0}}}");
        commandsInit.put("cloudinfo", "{\"cnCloud\":{\"get_info\":{}}}");
        commandsInit.put("wlanscan", "{\"netif\":{\"get_scaninfo\":{\"refresh\":0}}}");
        commandsInit.put("time", "{\"time\":{\"get_time\":{}}}");
        commandsInit.put("schedule", "{\"schedule\":{\"get_rules\":{}}}");
        commandsInit.put("countdown", "{\"count_down\":{\"get_rules\":{}}}");
        commandsInit.put("antitheft", "{\"anti_theft\":{\"get_rules\":{}}}");
        commandsInit.put("reboot", "{\"system\":{\"reboot\":{\"delay\":1}}}");
        commandsInit.put("reset", "{\"system\":{\"reset\":{\"delay\":1}}}");
        commandsInit.put("energy", "{\"emeter\":{\"get_realtime\":{}}}");

        return commandsInit;
    }

    protected static String command(InetAddress inetAddress, int port, String commandText) throws ConnectException
    {
        try {
            Socket sockConn = new Socket(inetAddress, port);

            InputStream iStream = sockConn.getInputStream();
            OutputStream oStream = sockConn.getOutputStream();
            oStream.write(XorCrypt.encryptStr(commandText));
            byte[] buffer = new byte[2048];
            iStream.read(buffer);
            oStream.close();
            iStream.close();
            return XorCrypt.decryptStr(buffer);
        }
        catch (Exception err)
        {
            Log.e("Error with net sockets", err.getMessage(), err);
        }
        return null;
    }

    public static void powerOn(InetAddress inetAddress, int port) throws ConnectException
    {
        command(inetAddress, port, commands.get("on"));
    }

    public static void powerOff(InetAddress inetAddress, int port) throws ConnectException
    {
        command(inetAddress, port, commands.get("off"));
    }

    public static String info(InetAddress inetAddress, int port) throws ConnectException
    {
        Log.d("NET", "info() called with: inetAddress = [" + inetAddress + "], port = [" + port + "]");
        return command(inetAddress, port, commands.get("info"));
    }
}


