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


import java.util.ArrayList;


public final class InputDataHelper {

    private ArrayList<String> sensorsValues;
    private StringBuilder readDataString;


    /**
     * Tag for the log (Debugging)
     */
    private static final String LOG_TAG = InputDataHelper.class.getSimpleName();

    /**
     * Required public constructor
     */
    public InputDataHelper() {
        sensorsValues = new ArrayList<>();
        readDataString = new StringBuilder();
    }

    /**
     * Set the data to the helper to format it
     *
     * @param data the reads from tbe handler
     * @return an array list of the sensors after format it
     */
    public ArrayList<String> setSensorsValues(String data) {
        readDataString.append(data);

        //determine the end of the line
        int endOfLineIndex = readDataString.indexOf("~");
        //make sure there data before ~
        if (endOfLineIndex > 0) {

            //if it start with # we know that we looking for
            if (readDataString.charAt(0) == '#') {

                //get the value from the string between indices
                String readValueAfterSub = readDataString.
                        substring(1, readDataString.length() - 1);

                int oldCounter = 0;
                //create an array that will hold the sensors value
                ArrayList<String> sensorsValueList = new ArrayList<>();
                for (int newCounter = 0; newCounter < readValueAfterSub.length(); newCounter++) {
                    if (readValueAfterSub.charAt(newCounter) == '+') {
                        sensorsValueList.add(readValueAfterSub.substring(oldCounter, newCounter));
                        oldCounter = newCounter + 1;
                    }
                }
                sensorsValues = sensorsValueList;
            }
            //clear all string data
            readDataString.delete(0, readDataString.length());

        }
        return sensorsValues;
    }
}
