package com.ahmedabdelmeged.bluetoothmc

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.os.AsyncTask
import android.os.Handler
import com.ahmedabdelmeged.bluetoothmc.util.BluetoothStates
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.*

/**
 * @author Ahmed Abd-Elmeged
 */
class BluetoothMC(val bufferSize: Int = 1024) {

    /**
     * the MAC address for the chosen device
     */
    private var devicesAddress: String? = null

    private var myBluetoothAdapter: BluetoothAdapter? = null
    private var btSocket: BluetoothSocket? = null
    private var isBtConnected = false

    private var mOnDataReceivedListener: onDataReceivedListener? = null
    private var mBluetoothConnectionListener: BluetoothConnectionListener? = null
    private var mBluetoothErrorsListener: BluetoothErrorsListener? = null

    private var newConnectionFlag = 0

    init {
        if (bufferSize == 0) {
            throw IllegalStateException("Buffer size can't be zero")
        }

        newConnectionFlag++

        //get the mobile bluetooth device
        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        HandleBluetoothStates()
    }

    /**
     * Interface for listen to the incoming data
     */
    interface onDataReceivedListener {
        fun onDataReceived(data: String)
    }

    /**
     * Interface for track the connection states
     */
    interface BluetoothConnectionListener {

        fun onDeviceConnecting()

        fun onDeviceConnected()

        fun onDeviceDisconnected()

        fun onDeviceConnectionFailed()
    }

    /**
     * Interface for track the errors
     */
    interface BluetoothErrorsListener {
        fun onSendingFailed()

        fun onReceivingFailed()

        fun onDisconnectingFailed()

        fun onCommunicationFailed()
    }

    fun setOnDataReceivedListener(onDataReceivedListener: onDataReceivedListener) {
        mOnDataReceivedListener = onDataReceivedListener
    }

    fun setOnBluetoothConnectionListener(bluetoothConnectionListener: BluetoothConnectionListener) {
        mBluetoothConnectionListener = bluetoothConnectionListener
    }

    fun setOnBluetoothErrorsListener(bluetoothErrorsListener: BluetoothErrorsListener) {
        mBluetoothErrorsListener = bluetoothErrorsListener
    }

    fun isBluetoothEnabled(): Boolean {
        return myBluetoothAdapter != null && myBluetoothAdapter?.isEnabled ?: false
    }

    fun isBluetoothAvailable(): Boolean {
        return myBluetoothAdapter != null
    }

    fun getBluetoothAdapter(): BluetoothAdapter? {
        return myBluetoothAdapter
    }

    fun enableBluetooth() {
        if (myBluetoothAdapter != null) {
            myBluetoothAdapter?.enable()
        }
    }

    /**
     * used to connect the the bluetooth device with it's address
     *
     * @param intent
     */
    fun connect(intent: Intent) {
        devicesAddress = intent.getStringExtra(BluetoothStates.EXTRA_DEVICE_ADDRESS)
        if (devicesAddress != null) {
            //call the class to connect to bluetooth
            if (newConnectionFlag == 1) {
                ConnectBT().execute()
            }
        }
    }

    /**
     * Helper method to handle bluetooth states
     */
    private fun HandleBluetoothStates() {
        bluetoothHandler = object : Handler() {
            override fun handleMessage(msg: android.os.Message) {
                //handle messages
                when (msg.what) {
                    BluetoothStates.BLUETOOTH_CONNECTING -> mBluetoothConnectionListener?.onDeviceConnecting()
                    BluetoothStates.BLUETOOTH_CONNECTED -> mBluetoothConnectionListener?.onDeviceConnected()
                    BluetoothStates.BLUETOOTH_CONNECTION_FAILED -> mBluetoothConnectionListener?.onDeviceConnectionFailed()
                    BluetoothStates.BLUETOOTH_CONNECTION_LOST -> mBluetoothConnectionListener?.onDeviceDisconnected()
                    BluetoothStates.BLUETOOTH_LISTENING -> {
                        // msg.arg1 = bytes from connect thread
                        val readBuf = msg.obj as ByteArray
                        val readMessage = String(readBuf, 0, msg.arg1)
                        mOnDataReceivedListener?.onDataReceived(readMessage)
                    }
                    BluetoothStates.ERROR_DISCONNECT -> mBluetoothErrorsListener?.onDisconnectingFailed()
                    BluetoothStates.ERROR_LISTEN -> mBluetoothErrorsListener?.onReceivingFailed()
                    BluetoothStates.ERROR_SEND -> mBluetoothErrorsListener?.onSendingFailed()
                    BluetoothStates.ERROR_COMMUNICATION -> mBluetoothErrorsListener?.onCommunicationFailed()
                }
            }
        }
    }

    /**
     * An AysncTask to connect to Bluetooth socket
     */
    private inner class ConnectBT : AsyncTask<Void, Void, Void>() {
        private var connectSuccess = true

        override fun onPreExecute() {}

        //while the progress dialog is shown, the connection is done in background
        override fun doInBackground(vararg params: Void): Void? {

            try {
                if (btSocket == null || !isBtConnected) {
                    bluetoothHandler?.obtainMessage(BluetoothStates.BLUETOOTH_CONNECTING)?.sendToTarget()

                    //connects to the device's address and checks if it's available
                    val bluetoothDevice = myBluetoothAdapter?.getRemoteDevice(devicesAddress)

                    //create a RFCOMM (SPP) connection
                    btSocket = bluetoothDevice?.createInsecureRfcommSocketToServiceRecord(myUUID)

                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery()

                    //start connection
                    btSocket?.connect()
                }

            } catch (e: IOException) {
                //if the try failed, you can check the exception here
                connectSuccess = false
            }

            return null
        }

        //after the doInBackground, it checks if everything went fine
        override fun onPostExecute(aVoid: Void) {
            super.onPostExecute(aVoid)
            if (!connectSuccess) {
                bluetoothHandler?.obtainMessage(BluetoothStates.BLUETOOTH_CONNECTION_FAILED)?.sendToTarget()
            } else {
                isBtConnected = true
                bluetoothHandler?.obtainMessage(BluetoothStates.BLUETOOTH_CONNECTED)?.sendToTarget()
                mConnectedThread = ConnectedThread(btSocket!!)
                mConnectedThread?.start()
            }
        }
    }

    /**
     * to disconnect the bluetooth connection
     */
    fun disconnect() {
        if (btSocket != null)
        //If the btSocket is busy
        {
            try {
                btSocket!!.close() //close connection
                isBtConnected = false
                bluetoothHandler?.obtainMessage(BluetoothStates.BLUETOOTH_CONNECTION_LOST)?.sendToTarget()
            } catch (e: IOException) {
                bluetoothHandler?.obtainMessage(BluetoothStates.ERROR_DISCONNECT)?.sendToTarget()
            }

        }
    }

    /**
     * create a new class for connect thread
     * to send and read DataSend from the microcontroller
     */
    private inner class ConnectedThread//Create the connect thread
    internal constructor(socket: BluetoothSocket) : Thread() {
        private val mmInputStream: InputStream?
        private val mmOutputStream: OutputStream?

        init {

            var tmpIn: InputStream? = null
            var tmpOut: OutputStream? = null

            try {
                //create I/O stream for the connection
                tmpIn = socket.inputStream
                tmpOut = socket.outputStream
            } catch (e: IOException) {
                bluetoothHandler?.obtainMessage(BluetoothStates.ERROR_COMMUNICATION)?.sendToTarget()
            }

            mmInputStream = tmpIn
            mmOutputStream = tmpOut

        }

        override fun run() {
            val buffer = ByteArray(bufferSize)
            var bytes: Int

            //keep looping for listen for received message
            while (true) {
                try {
                    //read bytes from input buffer
                    bytes = mmInputStream!!.read(buffer)
                    // Send the obtained bytes to the UI Activity via handler
                    bluetoothHandler?.obtainMessage(BluetoothStates.BLUETOOTH_LISTENING, bytes, -1, buffer)
                            ?.sendToTarget()
                } catch (e: IOException) {
                    bluetoothHandler?.obtainMessage(BluetoothStates.ERROR_LISTEN)?.sendToTarget()
                    break
                }

            }
        }

        //write method
        internal fun write(input: String) {
            //converted entered string into bytes
            val msgBuffer = input.toByteArray()

            //write bytes over bluetooth connection  via outstream
            try {
                mmOutputStream!!.write(msgBuffer)
            } catch (e: IOException) {
                bluetoothHandler?.obtainMessage(BluetoothStates.ERROR_SEND)?.sendToTarget()
            }

        }
    }

    /**
     * used to send data to the micro controller
     *
     * @param data the data that will send prefer to be one char
     */
    fun send(data: String) {
        if (btSocket != null && mConnectedThread != null) {
            mConnectedThread?.write(data)
        }
    }

    companion object {
        /**
         * Tag for the log (Debugging)
         */
        private val LOG_TAG = BluetoothMC::class.java.simpleName

        //SPP UUID. Look for it'
        //This the SPP for the arduino(AVR)
        private val myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

        private var mConnectedThread: ConnectedThread? = null

        /**
         * Handler to get the DataSend from thr thread and drive it to the UI
         */
        private var bluetoothHandler: Handler? = null
    }

}