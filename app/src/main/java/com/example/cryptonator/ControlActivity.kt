package com.example.cryptonator

import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.control_act.*
import java.io.IOException
import java.util.*


class ControlActivity: AppCompatActivity() {
    companion object {
        var m_myUUID: UUID= UUID.fromString("85cdc8c0-9119-11ea-bb37-0242ac130002")
        var m_bluetoothSocket: BluetoothSocket?= null
        lateinit var m_progress:ProgressDialog
        var m_isConnected:Boolean=false
        lateinit var m_bluetoothAdapter: BluetoothAdapter
        lateinit var m_address: String

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.control_act)
        m_address = intent.getStringExtra(MainActivity.EXTRA_ADDRESS)
        m_bluetoothAdapter= BluetoothAdapter.getDefaultAdapter()

        ConnectToDevice(this).execute()

        device_name.text = m_address
        Disconnectbutton.setOnClickListener{disconnect()}
        Unlockbutton.setOnClickListener { sendCommand("a") }

    }

    private fun sendCommand(input: String){
        if(m_bluetoothSocket!= null){
            try{

            }catch(e:IOException){
                e.printStackTrace()
            }
        }
    }

    private fun disconnect(){
        if(m_bluetoothSocket != null){
                try {
                    m_bluetoothSocket!!.close()
                    m_bluetoothSocket=null
                    m_isConnected=false

                }catch(e:IOException){
                    e.printStackTrace()
                }
        }
     finish()
    }

    private class ConnectToDevice(c: Context) : AsyncTask<Void, Void, String>() {
        private var connectSucess: Boolean = true
        private val context: Context

        init {
            this.context = c
        }

        override fun onPreExecute() {
            super.onPreExecute()
            m_progress = ProgressDialog.show(context, "Connecting", "Please Wait....")


        }

        override fun doInBackground(vararg p0: Void?): String? {
            try {
                if (m_bluetoothSocket == null || !m_isConnected){

                    val device: BluetoothDevice= m_bluetoothAdapter.getRemoteDevice(m_address)
                    m_bluetoothSocket=device.createInsecureRfcommSocketToServiceRecord(m_myUUID)
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery()
                    m_bluetoothSocket!!.connect()

                }
            }catch(e: IOException) {
                connectSucess= false
                e.printStackTrace()
            }
        return null
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if(!connectSucess){
                Log.i("data","Couldnt connect")

                val tea = Toast.makeText(context, "Couldn't Connect", Toast.LENGTH_LONG)
                tea.show()
//                val intent = Intent (context,MainActivity::class.java)
//
//                context.startActivity(intent)

//                (context as ControlActivity).finish()
            }else{
                m_isConnected = true
            }
            m_progress.dismiss()

        }

    }
}