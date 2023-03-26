package com.noti.plugin.telephony;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.telephony.TelephonyManager;

import com.noti.plugin.process.PluginAction;

public class TelecomReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        SharedPreferences prefs = Application.getSharedPreferences(context);

        if (action.equals("android.intent.action.PHONE_STATE")) {
            if(prefs.getBoolean("callReceiveEnabled", false)) {
                String phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

                if (phoneNumber != null && TelephonyManager.EXTRA_STATE_RINGING.equals(state)) {
                    PluginAction.pushCallData(context, phoneNumber);
                }
            }
        }
    }
}
