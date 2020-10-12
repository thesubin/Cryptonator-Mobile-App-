package com.example.cryptonator

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.widget.Toast
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.*


class BTManager(private val mainActivity: MainActivity) {
    //Returns a list of all the bluetooth devices in the area
    var deviceList: ArrayList<BluetoothDevice>? = null
        private set
    private var BTAdapter: BluetoothAdapter? = null
    private var connectionThread: ConnectedThread? = null
    private var connectThread: ConnectThread? = null
    private var filter: IntentFilter? = null
    private fun init() {
        BTAdapter = BluetoothAdapter.getDefaultAdapter()
        deviceList = ArrayList()
        connectionThread = null
        connectThread = null
        // Register the BroadcastReceiver
        filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        mainActivity.registerReceiver(mReceiver, filter)
    }

    //Disconnect from device if already connected. Reset everything and scan for devices
    fun reset() {
        if (connectionThread != null) connectionThread!!.cancel()
        if (connectThread != null) connectThread!!.cancel()
        BTAdapter!!.cancelDiscovery()
        BTAdapter = null
        BTAdapter = BluetoothAdapter.getDefaultAdapter()
        init()
        startScanning()
    }

    //Call on ondestroy from main activity
    fun closeConnections() {
        if (connectionThread != null) connectionThread!!.cancel()
        if (connectThread != null) connectThread!!.cancel()
        BTAdapter!!.cancelDiscovery()
        BTAdapter = null
        mainActivity.unregisterReceiver(mReceiver)
    }

    //Check if bluetooth is enabled, if not then request that it be turned on
    fun CheckBTEnabled(): Boolean {
        if (BTAdapter == null) {
            Toast.makeText(mainActivity, "Device does not support bluetooth", Toast.LENGTH_LONG)
                .show()
            mainActivity.finish()
        }
        if (!BTAdapter!!.isEnabled) {
            val enableBT = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            mainActivity.startActivityForResult(enableBT, REQUEST_ENABLE_BT)
            return false
        }
        return true
    }

    //Connect to the bluetooth device passed in
    fun ConnectToDevice(device: BluetoothDevice) {
        connectThread = ConnectThread(device)
        connectThread!!.run()
    }

    //Start scanning for bluetooth devices
    fun startScanning() {
        BTAdapter!!.startDiscovery()
    }

    //Send message passed in over bluetooth
    fun sendMessage(message: String) {
        connectionThread!!.write(message)
    }

    //Adds device into device list
    fun addFoundDevice(device: BluetoothDevice) {
        deviceList!!.add(device)
    }

    //Class for handling connection to bluetooth device
    private inner class ConnectedThread(private val mmSocket: BluetoothSocket?) : Thread() {
        private val mmInStream: InputStream?
        private val mmOutStream: OutputStream?
        private var thread: Thread?

        //Method for reading from bluetooth device
        override fun run() {
            thread = object : Thread() {
                override fun run() {
                    try {
                        while (true) {
                            try {
                                val buffer =
                                    ByteArray(1024) // buffer store for the stream

                                //Blocking call
                                val bytes =
                                    mmInStream!!.read(buffer) // bytes returned from read()
                                val message = String(buffer, 0, bytes)

                                //Give the received message to the main activity to be displayed
//                                mainActivity.displayMessage(message)
                            } catch (e: IOException) {
                                break
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            (thread as Thread).start()
        }

        //TODO make this stop crashing if not connected
        /* Call this from the main activity to send data to the remote device */
        fun write(message: String) {
            try {
                val bytes = message.toByteArray()
                mmOutStream!!.write(bytes)
            } catch (e: IOException) {
            }
        }

        /* Call this from the main activity to shutdown the connection */
        fun cancel() {
            try {
                thread!!.interrupt()
                thread = null
                mmInStream!!.close()
                mmOutStream!!.close()
                mmSocket!!.close()
            } catch (e: IOException) {
            }
        }

        init {
            var tmpIn: InputStream? = null
            var tmpOut: OutputStream? = null
            thread = null
            try {
                tmpIn = mmSocket!!.inputStream
                tmpOut = mmSocket.outputStream
            } catch (e: IOException) {
            }
            mmInStream = tmpIn
            mmOutStream = tmpOut
        }
    }

    private val mReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(
            context: Context,
            intent: Intent
        ) {
            val action = intent.action
            if (BluetoothDevice.ACTION_FOUND == action) {
                //Get BluetoothDevice object from the Intent
                val device =
                    intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                var alreadyInList = false
                for (b in deviceList!!) {
                    if (b.address == device.address) alreadyInList = true
                }
                if (!alreadyInList) {
                    addFoundDevice(device)
//                    mainActivity.deviceFound(device)
                }
            }
        }
    }

    //Class for connecting to bluetooth device
    private inner class ConnectThread(device: BluetoothDevice) : Thread() {
        private val mmSocket: BluetoothSocket?
        private val mmDevice: BluetoothDevice
        override fun run() {
            BTAdapter!!.cancelDiscovery()
            try {
                mmSocket!!.connect()
            } catch (connectException: IOException) {
                // Unable to connect; close the socket and get out
                try {
                    mmSocket!!.close()
                } catch (closeException: IOException) {
                }
                return
            }

            //Bluetooth connected
            connectionThread = ConnectedThread(mmSocket)

            //Run method that listens for bluetooth messages
            connectionThread!!.run()

            //cancel();
        }

        /**
         * Will cancel an in-progress connection, and close the socket
         */
        fun cancel() {
            try {
                mmSocket!!.close()
            } catch (e: IOException) {
            }
        }

        init {
            var tmp: BluetoothSocket? = null
            mmDevice = device
            try {
                tmp = mmDevice.createRfcommSocketToServiceRecord(MY_UUID)
            } catch (e: IOException) {
            }
            mmSocket = tmp
        }
    }

    companion object {
        private const val REQUEST_ENABLE_BT = 1
        private val MY_UUID =
            UUID.fromString("85cdc8c0-9119-11ea-bb37-0242ac130002")
    }

    init {
        init()
    }
}