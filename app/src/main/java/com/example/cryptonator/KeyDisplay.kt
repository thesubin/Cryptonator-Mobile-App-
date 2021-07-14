package com.example.cryptonator

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.keydisplay.*

class KeyDisplay:AppCompatActivity() {
    private lateinit var m_address:String
    private lateinit var dbHelper:DBHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.keydisplay)
        m_address = intent.getStringExtra("Device_address")
        dbHelper = DBHelper(this)

        key.text= dbHelper.getDeviceKey(m_address)
    }
}