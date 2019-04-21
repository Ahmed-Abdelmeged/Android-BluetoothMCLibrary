package com.ahmedabdelmeged.bluetoothmc.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ahmedabdelmeged.bluetoothmc.R

/**
 * custom array adapter to view the list of bluetooth devices
 *
 * @author Ahmed Abd-Elmeged
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