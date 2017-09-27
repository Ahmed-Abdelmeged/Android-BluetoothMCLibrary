/*
 * Copyright (c) 2017 Ahmed-Abdelmeged
 *
 * github: https://github.com/Ahmed-Abdelmeged
 * email: ahmed.abdelmeged.vm@gamil.com
 * Facebook: https://www.facebook.com/ven.rto
 * Twitter: https://twitter.com/A_K_Abd_Elmeged
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

package com.ahmedabdelmeged.bluetoothmc.util;


public final class BluetoothStates {

    //Intent request code
    public static final int REQUEST_CONNECT_DEVICE = 2653;
    public static final int REQUEST_ENABLE_BT = 3569;

    //Messages type send to the bluetooth handler
    public static final int BLUETOOTH_CONNECTING = 20;
    public static final int BLUETOOTH_CONNECTED = 21;
    public static final int BLUETOOTH_CONNECTION_FAILED = 22;
    public static final int BLUETOOTH_CONNECTION_LOST = 23;
    public static final int BLUETOOTH_LISTENING = 101;

    //Messages errors send to the bluetooth handler
    public static final int ERROR_SEND = 600;
    public static final int ERROR_LISTEN = 601;
    public static final int ERROR_DISCONNECT = 602;
    public static final int ERROR_COMMUNICATION = 603;

    /**
     * Return Intent extra (The device MAC address)
     */
    public static String EXTRA_DEVICE_ADDRESS = "device_address";

}
