package com.example.cryptonator

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import  kotlinx.android.synthetic.main.verify_act.*

class VerifiedActivity :AppCompatActivity(){
    companion object{
       val DEVICE_ADDRESS: String="address"
    }
    private lateinit var deviceaddress:String
    lateinit var m_bluetoothAdapter: BluetoothAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.verify_act)
        deviceaddress = intent.getStringExtra(ControlActivity.m_address)
        m_bluetoothAdapter= BluetoothAdapter.getDefaultAdapter()
        val testingName: BluetoothDevice = m_bluetoothAdapter.getRemoteDevice(deviceaddress)

        device_name.text =  testingName.name

    }
}