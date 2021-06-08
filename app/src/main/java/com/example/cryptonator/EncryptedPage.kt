package com.example.cryptonator

import android.app.KeyguardManager
import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.control_act.*
import kotlinx.android.synthetic.main.control_act.device_name
import kotlinx.android.synthetic.main.encrypted.*
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.lang.StringBuilder
import java.util.*


class EncryptedPage: AppCompatActivity() {

    object HOLDER {
        val INSTANCE = EncryptedPage()
    }


    companion object {
        var m_myUUID: UUID= UUID.fromString("85cdc8c0-9119-11ea-bb37-0242ac130002")
        var m_bluetoothSocket: BluetoothSocket?= null
        lateinit var m_progress:ProgressDialog
        var m_isConnected:Boolean=false
        var m_isEncrypted:Boolean=false

        lateinit var m_bluetoothAdapter: BluetoothAdapter
        lateinit var m_address: String
        lateinit var nameBlue:String// test

        private val TAG= MainActivity::getLocalClassName.toString()
        val instance: EncryptedPage by lazy { HOLDER.INSTANCE }

    }
    private lateinit var progress: ProgressDialog
    lateinit var biometricManager: BiometricManager
    lateinit var  keyguard:KeyguardManager
    private lateinit var biometricPrompt: BiometricPrompt
    private  lateinit var promptInfo: BiometricPrompt.PromptInfo
    private lateinit var dbHelper:DBHelper



    override fun onBackPressed() {
// super.onBackPressed();
// Not calling **super**, disables back button in current screen.
    }
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.encrypted)
        m_address = intent.getStringExtra(MainActivity.EXTRA_ADDRESS)
        m_bluetoothAdapter= BluetoothAdapter.getDefaultAdapter()
        val testingName: BluetoothDevice= m_bluetoothAdapter.getRemoteDevice(m_address) //test
        nameBlue= testingName.name//test
        device_name.text = nameBlue//test
        Disconnectbutton.setOnClickListener{disconnect()}
        m_bluetoothSocket=ControlActivity.m_bluetoothSocket;
        dbHelper= DBHelper(this)




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
            .setTitle("Verify to Unlock")
            .setDescription("One Step from unlocking your files")
            .setDeviceCredentialAllowed(true)
            .build()



        Unlockbutton.setOnClickListener { biometricPrompt.authenticate(promptInfo) }

    }

//    public fun connect(divId:BluetoothSocket?){
//
//        connectionThread = ConnectedThread(divId)
//        connectionThread!!.run()
//        Toast.makeText(applicationContext, "Im here", Toast.LENGTH_LONG).show()
//
//    }


    private fun verified(){
        m_isEncrypted=true;
        if(dbHelper.GetDB().toString() !="") {
            sendCommand("D${dbHelper.GetDB().toString()}")
        }
        else{
            val AlphaNumericString = ("ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                    + "0123456789"
                    + "abcdefghijklmnopqrstuvxyz")

            // create StringBuffer size of AlphaNumericString

            // create StringBuffer size of AlphaNumericString
            val sb = StringBuilder(24)

            for (i in 0 until 24) {

                // generate a random number between
                // 0 to AlphaNumericString variable length
                val index = (AlphaNumericString.length
                        * Math.random()).toInt()

                // add Character one by one in end of sb
                sb.append(
                    AlphaNumericString[index]
                )
            }
            var randomString = "D${sb.toString()}";
            sendCommand("$randomString")
        }
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
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE-> {
                @RequiresApi(Build.VERSION_CODES.O)

                if(keyguard.isKeyguardLocked){
                    keyguard.requestDismissKeyguard(this,
                        object :KeyguardManager.KeyguardDismissCallback(){
                            override fun onDismissSucceeded() {
                                super.onDismissSucceeded()
                                verified()
                            }

                            override fun onDismissCancelled() {
                                super.onDismissCancelled()
                                toasty("Authentication Failed")
                            }

                            override fun onDismissError() {
                                super.onDismissError()
                                toasty("Authentication Error")

                            }
                        })
                }
                Log.d(TAG, "CheckBiometricStatus: Biometrics features cureently unavailable")
            }BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED->
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
            //   finish()
            val intent = Intent (this,MainActivity::class.java)
            startActivity(intent)

        }
    }

    private fun sendCommand(input: String) {
        if (m_bluetoothSocket != null) {
            try{

                m_bluetoothSocket!!.outputStream.write(input.toByteArray())
            } catch(e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun disconnect(){
        if(ControlActivity.m_bluetoothSocket != null){
            try {
                ControlActivity.m_bluetoothSocket!!.close()
                ControlActivity.m_bluetoothSocket =null
                ControlActivity.m_isConnected =false

            }catch(e: IOException){
                e.printStackTrace()
            }
        }
        val intent = Intent (this,MainActivity::class.java)
        startActivity(intent)

    }




}