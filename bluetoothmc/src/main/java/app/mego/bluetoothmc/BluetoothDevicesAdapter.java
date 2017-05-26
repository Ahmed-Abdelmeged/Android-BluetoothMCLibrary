/*
 * Copyright (c) 2017 Ahmed-Abdelmeged
 *
 * github: https://github.com/Ahmed-Abdelmeged
 * email: ahmed.abdelmeged.vm@gamil.com
 * Facebook: https://www.facebook.com/ven.rto
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

package app.mego.bluetoothmc;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * custom array adapter to view the list of bluetooth devices
 */
public class BluetoothDevicesAdapter extends ArrayAdapter<String> {

    /**
     * Required public constructor
     */
    public BluetoothDevicesAdapter(@NonNull Context context, ArrayList<String> devices) {
        super(context, 0, devices);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {


        //check if the view is created or not if not inflate new one
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_device, parent, false);
        }
        //get a instance from the viewHolder class
        ViewHolder holder = new ViewHolder();
        holder.deviceName = (TextView) convertView.findViewById(R.id.device_name_textView);
        convertView.setTag(holder);

        //set the current device name
        String deviceName = getItem(position);
        if (deviceName != null) {
            holder.deviceName.setText(deviceName);
        }
        return convertView;
    }

    /**
     * View holder stores each of the component views inside the tag field of the Layout
     */
    private static class ViewHolder {
        TextView deviceName;
    }
}
