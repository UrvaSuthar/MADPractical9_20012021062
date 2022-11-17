package com.example.madpractical9_20012021062

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Telephony

class SMSBroadcastReceiver : BroadcastReceiver() {

    interface Listener{
        fun onTextReceived( sPhone: String?, sMsg: String?)
    }

    var listener: Listener?=null

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        if(intent.action== Telephony.Sms.Intents.SMS_RECEIVED_ACTION){
            var sPhoneNo = ""
            var sSMSBody = ""
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                for(smsMessage in Telephony.Sms.Intents.getMessagesFromIntent(intent)){
                    sPhoneNo = smsMessage.displayOriginatingAddress
                    sSMSBody += smsMessage.messageBody
                }
                if(listener != null){
                    listener?.onTextReceived(sPhoneNo,sSMSBody)
                }
            }
        }
    }
}