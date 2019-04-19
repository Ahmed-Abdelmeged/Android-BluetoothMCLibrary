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

package app.mego.android_bluetoothmclibrary;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ahmedabdelmeged.bluetoothmc.BluetoothMC;
import com.ahmedabdelmeged.bluetoothmc.ui.BluetoothDevices;
import com.ahmedabdelmeged.bluetoothmc.util.BluetoothStates;
import com.ahmedabdelmeged.bluetoothmc.util.InputDataHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    /**
     * Variables for bluetooth
     */
    BluetoothMC bluetoothMC;
    InputDataHelper inputDataHelper;
    ArrayList<String> sensors;

    /**
     * UI Element
     */
    private TextView potTextView, ldrTextView, buttonTextView, tempTextView;
    private FloatingActionButton fabConnect;
    private Button onButton, offButton;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeScreen();

        // initialize the bluetoothmc and the data helper
        bluetoothMC = new BluetoothMC();
        inputDataHelper = new InputDataHelper();

        //check if the mobile have a bluetooth
        if (!bluetoothMC.isBluetoothAvailable()) {
            finish();
        }
        //check if the bluetooth enable or not and enable it if not
        else if (!bluetoothMC.isBluetoothEnabled()) {
            bluetoothMC.enableBluetooth();
        }

        //you can retrieve the current bluetooth adapter to customize it as you want
        BluetoothAdapter bluetoothAdapter = bluetoothMC.getBluetoothAdapter();

        fabConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BluetoothDevices.class);
                startActivityForResult(intent, BluetoothStates.REQUEST_CONNECT_DEVICE);
            }
        });

        //send  a o command to the micro controller
        onButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bluetoothMC.send("o");
            }
        });

        //send  a f command to the micro controller
        offButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bluetoothMC.send("f");
            }
        });

        //set listener for listen for the incoming data from the microcontroller
        bluetoothMC.setOnDataReceivedListener(new BluetoothMC.onDataReceivedListener() {
            @Override
            public void onDataReceived(String data) {
                sensors = inputDataHelper.setSensorsValues(data);

                if (sensors.size() >= 4  /*this number despond on number of sensors you put*/) {
                    tempTextView.setText("Temp: " + sensors.get(0));
                    ldrTextView.setText("LDR: " + sensors.get(1));
                    potTextView.setText("POT: " + sensors.get(2));
                    buttonTextView.setText("Button: " + sensors.get(3));
                }
            }
        });

        //set listener to keep track for the connection states
        bluetoothMC.setOnBluetoothConnectionListener(new BluetoothMC.BluetoothConnectionListener() {
            @Override
            public void onDeviceConnecting() {
                //this method triggered during the connection processes
                //show a progress dialog
                progressDialog = ProgressDialog.show(MainActivity.this,
                        "Connecting...", "Please wait!!!");
            }

            @Override
            public void onDeviceConnected() {
                //this method triggered if the connection success
                showToast("Device Connected");
                progressDialog.dismiss();
            }

            @Override
            public void onDeviceDisconnected() {
                //this method triggered if the device disconnected
                showToast("Device disconnected");
            }

            @Override
            public void onDeviceConnectionFailed() {
                //this method triggered if the connection failed
                showToast("Connection failed try again");
                progressDialog.dismiss();
            }
        });

        //set listener to keep track the communication errors
        bluetoothMC.setOnBluetoothErrorsListener(new BluetoothMC.BluetoothErrorsListener() {
            @Override
            public void onSendingFailed() {
                //this method triggered if the app failed to send data
                showToast("Send data failed");
            }

            @Override
            public void onReceivingFailed() {
                //this method triggered if the app failed to receive data
                showToast("Receive data failed");
            }

            @Override
            public void onDisconnectingFailed() {
                //this method triggered if the app failed to disconnect to the bluetooth device
                showToast("Can't disconnect");
            }

            @Override
            public void onCommunicationFailed() {
                //this method triggered if the app connect and unable to send and receive data
                //from the bluetooth device
                showToast("Communication failed try again");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BluetoothStates.REQUEST_CONNECT_DEVICE) {
            if (resultCode == Activity.RESULT_OK) {
                bluetoothMC.connect(data);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.item_disconnect) {
            //here disconnect to the bluetooth device
            bluetoothMC.disconnect();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Link the layout element from XML to Java
     */
    private void initializeScreen() {
        potTextView = findViewById(R.id.sensor_pot);
        ldrTextView = findViewById(R.id.sensor_ldr);
        buttonTextView = findViewById(R.id.sensor_button);
        tempTextView = findViewById(R.id.sensor_temp);

        fabConnect = findViewById(R.id.fab_connect);

        onButton = findViewById(R.id.on_button);
        offButton = findViewById(R.id.off_button);
    }

    /**
     * Fast way to call toast
     */
    private void showToast(String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
    }

}