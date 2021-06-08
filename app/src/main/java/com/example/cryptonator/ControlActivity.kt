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
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.*


class ControlActivity: AppCompatActivity() {

        private object HOLDER {
            val INSTANCE = ControlActivity()
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
        private var connectionThread: ConnectedThread? = null

        private val TAG= MainActivity::getLocalClassName.toString()
        val instance: ControlActivity by lazy { HOLDER.INSTANCE }

    }
        private lateinit var progress: ProgressDialog
        lateinit var biometricManager: BiometricManager
    lateinit var  keyguard:KeyguardManager
        private lateinit var biometricPrompt: BiometricPrompt
        private  lateinit var promptInfo: BiometricPrompt.PromptInfo



    override fun onBackPressed() {
// super.onBackPressed();
// Not calling **super**, disables back button in current screen.
    }
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.control_act)
        m_address = intent.getStringExtra("Device_address")
        m_bluetoothAdapter= BluetoothAdapter.getDefaultAdapter()
        val testingName: BluetoothDevice= m_bluetoothAdapter.getRemoteDevice(m_address) //test
        nameBlue= testingName.name//test
        device_name.text = nameBlue//test
        connectionThread=null
            if(!MainActivity.isConnected)
        {
            if (testingName.bondState == BluetoothDevice.BOND_BONDED ) {

                ConnectToDevice(this).execute()

            } else {


                pairDevice(testingName)

            }
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
            .setTitle("Verify to Unlock")
            .setDescription("One Step from unlocking your files")
            .setDeviceCredentialAllowed(true)
            .build()




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

    private fun sendCommand(){

    }

    private fun disconnect(){
        if(m_bluetoothSocket != null){
                try {
                    m_bluetoothSocket!!.close()
                    m_bluetoothSocket=null
                    m_isConnected=false
                    MainActivity.isConnected=false;
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
//            m_progress = ProgressDialog.show(context, "Connecting", "Please Wait....")


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
                //ENABLE THIS AFTER CREATING SERVER
               val intent = Intent (context,MainActivity::class.java)
               context.startActivity(intent)

//                (context as ControlActivity).finish()
            }else{
                m_isConnected = true
                MainActivity.isConnected=true

                var androidId :String= Settings.Secure.getString(this.context.getContentResolver(),Settings.Secure.ANDROID_ID)
                var sendData:String="M$androidId"
                if(m_bluetoothSocket!= null){
                    try{
                        m_bluetoothSocket!!.outputStream.write(sendData.toByteArray());
                        val tea = Toast.makeText(context, "Sending $sendData", Toast.LENGTH_LONG)
                        tea.show()                              //Send Key

                        connectionThread = ConnectedThread(m_bluetoothSocket,this.context)
                        connectionThread!!.run()



                    }catch(e:IOException){
                        e.printStackTrace()
                    }

                }
            }
//            m_progress.dismiss()

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
                    ConnectToDevice(context).execute()

                } else if (state == BluetoothDevice.BOND_NONE && prevState == BluetoothDevice.BOND_BONDING) {
                    Toast.makeText(context, "Couldnt pair ", Toast.LENGTH_LONG)
                        .show()

                }

            }
        }
    }

    private  class ConnectedThread(private val mmSocket: BluetoothSocket?,c:Context) : Thread() {
        private val mmInStream: InputStream?
        private val mmOutStream: OutputStream?
        private var thread: Thread?
        private val context: Context

        init {
            this.context=c;
        }
        private val helperClass =HelperClass(context, m_address);

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
//                             //   mainActivity.displayMessage(message)
//                             Toast.makeText(applicationcontext, "Receiving :${message}", Toast.LENGTH_LONG).show()
//                                   nameBlue=message

//                                VerifiedActivity.instance.device_name.text=message;
                                Log.d(TAG,message)
                                println(message)
                                  helperClass.dataParser(message);

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


}