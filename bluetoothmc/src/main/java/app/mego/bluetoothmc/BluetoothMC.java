/*
 * Copyright (c) 2017 Ahmed-Abdelmeged
 *
 * github: https://github.com/Ahmed-Abdelmeged
 * email: ahmed.abdelmeged.vm@gamil.com
 * Facebook: https://www.facebook.com/ven.rto
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package app.mego.bluetoothmc;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class BluetoothMC {


    /**
     * the MAC address for the chosen device
     */
    private String devicesAddress = null;

    /**
     * Tag for the log (Debugging)
     */
    private static final String LOG_TAG = BluetoothMC.class.getSimpleName();

    private BluetoothAdapter myBluetoothAdapter = null;
    private BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    //SPP UUID. Look for it'
    //This the SPP for the arduino(AVR)
    private static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private int newConnectionFlag = 0;

    private static ConnectedThread mConnectedThread;
    private Context mContext;


    /**
     * Handler to get the DataSend from thr thread and drive it to the UI
     */
    private static Handler bluetoothHandler;


    private onDataReceivedListener mOnDataReceivedListener;
    private BluetoothConnectionListener mBluetoothConnectionListener;
    private BluetoothErrorsListener mBluetoothErrorsListener;


    /**
     * Required public constructor
     */
    public BluetoothMC(Context context) {
        newConnectionFlag++;
        mContext = context;
        //get the mobile bluetooth device
        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        HandleBluetoothStates();
    }

    /**
     * Interface for listen to the incoming data
     */
    public interface onDataReceivedListener {
        void onDataReceived(String data);
    }

    /**
     * Interface for track the connection states
     */
    public interface BluetoothConnectionListener {

        void onDeviceConnecting();

        void onDeviceConnected();

        void onDeviceDisconnected();

        void onDeviceConnectionFailed();
    }

    /**
     * Interface for track the errors
     */
    public interface BluetoothErrorsListener {
        void onSendingFailed();

        void onReceivingFailed();

        void onDisconnectingFailed();

        void onCommunicationFailed();
    }

    public void setOnDataReceivedListener(onDataReceivedListener onDataReceivedListener) {
        mOnDataReceivedListener = onDataReceivedListener;
    }

    public void setOnBluetoothConnectionListener(BluetoothConnectionListener bluetoothConnectionListener) {
        mBluetoothConnectionListener = bluetoothConnectionListener;
    }

    public void setOnBluetoothErrorsListener(BluetoothErrorsListener bluetoothErrorsListener) {
        mBluetoothErrorsListener = bluetoothErrorsListener;
    }

    public boolean isBluetoothEnabled() {
        return myBluetoothAdapter != null && myBluetoothAdapter.isEnabled();
    }

    public boolean isBluetoothAvailable() {
        return myBluetoothAdapter != null;
    }

    public BluetoothAdapter getBluetoothAdapter() {
        return myBluetoothAdapter;
    }

    public void enableBluetooth() {
        if (myBluetoothAdapter != null) {
            myBluetoothAdapter.enable();
        }
    }

    /**
     * used to connect the the bluetooth device with it's address
     *
     * @param intent
     */
    public void connect(Intent intent) {
        devicesAddress = intent.getStringExtra(BluetoothStates.EXTRA_DEVICE_ADDRESS);
        if (devicesAddress != null) {
            //call the class to connect to bluetooth
            if (newConnectionFlag == 1) {
                new ConnectBT().execute();
            }
        }
    }

    /**
     * Helper method to handle bluetooth states
     */
    private void HandleBluetoothStates() {
        bluetoothHandler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                //handle messages
                switch (msg.what) {
                    case BluetoothStates.BLUETOOTH_CONNECTING:
                        if (mBluetoothConnectionListener != null) {
                            mBluetoothConnectionListener.onDeviceConnecting();
                        }
                        break;
                    case BluetoothStates.BLUETOOTH_CONNECTED:
                        if (mBluetoothConnectionListener != null) {
                            mBluetoothConnectionListener.onDeviceConnected();
                        }
                        break;
                    case BluetoothStates.BLUETOOTH_CONNECTION_FAILED:
                        if (mBluetoothConnectionListener != null) {
                            mBluetoothConnectionListener.onDeviceConnectionFailed();
                        }
                        break;
                    case BluetoothStates.BLUETOOTH_CONNECTION_LOST:
                        if (mBluetoothConnectionListener != null) {
                            mBluetoothConnectionListener.onDeviceDisconnected();
                        }
                        break;
                    case BluetoothStates.BLUETOOTH_LISTENING:
                        // msg.arg1 = bytes from connect thread
                        byte[] readBuf = (byte[]) msg.obj;
                        String readMessage = new String(readBuf, 0, msg.arg1);
                        if (mOnDataReceivedListener != null) {
                            mOnDataReceivedListener.onDataReceived(readMessage);
                        }
                        break;
                    case BluetoothStates.ERROR_DISCONNECT:
                        if (mBluetoothErrorsListener != null) {
                            mBluetoothErrorsListener.onDisconnectingFailed();
                        }
                        break;
                    case BluetoothStates.ERROR_LISTEN:
                        if (mBluetoothErrorsListener != null) {
                            mBluetoothErrorsListener.onReceivingFailed();
                        }
                        break;
                    case BluetoothStates.ERROR_SEND:
                        if (mBluetoothErrorsListener != null) {
                            mBluetoothErrorsListener.onSendingFailed();
                        }
                        break;
                    case BluetoothStates.ERROR_COMMUNICATION:
                        if (mBluetoothErrorsListener != null) {
                            mBluetoothErrorsListener.onCommunicationFailed();
                        }
                        break;
                }
            }
        };
    }


    /**
     * An AysncTask to connect to Bluetooth socket
     */
    private class ConnectBT extends AsyncTask<Void, Void, Void> {
        private boolean connectSuccess = true;

        @Override
        protected void onPreExecute() {
        }

        //while the progress dialog is shown, the connection is done in background
        @Override
        protected Void doInBackground(Void... params) {

            try {
                if (btSocket == null || !isBtConnected) {

                    bluetoothHandler.obtainMessage(BluetoothStates.BLUETOOTH_CONNECTING).sendToTarget();

                    //connects to the device's address and checks if it's available
                    BluetoothDevice bluetoothDevice = myBluetoothAdapter.getRemoteDevice(devicesAddress);

                    //create a RFCOMM (SPP) connection
                    btSocket = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(myUUID);

                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();

                    //start connection
                    btSocket.connect();
                }

            } catch (IOException e) {
                //if the try failed, you can check the exception here
                connectSuccess = false;
            }

            return null;
        }

        //after the doInBackground, it checks if everything went fine
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (!connectSuccess) {
                bluetoothHandler.obtainMessage(BluetoothStates.BLUETOOTH_CONNECTION_FAILED).sendToTarget();
            } else {
                isBtConnected = true;
                bluetoothHandler.obtainMessage(BluetoothStates.BLUETOOTH_CONNECTED).sendToTarget();
                mConnectedThread = new ConnectedThread(btSocket);
                mConnectedThread.start();
            }
        }
    }

    /**
     * to disconnect the bluetooth connection
     */
    public void disconnect() {
        if (btSocket != null) //If the btSocket is busy
        {
            try {
                btSocket.close(); //close connection
                isBtConnected = false;
                bluetoothHandler.obtainMessage(BluetoothStates.BLUETOOTH_CONNECTION_LOST).sendToTarget();
            } catch (IOException e) {
                bluetoothHandler.obtainMessage(BluetoothStates.ERROR_DISCONNECT).sendToTarget();
            }
        }
    }

    /**
     * create a new class for connect thread
     * to send and read DataSend from the microcontroller
     */
    private class ConnectedThread extends Thread {
        private final InputStream mmInputStream;
        private final OutputStream mmOutputStream;

        //Create the connect thread
        private ConnectedThread(BluetoothSocket socket) {

            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                //create I/O stream for the connection
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                bluetoothHandler.obtainMessage(BluetoothStates.ERROR_COMMUNICATION).sendToTarget();
            }
            mmInputStream = tmpIn;
            mmOutputStream = tmpOut;

        }

        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;

            //keep looping for listen for received message
            while (true) {
                try {
                    //read bytes from input buffer
                    bytes = mmInputStream.read(buffer);
                    // Send the obtained bytes to the UI Activity via handler
                    bluetoothHandler.obtainMessage(BluetoothStates.BLUETOOTH_LISTENING, bytes, -1, buffer)
                            .sendToTarget();
                } catch (IOException e) {
                    bluetoothHandler.obtainMessage(BluetoothStates.ERROR_LISTEN).sendToTarget();
                    break;
                }
            }
        }

        //write method
        void write(String input) {
            //converted entered string into bytes
            byte[] msgBuffer = input.getBytes();

            //write bytes over bluetooth connection  via outstream
            try {
                mmOutputStream.write(msgBuffer);
            } catch (IOException e) {
                bluetoothHandler.obtainMessage(BluetoothStates.ERROR_SEND).sendToTarget();
            }
        }
    }

    /**
     * used to send data to the micro controller
     *
     * @param data the data that will send prefer to be one char
     */
    public void send(String data) {
        if (btSocket != null) {
            mConnectedThread.write(data);
        }
    }
}
