package com.example.cryptonator

import android.app.Activity
import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
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
    lateinit var m_progress:ProgressDialog
    val list: ArrayList<BluetoothDevice> = ArrayList()
    val nameList: ArrayList<String> = ArrayList()
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
            bAdapter.startDiscovery()
            registerReceiver()
        }

        btnRefresh.setOnClickListener{
            bAdapter.startDiscovery()
            nameList.clear()
            list.clear()
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
                        bAdapter.startDiscovery()
                        registerReceiver()
                    }
            }
            super.onActivityResult(requestCode, resultCode, data)
        }


    private fun registerReceiver() {
       registerReceiver(discoverReceiver, IntentFilter(BluetoothDevice.ACTION_FOUND))
        registerReceiver(discoverReceiver, IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED))
        registerReceiver(discoverReceiver, IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
    }

    private val discoverReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {


            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(intent.action)) {
                m_progress = ProgressDialog.show(
                    context,
                    "Searching for Bluetooth devices",
                    "Please Wait...."
                )
            }
            if (BluetoothDevice.ACTION_FOUND.equals(intent.action)) {
                val device =
                    intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)

                list.add(device)
                nameList.add(device.name)
                Log.i("device", "" + device)

                textselect.text = "Available Devices"
                val adapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, nameList)
                select_device.adapter = adapter
                select_device.onItemClickListener =
                    AdapterView.OnItemClickListener { _, _, position, _ ->
                        val device: BluetoothDevice = list[position]
                        val address: String = device.address

                        val intent = Intent(context, ControlActivity::class.java)
                        intent.putExtra(EXTRA_ADDRESS, address)
                        startActivity(intent)
                    }
            }

            if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(intent.action)) {
                Toast.makeText(context, "Search Completed", Toast.LENGTH_LONG)
                    .show()
                m_progress.dismiss()

                if (nameList.isEmpty()||nameList.size==0||nameList==null){
                    Toast.makeText(context, "No devices found!", Toast.LENGTH_LONG)
                        .show()
                    pairedDeviceList()
                }
                else{
                    Toast.makeText(context,"hello" +nameList.size, Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }





    private fun pairedDeviceList(){

        pdevices = bAdapter.bondedDevices
        textselect.text = "Paired Devices"


        val hiddenlist : ArrayList<BluetoothDevice> = ArrayList()
        val nList : ArrayList<String> = ArrayList()
        if (!pdevices.isEmpty()){
            for (device: BluetoothDevice in pdevices){
                hiddenlist.add(device)
                nList.add(device.name)
                Log.i("device",""+device)
            }
        }else{
            Toast.makeText(this,  "no paired bluetooth devices found", Toast.LENGTH_LONG).show()

        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1,nList)
        select_device.adapter = adapter
        select_device.onItemClickListener= AdapterView.OnItemClickListener{_,_,position,_->
            val device: BluetoothDevice= hiddenlist[position]
            val address: String = device.address
            
            val intent = Intent (this, ControlActivity::class.java)
            intent.putExtra(EXTRA_ADDRESS,address)
            startActivity(intent)
        }
    }
}
