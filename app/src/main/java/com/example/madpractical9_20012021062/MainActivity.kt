package com.example.madpractical9_20012021062

import android.Manifest
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Telephony
import android.telephony.SmsManager
import android.widget.ListView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PackageManagerCompat
import com.example.madpractical9_20012021062.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    val SMS_PERMISSION_CODE = 110

    private val isSMSReadPermission:Boolean
        get() = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED
    private val isSMSWritePermission:Boolean
        get() = ContextCompat.checkSelfPermission(this,Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED

    private lateinit var smsrecevier:SMSBroadcastReceiver
    private lateinit var al : ArrayList<SMSView>
    private lateinit var lv : ListView

    private fun checkRequestPermission():Boolean{
        return isSMSReadPermission && isSMSWritePermission
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        al = ArrayList()
        lv = binding.listview

        if(checkRequestPermission()){
            loadSMSInbox()
        }
        smsrecevier = SMSBroadcastReceiver()
        registerReceiver(smsrecevier, IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION))
        smsrecevier.listener=ListnerImplement()

    }

    fun sendsms(sPhone : String?, sMsg : String?){
        if(!checkRequestPermission()){
            //toast
            return
        }
        else{
            checkRequestPermission()
        }
        val smsmanager = SmsManager.getDefault()
        if(smsmanager!=null){
            smsmanager.sendTextMessage(sPhone, null, sMsg, null, null)
        }
    }
    inner class ListnerImplement:SMSBroadcastReceiver.Listener{
        override fun onTextReceived(sPhone: String?, sMsg: String?) {
            val builder: AlertDialog.Builder = AlertDialog.Builder(this@MainActivity)
            builder.setTitle("New SMS Recevied")
            builder.setMessage("$sPhone\n$sMsg")
            builder.setCancelable(true)
            builder.show()
            loadSMSInbox()
        }
    }

    private fun requestSMSPermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(this,android.Manifest.permission.READ_SMS)){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_SMS,android.Manifest.permission.SEND_SMS),SMS_PERMISSION_CODE)
        }
    }

    private fun loadSMSInbox(){
        if(!checkRequestPermission()){
            return
        }
        else{
            checkRequestPermission()
        }
        val uriSMS = Uri.parse("content://sms/inbox")
        val c = contentResolver.query(uriSMS, null, null, null, null)
        al.clear()
        while(c!!.moveToNext()){
            al.add(SMSView(c.getString(2),c.getString(12)))
        }
        lv.adapter = SMSViewAdapter(this,al)
    }
}