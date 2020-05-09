package com.example.cryptonator

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val REQUEST_CODE_ENABLE_BT: Int = 1
    private lateinit var bAdapter:BluetoothAdapter
    private lateinit var  pdevices: Set<BluetoothDevice>

    companion object{
        val EXTRA_ADDRESS: String= "Device_address"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bAdapter= BluetoothAdapter.getDefaultAdapter()
        if(bAdapter.isEnabled){

            Onbutton.visibility=View.GONE
            select_device.visibility=View.VISIBLE
            btnRefresh.visibility=View.VISIBLE
            textselect.visibility=View.VISIBLE
            pairedDeviceList()
        }

        btnRefresh.setOnClickListener{
            pairedDeviceList()

        }

        Onbutton.setOnClickListener {
            if(bAdapter.isEnabled){
                Toast.makeText(this,  "Already on", Toast.LENGTH_LONG).show()
            }
        else{
                val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(intent, REQUEST_CODE_ENABLE_BT)

            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if(requestCode == REQUEST_CODE_ENABLE_BT){
                    if (requestCode == Activity.RESULT_OK){
                        Toast.makeText(this,  "Could not turn on", Toast.LENGTH_LONG).show()
                    }
                else{
                        Toast.makeText(this,  "Turned on", Toast.LENGTH_LONG).show()
                        Onbutton.visibility=View.GONE
                        select_device.visibility=View.VISIBLE
                        btnRefresh.visibility=View.VISIBLE
                        textselect.visibility=View.VISIBLE
                        pairedDeviceList()
                    }
            }
            super.onActivityResult(requestCode, resultCode, data)
        }

    private fun pairedDeviceList(){
        pdevices = bAdapter.bondedDevices
        val list : ArrayList<BluetoothDevice> = ArrayList()

        if (!pdevices.isEmpty()){
            for (device: BluetoothDevice in pdevices){
                list.add(device)
                Log.i("device",""+device)
            }
        }else{
            Toast.makeText(this,  "no paired bluetooth devices found", Toast.LENGTH_LONG).show()

        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1,list)
        select_device.adapter = adapter
        select_device.onItemClickListener= AdapterView.OnItemClickListener{_,_,position,_->
            val device: BluetoothDevice= list[position]
            val address: String = device.address
            
            val intent = Intent (this, ControlActivity::class.java)
            intent.putExtra(EXTRA_ADDRESS,address)
            startActivity(intent)
        }
    }
}
