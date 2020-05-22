package com.example.cryptonator

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.cryptonator.ControlActivity.Companion.m_bluetoothSocket
import com.example.cryptonator.ControlActivity.Companion.m_isConnected
import  kotlinx.android.synthetic.main.verify_act.*
import java.io.IOException

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
        Disconnectbutton.setOnClickListener{disconnect()}
    }


        private fun disconnect(){
            if(m_bluetoothSocket != null){
                try {
                    m_bluetoothSocket!!.close()
                    m_bluetoothSocket=null
                    m_isConnected=false

                }catch(e: IOException){
                    e.printStackTrace()
                }
            }
            val intent = Intent (this,MainActivity::class.java)
                startActivity(intent)

    }
}