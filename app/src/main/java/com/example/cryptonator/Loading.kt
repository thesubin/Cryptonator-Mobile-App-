package com.example.cryptonator

import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.control_act.*
import kotlinx.android.synthetic.main.control_act.device_name
import kotlinx.android.synthetic.main.encrypted.*
import java.util.*

class Loading:AppCompatActivity() {
    private object HOLDER {
        val INSTANCE = Loading()
    }
    companion object {
       val instance: Loading by lazy {Loading.HOLDER.INSTANCE }

    }
    var m_bluetoothSocket: BluetoothSocket?= null
    lateinit var m_progress:ProgressDialog
    var m_isConnected:Boolean=false
    var m_isEncrypted:Boolean=false

    lateinit var m_bluetoothAdapter: BluetoothAdapter
    lateinit var m_address: String
    lateinit var nameBlue:String// test

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContentView(R.layout.loading)
        m_address = intent.getStringExtra(MainActivity.EXTRA_ADDRESS)
        m_bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        val testingName: BluetoothDevice = m_bluetoothAdapter.getRemoteDevice(
            m_address
        ) //test
        nameBlue = testingName.name//test
        m_bluetoothSocket =ControlActivity.m_bluetoothSocket;


    }


}