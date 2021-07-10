package com.example.cryptonator

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context


class PolicyManager(private val mContext: Context) {
    private val mDPM: DevicePolicyManager
 public val adminComponent: ComponentName
    public val isAdminActive: Boolean
        get() = mDPM.isAdminActive(adminComponent)

    fun disableAdmin() {
        mDPM.removeActiveAdmin(adminComponent)
    }

    companion object {
        const val DPM_ACTIVATION_REQUEST_CODE = 100
    }

    init {


        // TODO Auto-generated constructor stub
        mDPM = mContext
            .getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        adminComponent = ComponentName(
            mContext.packageName,
            mContext.packageName + ".SampleDeviceAdminReceiver"
        )
    }

    fun isAdminsActive(): Boolean {
        return mDPM.isAdminActive(adminComponent)
    }
         fun getAdminsComponent(): ComponentName {
        return adminComponent
    }

}
