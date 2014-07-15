package com.nashglover.bluevncclient;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by rglover3 on 7/15/2014.
 */
public class Connection implements Runnable {

    private static final UUID MY_UUID = UUID.fromString("00000001-0000-1000-8000-00805F9B34FB");

    private BluetoothDevice serverDevice = null;
    private BluetoothSocket btSocket = null;
    private OutputStream outStream = null;
    private InputStream inStream = null;
    Handler mainHandler;

    public Connection(Handler _mainHandler) {
        mainHandler = _mainHandler;
    }

    public void setDevice(BluetoothDevice _device) {
        serverDevice = _device;
    }

    public void run() {
        try {
            btSocket = serverDevice.createRfcommSocketToServiceRecord(MY_UUID);
            System.out.println("Connecting to " + serverDevice.getName() + "...");
            btSocket.connect();
            System.out.println("Connected.");
            outStream = btSocket.getOutputStream();
            inStream = btSocket.getInputStream();

            Message msg = mainHandler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString("type", "connected");
            msg.setData(bundle);

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
