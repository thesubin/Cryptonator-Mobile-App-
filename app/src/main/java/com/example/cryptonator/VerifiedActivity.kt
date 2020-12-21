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


class VerifiedActivity :AppCompatActivity(){
    companion object{


        val DEVICE_ADDRESS: String="address"
    }
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.verify_act)
        deviceaddress = intent.getStringExtra(ControlActivity.m_address)
        deviceStatus = ControlActivity.m_bluetoothSocket;

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
         handler.postDelayed(Runnable {
            handler.postDelayed(runnable, delay.toLong())
            try {

//                val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
//                val randomString = (1..64)
//                    .map { i -> kotlin.random.Random.nextInt(0, charPool.size) }
//                    .map(charPool::get)
//                    .joinToString("");
                var keybits =kotlin.random.Random.nextBytes(50);
                var randomString = "K$keybits";
               
                sendCommand("$randomString")

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


