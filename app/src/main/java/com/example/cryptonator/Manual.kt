package com.example.cryptonator

import android.app.KeyguardManager
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.manual.*

class Manual : AppCompatActivity() {
    private  lateinit var  dbHelper: DBHelper
    lateinit var biometricManager: BiometricManager
    lateinit var  keyguard: KeyguardManager
    private lateinit var biometricPrompt: BiometricPrompt
    private  lateinit var promptInfo: BiometricPrompt.PromptInfo

    private val TAG= MainActivity::getLocalClassName.toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.manual)
        dbHelper = DBHelper(this)
        val list: ArrayList<String> = dbHelper.GetTable()

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1,list)
        devicetable.adapter = adapter
        val executor= ContextCompat.getMainExecutor(this)

        biometricManager= BiometricManager.from(this)


        promptInfo = BiometricPrompt.PromptInfo.Builder()

            .setTitle("Verify to the View Key")
            .setDeviceCredentialAllowed(true)
            .build()




        devicetable.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                val device: String = list[position]

                biometricPrompt= BiometricPrompt(this,executor,object : BiometricPrompt.AuthenticationCallback(){
                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        super.onAuthenticationError(errorCode, errString)
                        toasty("Authenication error:$errString")
                        checkBiometricStatus(biometricManager)

                    }

                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()
                        toasty("Authenication failed!!")
                    }

                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        super.onAuthenticationSucceeded(result)
                        val intent = Intent(applicationContext, KeyDisplay::class.java)
                        intent.putExtra("Device_address", device)
                        startActivity(intent)
                    }
                })
                biometricPrompt.authenticate(promptInfo)

            }
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
//                                verified()
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

}