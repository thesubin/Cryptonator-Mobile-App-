package com.example.cryptonator

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cryptonator.ControlActivity.Companion.m_bluetoothSocket
import com.example.cryptonator.ControlActivity.Companion.m_isConnected
import kotlinx.android.synthetic.main.verify_act.*
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.security.SecureRandom


class VerifiedActivity :AppCompatActivity(){

        private object HOLDER {
            val INSTANCE = VerifiedActivity()
        }


    companion object{

        val instance: VerifiedActivity by lazy { VerifiedActivity.HOLDER.INSTANCE }

        val DEVICE_ADDRESS: String="address"

    }
    private lateinit var dbHelper:DBHelper
    private lateinit var deviceaddress:String
    lateinit var m_bluetoothAdapter: BluetoothAdapter
    var deviceStatus: BluetoothSocket?=null;
    var handler: Handler = Handler()
    var runnable: Runnable? = null
    var delay = 10000
    private val btManager: BTManager? = null

    override fun onBackPressed() {
// super.onBackPressed();
// Not calling **super**, disables back button in current screen.
    }

            fun toasty (value:String?){
               Toast.makeText(applicationContext, value, Toast.LENGTH_LONG).show()

           }
    override fun onCreate(savedInstanceState: Bundle?) {
        println("Verified Activity")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.verify_act)
        deviceaddress = intent.getStringExtra("Address")
        deviceStatus = ControlActivity.m_bluetoothSocket;

        dbHelper= DBHelper(this)

        m_bluetoothAdapter= BluetoothAdapter.getDefaultAdapter()
        val testingName: BluetoothDevice = m_bluetoothAdapter.getRemoteDevice(deviceaddress)


        device_name.text =  testingName.name
    //    run()
        Disconnectbutton.setOnClickListener{disconnect()}

    }
    private fun sendCommand(input: String) {
        if (deviceStatus != null) {
            try{

                deviceStatus!!.outputStream.write(input.toByteArray())
            } catch(e: IOException) {
                e.printStackTrace()
            }
        }
    }




    override fun onResume() {
//        var androidId :String= Settings.Secure.getString(contentResolver,Settings.Secure.ANDROID_ID)
//        sendCommand("M$androidId")
        sendCommand(dbHelper.GetDB().toString())
         handler.postDelayed(Runnable {
            handler.postDelayed(runnable, delay.toLong())
            try {

//                val charPool: List<IntRange> =  (0..255)
//                val randomString = (1..24)
//                    .map { i -> kotlin.random.Random.nextInt(0, charPool.size) }
//                    .map(charPool::get)
//                    .joinToString("");
                var randomKey:IntArray = IntArray(24);
                for(i in 1..23){
                    randomKey[i]=kotlin.random.Random.nextInt(0, 255)

                }

//                var keybits =kotlin.random.Random.nextBytes(75);
//                var keybits = ByteArray(24)
//                kotlin.random.Random.nextBytes(keybits);

                println("HERE"+randomKey);
//                var randomString = "K$keybits.toSt";
//
//                sendCommand("$randomString")

            }
            catch(e:IOException){
                Toast.makeText(applicationContext, "Not Successful", Toast.LENGTH_LONG).show()

            }
        }.also { runnable = it }, delay.toLong())
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(runnable) //stop handler when activity not visible super.onPause();
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


