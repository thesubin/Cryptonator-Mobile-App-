package com.example.cryptonator

import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private val REQUEST_CODE_ENABLE_BT: Int = 1
    private lateinit var bAdapter:BluetoothAdapter
    private lateinit var  pdevices: Set<BluetoothDevice>
    lateinit var m_progress:ProgressDialog
    val list: ArrayList<BluetoothDevice> = ArrayList()
    val nameList: ArrayList<String> = ArrayList()
     var params: Int = 0
    private val btManager: BTManager? = null

    companion object{
        val EXTRA_ADDRESS: String= "Device_address"
    }

    override fun onBackPressed() {
// super.onBackPressed();
// Not calling **super**, disables back button in current screen.
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        params= select_device.layoutParams.height

        bAdapter= BluetoothAdapter.getDefaultAdapter()
        if(bAdapter.isEnabled){
            if(ControlActivity.m_isConnected){
                val intent = Intent (this,VerifiedActivity::class.java)
                intent.putExtra(ControlActivity.m_address, ControlActivity.m_address)
                startActivity(intent)

            }
            Onbutton.visibility=View.GONE
            select_device.visibility=View.VISIBLE
            btnRefresh.visibility=View.VISIBLE
            textselect.visibility=View.VISIBLE
            registerReceiver()
            bAdapter.startDiscovery()
        }

        btnRefresh.setOnClickListener{
            registerReceiver()
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
                    if (bAdapter.isEnabled){
                        Toast.makeText(this,  "Turned on", Toast.LENGTH_LONG).show()
                        Onbutton.visibility=View.GONE
                        select_device.visibility=View.VISIBLE
                        btnRefresh.visibility=View.VISIBLE
                        textselect.visibility=View.VISIBLE
                        registerReceiver()
                        bAdapter.startDiscovery()

                    }
                else{
                        Toast.makeText(this,  "Could not turn on", Toast.LENGTH_LONG).show()
                    }
            }
            super.onActivityResult(requestCode, resultCode, data)
        }


    private fun registerReceiver() {
       registerReceiver(discoverReceiver, IntentFilter(BluetoothDevice.ACTION_FOUND))
        registerReceiver(discoverReceiver, IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED))
        registerReceiver(discoverReceiver, IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED))
        registerReceiver(discoverReceiver, IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
    }

    private val discoverReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {


            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(intent.action)) {
                val state =
                    intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);

                if (state == BluetoothAdapter.STATE_OFF) {
                    Toast.makeText(context, "Bluetooth Turned Off ", Toast.LENGTH_LONG)
                        .show()

                    Onbutton.visibility = View.VISIBLE
                    select_device.visibility = View.GONE
                    btnRefresh.visibility = View.GONE
                    textselect.visibility = View.GONE
                }
            }

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

                select_device.layoutParams.height= ViewGroup.LayoutParams.WRAP_CONTENT+240

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
                    select_device.layoutParams.height= params

                    pairedDeviceList("NotA")

                }
                else{
                    Toast.makeText(context,"Devices found: " +nameList.size, Toast.LENGTH_LONG)
                        .show()
                    pairedDeviceList("Available")
                }
            }
        }
    }





    private fun pairedDeviceList(status:String){
        pdevices = bAdapter.bondedDevices
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

        if(status=="NotA") {
            select_device2.visibility=View.GONE
            textselect2.visibility=View.GONE
            textselect.text = "Paired Devices"
            select_device.adapter = adapter
            select_device.onItemClickListener =
                AdapterView.OnItemClickListener { _, _, position, _ ->
                    val device: BluetoothDevice = hiddenlist[position]
                    val address: String = device.address
                    val intent = Intent(this, ControlActivity::class.java)
                    intent.putExtra(EXTRA_ADDRESS, address)
                    startActivity(intent)
                }

        }
        else{
            select_device2.visibility=View.VISIBLE
            textselect2.visibility=View.VISIBLE
            select_device2.adapter = adapter
            select_device2.onItemClickListener =
                AdapterView.OnItemClickListener { _, _, position, _ ->
                    val device: BluetoothDevice = hiddenlist[position]
                    val address: String = device.address
                    val intent = Intent(this, ControlActivity::class.java)
                    intent.putExtra(EXTRA_ADDRESS, address)
                    startActivity(intent)
                }

        }
    }
}
