package com.rbnquintero.personal.mygoproapp.service;

import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import com.rbnquintero.personal.mygoproapp.service.delegate.GoProWakeUpServiceDelegate;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by rbnquintero on 2/15/16.
 */
public class GoProWakeUpService extends AsyncTask<String, Void, Boolean> {
    private String TAG = this.getClass().getSimpleName();
    private GoProWakeUpServiceDelegate delegate;

    private final Handler handler = new Handler();

    // Service configuration
    private static final long timeout = 5000;
    private static final String broadcastIP = "224.0.0.251";
    private static final int PORT = 9;

    public GoProWakeUpService(GoProWakeUpServiceDelegate delegate) {
        this.delegate = delegate;
    }

    @Override
    protected Boolean doInBackground(String... params) {
        Boolean success = false;
        long initialTime = System.currentTimeMillis();
        while(!success && (System.currentTimeMillis() - initialTime) < timeout) {
            success = sendWOLPacket(params[0]);
            if (!success) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Log.e(TAG, "Could not sleep thread", e);
                }
            }
        }
        return success;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if(result) {
            delegate.wakeUpSuccess();
        } else {
            delegate.wakeUpError();
        }
    }

    public Boolean sendWOLPacket(String macAddress) {
        if (broadcastIP == null || broadcastIP.isEmpty() || macAddress == null || macAddress.isEmpty()) {
            Log.d(TAG, "Usage: java GoProWakeUpService <broadcast-ip> <mac-address>");
            Log.d(TAG, "Example: java GoProWakeUpService 192.168.0.255 00:0D:61:08:22:4A");
            Log.d(TAG, "Example: java GoProWakeUpService 192.168.0.255 00-0D-61-08-22-4A");
            return false;
        }

        try {
            byte[] macBytes = getMacBytes(macAddress);
            byte[] bytes = new byte[6 + 16 * macBytes.length];
            for (int i = 0; i < 6; i++) {
                bytes[i] = (byte) 0xff;
            }
            for (int i = 6; i < bytes.length; i += macBytes.length) {
                System.arraycopy(macBytes, 0, bytes, i, macBytes.length);
            }

            InetAddress address = InetAddress.getByName(broadcastIP);
            DatagramPacket packet = new DatagramPacket(bytes, bytes.length, address, PORT);
            DatagramSocket socket = new DatagramSocket();
            socket.send(packet);
            socket.close();

            Log.d(TAG, "Wake-on-LAN packet sent.");
            return true;
        }
        catch (Exception e) {
            Log.e("Error", e.getLocalizedMessage(), e);
            System.out.println("Failed to send Wake-on-LAN packet: + e");
            return false;
        }
    }

    private static byte[] getMacBytes(String macStr) throws IllegalArgumentException {
        byte[] bytes = new byte[6];
        String[] hex = macStr.split("(\\:|\\-)");
        if (hex.length != 6) {
            throw new IllegalArgumentException("Invalid MAC address.");
        }
        try {
            for (int i = 0; i < 6; i++) {
                bytes[i] = (byte) Integer.parseInt(hex[i], 16);
            }
        }
        catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid hex digit in MAC address.");
        }
        return bytes;
    }
}
