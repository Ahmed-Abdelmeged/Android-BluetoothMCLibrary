package com.ahmedabdelmeged.bluetoothmc.util

/**
 * @author Ahmed Abd-Elmeged
 */
object BluetoothStates {

    //Intent request code
    const val REQUEST_CONNECT_DEVICE = 2653
    const val REQUEST_ENABLE_BT = 3569

    //Messages type send to the bluetooth handler
    const val BLUETOOTH_CONNECTING = 20
    const val BLUETOOTH_CONNECTED = 21
    const val BLUETOOTH_CONNECTION_FAILED = 22
    const val BLUETOOTH_CONNECTION_LOST = 23
    const val BLUETOOTH_LISTENING = 101

    //Messages errors send to the bluetooth handler
    const val ERROR_SEND = 600
    const val ERROR_LISTEN = 601
    const val ERROR_DISCONNECT = 602
    const val ERROR_COMMUNICATION = 603

    /**
     * Return Intent extra (The device MAC address)
     */
    var EXTRA_DEVICE_ADDRESS = "device_address"

}