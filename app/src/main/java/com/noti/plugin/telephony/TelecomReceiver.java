package com.noti.plugin.telephony;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.noti.plugin.process.PluginAction;

public class TelecomReceiver extends BroadcastReceiver {

    private static final String PHONE_STATE = "android.intent.action.PHONE_STATE";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        SharedPreferences prefs = Application.getSharedPreferences(context);

        switch (action) {
            case PHONE_STATE:
                if (prefs.getBoolean("callReceiveEnabled", false)) {
                    String phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                    String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

                    if (phoneNumber != null && TelephonyManager.EXTRA_STATE_RINGING.equals(state)) {
                        PluginAction.pushCallData(context, phoneNumber);
                    }
                }
                break;

            case Telephony.Sms.Intents.SMS_RECEIVED_ACTION:
                if (prefs.getBoolean("messageReceiveEnabled", false)) {
                    SmsMessage message = Telephony.Sms.Intents.getMessagesFromIntent(intent)[0];
                    PluginAction.pushMessageData(context, message.getOriginatingAddress(), message.getMessageBody());
                }
                break;
        }
    }
}
