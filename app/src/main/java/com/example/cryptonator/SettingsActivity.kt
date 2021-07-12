package com.example.cryptonator

import android.app.admin.DevicePolicyManager
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import androidx.preference.SwitchPreferenceCompat


class SettingsActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener  {
    private var mDPM: DevicePolicyManager? = null
    private lateinit var policyManager: PolicyManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        policyManager =  PolicyManager(this);

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        PreferenceManager.getDefaultSharedPreferences(this)
            .registerOnSharedPreferenceChangeListener(this)

//        mDPM = this
//            .getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager?
//
//
//        adminComponent = ComponentName(
//            mContext.getPackageName(),
//            mContext.getPackageName().toString() + ".SampleDeviceAdminReceiver"
//        )


    }

    class SettingsFragment : PreferenceFragmentCompat() {

        private lateinit var  spd :SwitchPreferenceCompat
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

        }

    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
       if(key == "uninstall") {
           val pref = sharedPreferences?.getBoolean(key, false)
           if(pref == true){
               if (!policyManager.isAdminsActive()) {

                   println("Here inside")
                   val activateDeviceAdmin = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);


                   activateDeviceAdmin.putExtra(


                       DevicePolicyManager.EXTRA_DEVICE_ADMIN,


                       policyManager.getAdminsComponent()
                   );


                   activateDeviceAdmin.putExtra(
                       DevicePolicyManager.EXTRA_ADD_EXPLANATION,


                       "After activating admin, you will be able to block application uninstallation."
                   );


                   startActivityForResult(
                       activateDeviceAdmin,


                       PolicyManager.DPM_ACTIVATION_REQUEST_CODE
                   );


               }
           }else{
               if (policyManager.isAdminsActive())


                   policyManager.disableAdmin();

           }
       }

        println("Here" +key)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){
        super.onActivityResult(requestCode, resultCode, data)
       if (!policyManager.isAdminsActive()){

           val pref = PreferenceManager.getDefaultSharedPreferences(this)
            val data=   pref.edit().putBoolean("uninstall", false).commit()
           val intent = Intent (this,MainActivity::class.java)
           startActivity(intent)

           println("Herererer" + data)
        }
    }
        override fun onDestroy() {
            super.onDestroy()
            PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this)
        }


}