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

package app.mego.android_bluetoothmclibrary

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ahmedabdelmeged.bluetoothmc.BluetoothMC
import com.ahmedabdelmeged.bluetoothmc.ui.BluetoothDevices
import com.ahmedabdelmeged.bluetoothmc.util.BluetoothStates
import com.ahmedabdelmeged.bluetoothmc.util.InputDataHelper
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    /**
     * Variables for bluetooth
     */
    lateinit var bluetoothMC: BluetoothMC
    lateinit var inputDataHelper: InputDataHelper
    var sensors = listOf<String>()

    /**
     * UI Element
     */
    private var potTextView: TextView? = null
    private var ldrTextView: TextView? = null
    private var buttonTextView: TextView? = null
    private var tempTextView: TextView? = null
    private var fabConnect: FloatingActionButton? = null
    private var onButton: Button? = null
    private var offButton: Button? = null
    private var progressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initializeScreen()

        // initialize the bluetoothmc and the data helper
        bluetoothMC = BluetoothMC()
        inputDataHelper = InputDataHelper()

        //check if the mobile have a bluetooth
        if (!bluetoothMC.isBluetoothAvailable) {
            finish()
        } else if (!bluetoothMC.isBluetoothEnabled) {
            bluetoothMC.enableBluetooth()
        }//check if the bluetooth enable or not and enable it if not

        //you can retrieve the current bluetooth adapter to customize it as you want
        val bluetoothAdapter = bluetoothMC.bluetoothAdapter

        fabConnect!!.setOnClickListener {
            val intent = Intent(this@MainActivity, BluetoothDevices::class.java)
            startActivityForResult(intent, BluetoothStates.REQUEST_CONNECT_DEVICE)
        }

        //send  a o command to the micro controller
        onButton!!.setOnClickListener { bluetoothMC.send("o") }

        //send  a f command to the micro controller
        offButton!!.setOnClickListener { bluetoothMC.send("f") }

        //set listener for listen for the incoming data from the microcontroller
        bluetoothMC.setOnDataReceivedListener { data ->
            sensors = inputDataHelper.setSensorsValues(data)

            if (sensors.size >= 4  /*this number despond on number of sensors you put*/) {
                tempTextView!!.text = "Temp: " + sensors[0]
                ldrTextView!!.text = "LDR: " + sensors[1]
                potTextView!!.text = "POT: " + sensors[2]
                buttonTextView!!.text = "Button: " + sensors[3]
            }
        }

        //set listener to keep track for the connection states
        bluetoothMC.setOnBluetoothConnectionListener(object : BluetoothMC.BluetoothConnectionListener {
            override fun onDeviceConnecting() {
                //this method triggered during the connection processes
                //show a progress dialog
                progressDialog = ProgressDialog.show(this@MainActivity,
                        "Connecting...", "Please wait!!!")
            }

            override fun onDeviceConnected() {
                //this method triggered if the connection success
                showToast("Device Connected")
                progressDialog!!.dismiss()
            }

            override fun onDeviceDisconnected() {
                //this method triggered if the device disconnected
                showToast("Device disconnected")
            }

            override fun onDeviceConnectionFailed() {
                //this method triggered if the connection failed
                showToast("Connection failed try again")
                progressDialog!!.dismiss()
            }
        })

        //set listener to keep track the communication errors
        bluetoothMC.setOnBluetoothErrorsListener(object : BluetoothMC.BluetoothErrorsListener {
            override fun onSendingFailed() {
                //this method triggered if the app failed to send data
                showToast("Send data failed")
            }

            override fun onReceivingFailed() {
                //this method triggered if the app failed to receive data
                showToast("Receive data failed")
            }

            override fun onDisconnectingFailed() {
                //this method triggered if the app failed to disconnect to the bluetooth device
                showToast("Can't disconnect")
            }

            override fun onCommunicationFailed() {
                //this method triggered if the app connect and unable to send and receive data
                //from the bluetooth device
                showToast("Communication failed try again")
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == BluetoothStates.REQUEST_CONNECT_DEVICE) {
            if (resultCode == Activity.RESULT_OK) {
                bluetoothMC.connect(data)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.item_disconnect) {
            //here disconnect to the bluetooth device
            bluetoothMC.disconnect()
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Link the layout element from XML to Java
     */
    private fun initializeScreen() {
        potTextView = findViewById(R.id.sensor_pot)
        ldrTextView = findViewById(R.id.sensor_ldr)
        buttonTextView = findViewById(R.id.sensor_button)
        tempTextView = findViewById(R.id.sensor_temp)

        fabConnect = findViewById(R.id.fab_connect)

        onButton = findViewById(R.id.on_button)
        offButton = findViewById(R.id.off_button)
    }

    /**
     * Fast way to call toast
     */
    private fun showToast(message: String) {
        Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
    }

}