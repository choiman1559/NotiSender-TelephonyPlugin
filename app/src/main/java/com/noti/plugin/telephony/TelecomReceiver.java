package com.noti.plugin.telephony;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

import com.noti.plugin.process.PluginAction;

public class TelecomReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences prefs = Application.getSharedPreferences(context);
        if (intent.getAction().equals("android.intent.action.PHONE_STATE")) {
            if(prefs.getBoolean("callReceiveEnabled", false)) {
                String phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

                if (phoneNumber != null && TelephonyManager.EXTRA_STATE_RINGING.equals(state)) {
                    PluginAction.pushCallData(context, phoneNumber);
                }
            }
        } else if (intent.getAction().equals("android.intent.action.NEW_OUTGOING_")) {
            if(prefs.getBoolean("messageReceiveEnabled", false)) {
                Bundle bundle = intent.getExtras();
                Object[] obs = (Object[]) bundle.get("pdus");
                SmsMessage[] messages = new SmsMessage[obs.length];

                for (int i = 0; i < obs.length; i++) {
                    messages[i] = SmsMessage.createFromPdu((byte[]) obs[i]);
                }

                if (messages.length > 0) {
                    String sender = messages[0].getOriginatingAddress();
                    String content = messages[0].getMessageBody();
                    PluginAction.pushMessageData(context, sender, content);
                }
            }
        }
    }
}
