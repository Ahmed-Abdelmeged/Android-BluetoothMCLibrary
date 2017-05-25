## Android-BluetoothMC Library
This library is use SSP(Bluetooth Serial Port) profile to develop application to
communicate between Microcontroller and android device as the android device act as client . This library include send and receive functionality with some helper method
and it has built in `BluetoothDevices` list

## Feature
* It's very easy to use
* Apply material design
* Easy to integrate at any exciting project
* Built in `BluetoothDevices` list
* Listener for receive data from connection device
* Listeners for track the connection states and errors
* `InputDataHelper` to get the data well formatted from the Microcontroller

## Use case
This library can use in many different applications like ,IOT ,RC car, Prostheses
...etc the ideas and options are endless make now your own idea  true


## Getting started
* Download the `bluetoothmc` module from the repository      
1- in `settings.gradle` add the module like this
`include ':app',':bluetoothmc'`

    2- in the `build.gradle(Module:app)` add this line in the dependencies block   
  `compile project (':bluetoothmc')`

 * Declare BluetoothMC like this      
`BluetoothMC bluetoothmc = new BluetoothMC(Context)`

* Check if the bluetooth is now available          
```java
//check if the bluetooth is now available
     if (!bluetoothMC.isBluetoothAvailable()) {
        //do any action if the bluetooth is not available
     }
```     

* Check if the bluetooth is not enabled and you can enable it
```java    
     //check if the bluetooth enable or not and enable it if not
     else if (!bluetoothMC.isBluetoothEnabled()) {
         bluetoothMC.enableBluetooth();
     }
```     
* If the bluetooth available and enabled now start the BluetoothDevices and connect
```java
Intent intent = new Intent(MainActivity.this, BluetoothDevices.class);
startActivityForResult(intent, BluetoothStates.REQUEST_CONNECT_DEVICE);
```
In `onActivityResult` connect to the bluetooth device you choose

```java
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == BluetoothStates.REQUEST_CONNECT_DEVICE) {
        if (resultCode == Activity.RESULT_OK) {
            bluetoothMC.connect(data);
        }
    }
}
```

* You can set the listener to keep track the connection process and show a dialog ,  toast or what ever you want check the sample app for more details

```java
//set listener to keep track for the connection states
    bluetoothMC.setOnBluetoothConnectionListener(new BluetoothMC.BluetoothConnectionListener() {
        @Override
        public void onDeviceConnecting() {
            //this method triggered during the connection processes
        }

        @Override
        public void onDeviceConnected() {
            //this method triggered if the connection success
        }

        @Override
        public void onDeviceDisconnected() {
            //this method triggered if the device disconnected
        }

        @Override
        public void onDeviceConnectionFailed() {
            //this method triggered if the connection failed
        }
    });

```
* Now you are to use the send and receive functionality :D
to send data you the method `send()` like this  `bluetoothMC.send("o");`

* For receiving data first set a data listener like this
```java
//set listener for listen for the incoming data from the microcontroller
bluetoothMC.setOnDataReceivedListener(new BluetoothMC.onDataReceivedListener() {
    @Override
    public void onDataReceived(String data) {
    }
});
```

And you have to options for the incoming data you can receive it as string and formmat it as you want or you can use the `InputDataHelper` class . to use this method you **Must** send data in special way from the microcontroller as shown in the example  [Arduino-BluetoothMC.ino](https://github.com/Ahmed-Abdelmeged/Android-BluetoothMCLibrary/blob/master/Arduino-BluetoothMC/Arduino-BluetoothMC.ino)
to be the data formatted in this way eg. we have two sensors the data will be in this way `#sensor1Value+sensors2Value+~`


And then in Declare a InputDataHelper as Global  like this  
```java                          
InputDataHelper inputDataHelper = new InputDataHelper()
```

Now in the `onDataReceived()` call the `setSensorsValues` like this

```java
sensors = inputDataHelper.setSensorsValues(data);
```
Which Sensor is an `ArrayList<String>` and know you extract the reads from the
readings from the  `ArrayList` and parse it as you want

* You can set listener to keep track of the errors after the connection like this
```java
//set listener to keep track the communication errors
bluetoothMC.setOnBluetoothErrorsListener(new BluetoothMC.BluetoothErrorsListener() {
    @Override
    public void onSendingFailed() {
        //this method triggered if the app failed to send data
    }

    @Override
    public void onReceivingFailed() {
        //this method triggered if the app failed to receive data
    }

    @Override
    public void onDisconnectingFailed() {
        //this method triggered if the app failed to disconnect to the bluetooth device
    }

    @Override
    public void onCommunicationFailed() {
        //this method triggered if the app connect and unable to send and receive data
        //from the bluetooth device
    }
});
```

* You can also get the current adapter if you want to add some custom functionality
like this `        BluetoothAdapter bluetoothAdapter = bluetoothMC.getBluetoothAdapter();
`

* After you finish your use it's a good practice to disconnect from the current connection just call
`bluetoothMC.disconnect();`

* Don't Forget to handle the activity **life cycle** for your app
it's a better practice to lock the screen Orientation and keep it in one mode in the `manifest` like this `android:screenOrientation="portrait"` or the landscape mode

## Tutorial
This a video in arabic to help you use the library also
**SOON**

## Microcontroller
I use **Arduino** as simple example for test the code but it work for the other microcontrollers and this the Arduino code [Arduino-BluetoothMC.ino](https://github.com/Ahmed-Abdelmeged/Android-BluetoothMCLibrary/blob/master/Arduino-BluetoothMC/Arduino-BluetoothMC.ino)

## What's next?

* Add the library to `mavenCentral()` and `jcenter()`
* Add more customization to the `BluetoothDevice` activity
* Add auto connect future
* Support for secure and insecure connection
* Ability to connect to android device

## Sample App
You can use the sample app to help you get started
* BluetoothDevices activity

![BluetoothDevices](http://imgur.com/SN7DG21.png)

* MainActivity

![MainActivity](http://i.imgur.com/x6HC3lY.png)



## License

Copyright (c) 2017 Ahmed-Abdelmeged.

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

[http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
