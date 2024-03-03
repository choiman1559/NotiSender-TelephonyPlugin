package com.noti.plugin.telephony;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

import com.noti.plugin.process.PluginAction;

public class TelecomReceiver extends BroadcastReceiver {

    private static final String PHONE_STATE = "android.intent.action.PHONE_STATE";
    public static final String ACTION_PUSH_MESSAGE_DATA = "push_message_data";
    public static final String ACTION_PUSH_CALL_DATA = "push_call_data";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        SharedPreferences prefs = Application.getSharedPreferences(context);

        if (action != null) switch (action) {
            case PHONE_STATE:
                if (prefs.getBoolean("callReceiveEnabled", false)) {
                    String phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                    String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

                    if (phoneNumber != null && TelephonyManager.EXTRA_STATE_RINGING.equals(state)) {
                        String args = phoneNumber + "|" + getContactNameFromPhoneNumber(context, phoneNumber);
                        PluginAction.responseHostApiInject(context, null, ACTION_PUSH_CALL_DATA, args);
                    }
                }
                break;

            case Telephony.Sms.Intents.SMS_RECEIVED_ACTION:
                if (prefs.getBoolean("messageReceiveEnabled", false)) {
                    SmsMessage message = Telephony.Sms.Intents.getMessagesFromIntent(intent)[0];
                    String address = message.getOriginatingAddress();
                    String args = address + "|" + message + "|" + getContactNameFromPhoneNumber(context, address);
                    PluginAction.responseHostApiInject(context, null, ACTION_PUSH_MESSAGE_DATA, args);
                }
                break;
        }
    }

    public static String getContactNameFromPhoneNumber(Context context, String phoneNumber) {
        try {
            Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
            Cursor cursor = context.getContentResolver().query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);

            if (cursor == null) {
                return "";
            }

            String contactName = "";
            if (cursor.moveToFirst()) {
                int index = cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME);
                if (index > -1) {
                    contactName = cursor.getString(index);
                }
            }

            if (!cursor.isClosed()) {
                cursor.close();
            }

            return contactName;
        } catch (Exception e) {
            return "";
        }
    }
}
