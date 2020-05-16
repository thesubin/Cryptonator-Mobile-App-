package com.example.cryptonator

import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.*

import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager

import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat


import kotlinx.android.synthetic.main.control_act.*
import java.io.IOException
import java.lang.reflect.Method
import java.util.*


class ControlActivity: AppCompatActivity() {
    companion object {
        var m_myUUID: UUID= UUID.fromString("85cdc8c0-9119-11ea-bb37-0242ac130002")
        var m_bluetoothSocket: BluetoothSocket?= null
        lateinit var m_progress:ProgressDialog
        var m_isConnected:Boolean=false
        lateinit var m_bluetoothAdapter: BluetoothAdapter
        lateinit var m_address: String
        lateinit var nameBlue:String// test

    }

        lateinit var biometricManager: BiometricManager
        private val TAG= MainActivity::getLocalClassName.toString()
        private lateinit var biometricPrompt: BiometricPrompt
        private  lateinit var promptInfo: BiometricPrompt.PromptInfo

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.control_act)
        m_address = intent.getStringExtra(MainActivity.EXTRA_ADDRESS)
        m_bluetoothAdapter= BluetoothAdapter.getDefaultAdapter()
        val testingName: BluetoothDevice= m_bluetoothAdapter.getRemoteDevice(m_address) //test
        nameBlue= testingName.name//test
        device_name.text = nameBlue//test
        Disconnectbutton.setOnClickListener{disconnect()}


            if (testingName.bondState == BluetoothDevice.BOND_BONDED) {

                 ConnectToDevice(this).execute()

            } else {


                 pairDevice(testingName)

            }




        registerReceiver(mPairReceiver, IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED))

        biometricManager= BiometricManager.from(this)
        checkBiometricStatus(biometricManager)

        val executor= ContextCompat.getMainExecutor(this)

        biometricPrompt= BiometricPrompt(this,executor,object :BiometricPrompt.AuthenticationCallback(){
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                    toasty("Authenication error:$errString")
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                toasty("Authenication failed!!")
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                verified()

            }
        })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Verify to Unlock")
            .setDescription("One Step from unlocking your files")
            .setNegativeButtonText("use email to recover")
            .build()


        Unlockbutton.setOnClickListener { biometricPrompt.authenticate(promptInfo) }

    }


        private fun verified(){
            val intent = Intent (this,VerifiedActivity::class.java)
               intent.putExtra(m_address, m_address)
            startActivity(intent)

        }

        private fun toasty(message: String){
              Toast.makeText(applicationContext, message, Toast.LENGTH_LONG)
                  .show()
        }
    fun checkBiometricStatus(biometricManager: BiometricManager){
        when(biometricManager.canAuthenticate()){
                BiometricManager.BIOMETRIC_SUCCESS->
                    Log.d(TAG,"CheckBiometricStatus: App can use biometric Authentication")
                BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE->
                    Log.d(TAG,"CheckBiometricStatus: Biometrics features cureently unavailable")
               BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED->
                    Log.d(TAG,"CheckBiometricStatus: The User hasnt enrolled any biometrics")
                BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE->
                    Log.d(TAG,"CheckBiometricStatus: No Biometric Features available in this device")

        }
    }


    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun pairDevice(device: BluetoothDevice) {
        try {


            device.createBond()
//
//            val method = device.javaClass.getMethod("createBond", *(null as Array<Class<Any>>))
//            method.invoke(device, *(null as Array<Any>))
//

        } catch (e: Exception) {
            e.printStackTrace()

        }
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

    private val mPairReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (BluetoothDevice.ACTION_BOND_STATE_CHANGED == action) {
                val state =
                    intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR)
                val prevState = intent.getIntExtra(
                    BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE,
                    BluetoothDevice.ERROR
                )
                if (state == BluetoothDevice.BOND_BONDED && prevState == BluetoothDevice.BOND_BONDING) {
                    Toast.makeText(context, "Connected ", Toast.LENGTH_LONG)
                        .show()

                } else if (state == BluetoothDevice.BOND_NONE && prevState == BluetoothDevice.BOND_BONDING) {
                    Toast.makeText(context, "Couldnt pair ", Toast.LENGTH_LONG)
                        .show()

                }

            }
        }
    }
}