package com.example.cryptonator

import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothSocket
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class Loading:AppCompatActivity() {
    private object HOLDER {
        val INSTANCE = Loading()
    }
    companion object {
       val instance: Loading by lazy {Loading.HOLDER.INSTANCE }

    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContentView(R.layout.loading)


    }


}