package com.ahmedabdelmeged.bluetoothmc.util

import java.util.ArrayList

/**
 * @author Ahmed Abd-Elmeged
 */
class InputDataHelper {

    private var sensorsValues = listOf<String>()
    private val readDataString: StringBuilder = StringBuilder()

    /**
     * Set the data to the helper to format it
     *
     * @param data the reads from tbe handler
     * @return an array list of the sensors after format it
     */
    fun setSensorsValues(data: String): List<String> {
        readDataString.append(data)

        //determine the end of the line
        val endOfLineIndex = readDataString.indexOf("~")
        //make sure there data before ~
        if (endOfLineIndex > 0) {

            //if it start with # we know that we looking for
            if (readDataString[0] == '#') {

                //get the value from the string between indices
                val readValueAfterSub = readDataString.substring(1, readDataString.length - 1)

                var oldCounter = 0
                //create an array that will hold the sensors value
                val sensorsValueList = ArrayList<String>()
                for (newCounter in 0 until readValueAfterSub.length) {
                    if (readValueAfterSub[newCounter] == '+') {
                        sensorsValueList.add(readValueAfterSub.substring(oldCounter, newCounter))
                        oldCounter = newCounter + 1
                    }
                }
                sensorsValues = sensorsValueList
            }
            //clear all string data
            readDataString.delete(0, readDataString.length)
        }

        return sensorsValues
    }

}