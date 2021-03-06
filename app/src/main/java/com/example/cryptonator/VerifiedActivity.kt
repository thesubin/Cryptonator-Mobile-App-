package com.example.cryptonator

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cryptonator.ControlActivity.Companion.m_bluetoothSocket
import com.example.cryptonator.ControlActivity.Companion.m_isConnected
import kotlinx.android.synthetic.main.verify_act.*
import java.io.IOException
import java.lang.StringBuilder


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
    var delay = 2000
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
        if(dbHelper.GetDB().toString() !="") {
            println("D${dbHelper.GetDB().toString()}")
            sendCommand("K${dbHelper.GetDB().toString()}")
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
            var randomString = "K${sb.toString()}";
            sendCommand("$randomString")
        }
         handler.postDelayed(Runnable {
            handler.postDelayed(runnable, delay.toLong())
            try {

                val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
//               val randomString = (1..64)
//                var keybits =kotlin.random.Random.nextBytes(50);
//                var randomString = "K$keybits";

//                sendCommand("$randomString")

//                var keybits =kotlin.random.Random.nextBytes(75);
//                var keybits = ByteArray(24)
//                kotlin.random.Random.nextBytes(keybits);

//                println("HERE"+randomKey);
//                var randomString = "K$keybits.toSt";
//
//               sendCommand("$randomKey")

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
                var randomString = "K${sb.toString()}";
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


