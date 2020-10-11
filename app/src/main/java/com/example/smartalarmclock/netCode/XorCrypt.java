package com.example.smartalarmclock.netCode;

import android.util.Log;

import java.nio.ByteBuffer;

public class XorCrypt {

    public static byte[] encryptStr(String input)
    {
        return encrypt(input.getBytes());
    }

    public static String decryptStr(byte[] input)
    {
        byte[] decrypted = decrypt(input);
        try
        {
            return new String(decrypted, "UTF-8");
        }
        catch (Exception anyErr)
        {
            Log.e("error in decrypt", anyErr.getMessage(), anyErr);
        }
        return "";
    }

    public static byte[] encrypt(byte[] input)
    {
        ByteBuffer outputData = ByteBuffer.allocate(4 + input.length).putInt(input.length);
        if (input != null && input.length > 0)
        {
            int seed = -85;
            for (int idx = 0; idx < input.length; idx++)
            {
                byte xor = (byte) (seed ^ input[idx]);
                seed = xor;
                outputData.put(xor);
            }
        }

        return outputData.array();
    }

    public static byte[] decrypt(byte[] input)
    {
        if (input != null && input.length > 0)
        {
            int seed = -85;
            for (int idx = 0; idx < input.length; idx++)
            {
                byte xor = (byte) (seed ^ input[idx]);
                seed = input[idx];
                input[idx] = xor;
            }
        }

        return input;
    }
}
