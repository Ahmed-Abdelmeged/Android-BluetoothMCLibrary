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

package com.ahmedabdelmeged.bluetoothmc.ui.adapter;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.ahmedabdelmeged.bluetoothmc.R;

import java.util.ArrayList;
import java.util.List;


/**
 * custom array adapter to view the list of bluetooth devices
 */
public class BluetoothDevicesAdapter extends RecyclerView.Adapter<BluetoothDevicesAdapter.BluetoothDeviceViewHolder> {

    private List<String> devices = new ArrayList<>();

    private DeviceClickCallbacks deviceClickCallbacks;

    public BluetoothDevicesAdapter(List<String> devices, DeviceClickCallbacks deviceClickCallbacks) {
        this.devices = devices;
        this.deviceClickCallbacks = deviceClickCallbacks;
    }

    @Override
    public BluetoothDevicesAdapter.BluetoothDeviceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_device, parent, false);
        return new BluetoothDeviceViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    @Override
    public void onBindViewHolder(BluetoothDevicesAdapter.BluetoothDeviceViewHolder holder, int position) {
        //set the device name
        String currentDevice = devices.get(position);
        if (currentDevice != null) {
            holder.deviceName.setText(currentDevice);
        } else {
            holder.deviceName.setText("");
        }
    }

    class BluetoothDeviceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView deviceName;

        BluetoothDeviceViewHolder(View itemView) {
            super(itemView);
            deviceName = itemView.findViewById(R.id.device_name_textView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            deviceClickCallbacks.onDeviceClick(devices.get(getAdapterPosition()));
        }
    }

}
