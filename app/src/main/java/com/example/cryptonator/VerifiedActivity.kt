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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.verify_act)
        deviceaddress = intent.getStringExtra(ControlActivity.m_address)
        deviceStatus = ControlActivity.m_bluetoothSocket;

        m_bluetoothAdapter= BluetoothAdapter.getDefaultAdapter()
        val testingName: BluetoothDevice = m_bluetoothAdapter.getRemoteDevice(deviceaddress)

        device_name.text =  testingName.name
        run()
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
    fun run() {
        val mmInStream: InputStream
        var tempInStream: InputStream? = null

        try {
            tempInStream = deviceStatus!!.inputStream
        } catch (e: IOException) {
            e.printStackTrace()
        }
        mmInStream=tempInStream!!
        val buffer = ByteArray(1024) // buffer store for the stream
        var bytes: Int // bytes returned from read()

        // Keep listening to the InputStream until an exception occurs
        while (true) {
            // Read from the InputStream
            try {
                bytes = mmInStream.read(buffer)
                val incomingMessage = String(buffer, 0, bytes)
                Toast.makeText(applicationContext, "Receiving:$incomingMessage", Toast.LENGTH_LONG).show()

                //   Log.d(FragmentActivity.TAG, "InputStream: $incomingMessage")
            } catch (e: IOException) {
               // Log.e(FragmentActivity.TAG, "write: Error reading Input Stream. " + e.message)
                break
            }
        }
    }
    override fun onResume() {
        handler.postDelayed(Runnable {
            handler.postDelayed(runnable, delay.toLong())
            try {

                val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
                val randomString = (1..64)
                    .map { i -> kotlin.random.Random.nextInt(0, charPool.size) }
                    .map(charPool::get)
                    .joinToString("");
                Toast.makeText(applicationContext, "Sending Key:$randomString", Toast.LENGTH_LONG).show()

                sendCommand(randomString)

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


