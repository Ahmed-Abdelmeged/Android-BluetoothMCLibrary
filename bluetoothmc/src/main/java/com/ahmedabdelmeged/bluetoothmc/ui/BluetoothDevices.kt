/*
 * Copyright (c) 2019 Ahmed-Abdelmeged
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

package com.ahmedabdelmeged.bluetoothmc.ui

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ahmedabdelmeged.bluetoothmc.R
import com.ahmedabdelmeged.bluetoothmc.ui.adapter.BluetoothDevicesAdapter
import com.ahmedabdelmeged.bluetoothmc.ui.adapter.DeviceClickCallbacks
import com.ahmedabdelmeged.bluetoothmc.util.BluetoothStates
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.ArrayList
import com.ahmedabdelmeged.bluetoothmc.util.BluetoothStates.REQUEST_ENABLE_BT

/**
 * This Activity to connect the app with the device(MicroController)
 * if it's a already paired devices using MAC address(Media Access Control)
 * then send the MAC address to the parent Activity as a intent result
 */
class BluetoothDevices : AppCompatActivity(), DeviceClickCallbacks {

    /**
     * UI Element
     */
    private var searchForNewDevices: FloatingActionButton? = null
    private var deviceRecycler: RecyclerView? = null
    private var searchProgressbar: ProgressBar? = null

    /**
     * The adapter to get all bluetooth services
     */
    private var mBluetoothAdapter: BluetoothAdapter? = null
    private var pairedDevices: Set<BluetoothDevice>? = null

    /**
     * Adapter for the devices list
     */
    private var bluetoothDevicesAdapter: BluetoothDevicesAdapter? = null

    /**
     * The BroadcastReceiver that listens for discovered devices and changes the title when
     * discovery is finished
     */
    private val mReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action

            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND == action) {
                // Get the BluetoothDevice object from the Intent
                val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                // If it's already paired, skip it, because it's been listed already
                if (device.bondState != BluetoothDevice.BOND_BONDED) {

                    if (device.name != null) {
                        bluetoothDevicesAdapter!!.addDevice(device.name + "\n" + device.address)
                    }
                }

            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED == action) {
                searchForNewDevices!!.isEnabled = true
                searchProgressbar!!.visibility = View.GONE
                if (pairedDevices!!.size == bluetoothDevicesAdapter!!.itemCount) {
                    Toast.makeText(this@BluetoothDevices, "No devices found", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /**
     * handle the click for the list view to get the MAC address
     */
    private val bluetoothListClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
        //Get the device MAC address , the last 17 char in the view
        val info = parent.getItemAtPosition(position) as String
        val MACAddress = info.substring(info.length - 17)

        // Create the result Intent and include the MAC address
        val intent = Intent()
        intent.putExtra(BluetoothStates.EXTRA_DEVICE_ADDRESS, MACAddress)

        // Set result and finish this Activity
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bluetooth_devices)
        initializeScreen()

        //check if the device has a bluetooth or not
        //and show Toast message if it does't have
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        //set the bluetooth adapter
        val linearLayoutManager = LinearLayoutManager(
                this, RecyclerView.VERTICAL, false)
        deviceRecycler!!.layoutManager = linearLayoutManager
        deviceRecycler!!.setHasFixedSize(true)
        bluetoothDevicesAdapter = BluetoothDevicesAdapter(ArrayList(), this)
        deviceRecycler!!.adapter = bluetoothDevicesAdapter

        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.does_not_have_bluetooth, Toast.LENGTH_LONG).show()
            finish()
        } else if (!mBluetoothAdapter!!.isEnabled) {
            val enableIntentBluetooth = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableIntentBluetooth, REQUEST_ENABLE_BT)
        } else if (mBluetoothAdapter!!.isEnabled) {
            PairedDevicesList()
        }

        setBroadCastReceiver()

        //request location permission for bluetooth scanning for android API 23 and above
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_ENABLE_FINE_LOCATION)


        //press the button to start search new Devices
        searchForNewDevices!!.setOnClickListener {
            searchForNewDevices!!.isEnabled = false
            searchProgressbar!!.visibility = View.VISIBLE
            bluetoothDevicesAdapter!!.clear()
            PairedDevicesList()
            NewDevicesList()
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == REQUEST_ENABLE_FINE_LOCATION) {// If request is cancelled, the result arrays are empty.
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //permission granted!
            } else {
                Toast.makeText(this, "Access Location must be allowed for bluetooth Search", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                PairedDevicesList()
            } else {
                finish()
            }
        }
    }

    /**
     * Link the layout element from XML to Java
     */
    private fun initializeScreen() {
        searchForNewDevices = findViewById(R.id.search_fab_button)
        deviceRecycler = findViewById(R.id.devices_recycler)
        searchProgressbar = findViewById(R.id.search_progress_bar)
    }

    /**
     * to set the BroadCaster Receiver
     */
    private fun setBroadCastReceiver() {
        // Register for broadcasts when a device is discovered
        var filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        this.registerReceiver(mReceiver, filter)


        // Register for broadcasts when discovery has finished
        filter = IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        this.registerReceiver(mReceiver, filter)

    }

    /**
     * scan for new Devices and pair with them
     */
    private fun NewDevicesList() {
        // If we're already discovering, stop it
        if (mBluetoothAdapter!!.isDiscovering) {
            mBluetoothAdapter!!.cancelDiscovery()
        }

        // Request discover from BluetoothAdapter
        mBluetoothAdapter!!.startDiscovery()
    }

    /**
     * get the paired devices in the phone
     */
    private fun PairedDevicesList() {
        pairedDevices = mBluetoothAdapter!!.bondedDevices

        if (pairedDevices!!.size > 0) {
            for (bt in pairedDevices!!) {
                //Get the device's name and the address
                bluetoothDevicesAdapter!!.addDevice(bt.name + "\n" + bt.address)
            }
        } else {
            Toast.makeText(applicationContext, R.string.no_paired_devices,
                    Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        // Make sure we're not doing discovery anymore
        if (mBluetoothAdapter != null) {
            mBluetoothAdapter!!.cancelDiscovery()
        }

        // Unregister broadcast listeners
        unregisterReceiver(mReceiver)
    }

    /**
     * handle the click for the recycler view to get the MAC address
     */
    override fun onDeviceClick(deviceName: String) {
        //Get the device MAC address , the last 17 char in the view
        val MACAddress = deviceName.substring(deviceName.length - 17)

        // Create the result Intent and include the MAC address
        val intent = Intent()
        intent.putExtra(BluetoothStates.EXTRA_DEVICE_ADDRESS, MACAddress)

        // Set result and finish this Activity
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    companion object {
        /**
         * Tag for the log (Debugging)
         */
        private val LOG_TAG = BluetoothDevices::class.java.simpleName

        /**
         * request to enable bluetooth form activity result
         */
        val REQUEST_ENABLE_FINE_LOCATION = 1256
    }

}