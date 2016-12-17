package com.example.jaipunjwani.androidpcbluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.ParcelUuid;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.bluetooth.BluetoothAdapter;
import android.widget.TextView;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;


public class BluetoothActivity extends AppCompatActivity {


    private static final int REQUEST_ENABLE_BT = 1; // must be an integer greater than 1
    //public static final UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); // standard UUID for serial port
    public static final UUID uuid = UUID.fromString("00001115-0000-1000-8000-00805f9b34fb");
    public static final UUID uuid2 = UUID.fromString("00000000-0000-1000-8000-00805f9b34fb");
    public static final String MY_DEVICE = "DESKTOP-M86RFA1";

    private BluetoothAdapter bluetoothAdapter;
    private TextView bluetoothStatusTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        bluetoothStatusTextView = (TextView) findViewById(R.id.bluetoothDevicesTextView);


        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            scanBluetoothDevices();
        }
    }

    private void scanBluetoothDevices()  {
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        // If there are paired devices
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
                String name = device.getName();
                ParcelUuid[] uuids = device.getUuids(); // to be used to obtain appropriate UUID


                // connect to my device
                if (name.equals(MY_DEVICE)) {
                    bluetoothStatusTextView.append("Device found: " + name);
                    String MACAddress = device.getAddress();
                    BluetoothDevice d = bluetoothAdapter.getRemoteDevice(MACAddress);

                    // starts new thread to connect to device
                    new BluetoothConnectionThread(d).start();

                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_OK) {
            scanBluetoothDevices();
        }
    }
}

/**
 * Thread class to handle bluetooth connection
 */
class BluetoothConnectionThread extends Thread {

    BluetoothSocket socket;
    BluetoothDevice device;

    public BluetoothConnectionThread(BluetoothDevice dev) {
        device =   dev;
        BluetoothSocket tmp = null;
        try {
            tmp = device.createInsecureRfcommSocketToServiceRecord(BluetoothActivity.uuid2);

        } catch(IOException e) {

        }

        socket = tmp;
    }

    public void run() {
        try {
            // Connect the device through the socket. This will block
            // until it succeeds or throws an exception
            socket.connect();
        } catch (IOException connectException) {
            // Unable to connect; close the socket and get out
            try {
                socket.close();
            } catch (IOException closeException) { }
            return;
        }

    }

}
