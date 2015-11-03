package com.example.android.bluetoothchat;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.util.UUID;

/**
 * CB class specified within tutorial
 * Created by Acer on 11/3/2015.
 */
public class ConnectThread extends Thread {
    private final BluetoothSocket mmSocket;
    private final BluetoothDevice mmDevice;
    UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    public ConnectThread(BluetoothDevice device) {
        mmDevice = device;
        BluetoothSocket tempSocket = null;
        mmSocket = tempSocket;
    }

    @Override
    public void run(){
        mmSocket = createRfcommSocketToServiceRecord(myUUID);

    }

    public void cancel(){

    }
}
