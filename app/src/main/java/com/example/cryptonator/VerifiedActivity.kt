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
    private var connectionThread: ConnectedThread? = null

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
        connectionThread = ConnectedThread(deviceStatus)
        connectionThread!!.run()

        device_name.text =  testingName.name
    //    run()
        Disconnectbutton.setOnClickListener{disconnect()}

    }
    private fun sendCommand(input: String) {
        if (deviceStatus != null) {
            try{
                Toast.makeText(applicationContext, "Sending Key:$input", Toast.LENGTH_LONG).show()

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
                var keybits =kotlin.random.Random.nextBits(184);
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
                    connectionThread!!.cancel()

                }catch(e: IOException){
                    e.printStackTrace()
                }
            }
            val intent = Intent (this,MainActivity::class.java)
                startActivity(intent)

    }


    private inner class ConnectedThread(private val mmSocket: BluetoothSocket?) : Thread() {
        private val mmInStream: InputStream?
        private val mmOutStream: OutputStream?
        private var thread: Thread?

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
//                                mainActivity.displayMessage(message)
                                Toast.makeText(applicationContext, "Receiving :${message}", Toast.LENGTH_LONG).show()

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


