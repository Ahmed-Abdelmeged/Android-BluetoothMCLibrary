package com.ahmedabdelmeged.bluetoothmcsample

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ahmedabdelmeged.bluetoothmc.BluetoothMC
import com.ahmedabdelmeged.bluetoothmc.ui.BluetoothDevices
import com.ahmedabdelmeged.bluetoothmc.util.BluetoothStates
import com.ahmedabdelmeged.bluetoothmc.util.InputDataHelper
import kotlinx.android.synthetic.main.activity_main.*

/**
 * @author Ahmed Abd-Elmeged
 */
class MainActivity : AppCompatActivity() {

    /**
     * Variables for bluetooth
     */
    private lateinit var bluetoothMC: BluetoothMC
    private lateinit var inputDataHelper: InputDataHelper
    private var sensors = listOf<String>()

    /**
     * UI Element
     */
    private var progressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // initialize the bluetoothmc and the data helper
        bluetoothMC = BluetoothMC()
        inputDataHelper = InputDataHelper()

        //check if the mobile have a bluetooth
        if (!bluetoothMC.isBluetoothAvailable()) {
            finish()
        } else if (!bluetoothMC.isBluetoothEnabled()) {
            bluetoothMC.enableBluetooth()
        }//check if the bluetooth enable or not and enable it if not

        //you can retrieve the current bluetooth adapter to customize it as you want
        val bluetoothAdapter = bluetoothMC.getBluetoothAdapter()

        fab_connect!!.setOnClickListener {
            val intent = Intent(this@MainActivity, BluetoothDevices::class.java)
            startActivityForResult(intent, BluetoothStates.REQUEST_CONNECT_DEVICE)
        }

        //send  a o command to the micro controller
        on_button!!.setOnClickListener { bluetoothMC.send("o") }

        //send  a f command to the micro controller
        off_button!!.setOnClickListener { bluetoothMC.send("f") }

        //set listener for listen for the incoming data from the microcontroller
        bluetoothMC.setOnDataReceivedListener(object : BluetoothMC.onDataReceivedListener {
            override fun onDataReceived(data: String) {
                sensors = inputDataHelper.setSensorsValues(data)

                if (sensors.size >= 4  /*this number despond on number of sensors you put*/) {
                    sensor_temp!!.text = getString(R.string.sensor_temp, sensors[0])
                    sensor_ldr!!.text = getString(R.string.sensor_ldr, sensors[1])
                    sensor_pot!!.text = getString(R.string.sensor_pot, sensors[2])
                    sensor_button!!.text = getString(R.string.sensor_button, sensors[3])
                }
            }
        })

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
                bluetoothMC.connect(data!!)
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
     * Fast way to call toast
     */
    private fun showToast(message: String) {
        Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
    }

}