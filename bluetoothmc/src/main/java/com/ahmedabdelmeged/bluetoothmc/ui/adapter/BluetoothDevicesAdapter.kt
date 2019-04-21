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

package com.ahmedabdelmeged.bluetoothmc.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ahmedabdelmeged.bluetoothmc.R

/**
 * custom array adapter to view the list of bluetooth devices
 */
class BluetoothDevicesAdapter(private val devices: MutableList<String>, private val deviceClickCallbacks: DeviceClickCallbacks)
    : RecyclerView.Adapter<BluetoothDevicesAdapter.BluetoothDeviceViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BluetoothDeviceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_device, parent, false)
        return BluetoothDeviceViewHolder(view)
    }

    override fun getItemCount(): Int {
        return devices.size
    }

    override fun onBindViewHolder(holder: BluetoothDeviceViewHolder, position: Int) {
        //set the device name
        val currentDevice = devices[position]
        holder.deviceName.text = currentDevice
    }

    inner class BluetoothDeviceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val deviceName: TextView = itemView.findViewById(R.id.device_name_textView)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            deviceClickCallbacks.onDeviceClick(devices[adapterPosition])
        }
    }

    fun clear() {
        this.devices.clear()
        notifyDataSetChanged()
    }

    fun addDevice(device: String) {
        this.devices.add(device)
        notifyDataSetChanged()
    }

}