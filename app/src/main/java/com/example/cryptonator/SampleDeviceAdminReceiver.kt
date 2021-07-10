package com.example.cryptonator

import android.app.admin.DeviceAdminReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast


class SampleDeviceAdminReceiver : DeviceAdminReceiver() {
    override fun onDisabled(context: Context, intent: Intent) {


        // TODO Auto-generated method stub
        Toast.makeText(context, "disabled dpm", Toast.LENGTH_SHORT).show()
        super.onDisabled(context, intent)
    }

    override fun onEnabled(context: Context, intent: Intent) {


        // TODO Auto-generated method stub
        Toast.makeText(context, "enabled dpm", Toast.LENGTH_SHORT).show()
        super.onEnabled(context, intent)
    }

    override fun onDisableRequested(
        context: Context,
        intent: Intent
    ): CharSequence {


        // TODO Auto-generated method stub
        Toast.makeText(context, "disable dpm request", Toast.LENGTH_SHORT).show()
        return super.onDisableRequested(context, intent)
    }
}
