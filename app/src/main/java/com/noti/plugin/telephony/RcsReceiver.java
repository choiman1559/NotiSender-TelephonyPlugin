package com.noti.plugin.telephony;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.noti.plugin.process.PluginAction;

public class RcsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences prefs = Application.getSharedPreferences(context);
        if (prefs.getBoolean("messageReceiveEnabled", false)) {
            if (intent.getAction().equals("com.samsung.rcs.framework.instantmessaging.action.RECEIVE_NEW_MESSAGE")) {
                processRcsReceiver(context, intent, true);
            }
        }
    }

    void processRcsReceiver(Context context, Intent intent, boolean isFirstTry) {
        try {
            Thread.sleep(3000);
            Uri uri = Uri.parse("content://im/chat");
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, "date DESC LIMIT 40 OFFSET 0");

            if (cursor != null && cursor.moveToFirst()) {
                String address = getStringFromCursor(cursor, "address");
                String message = getStringFromCursor(cursor, "body");

                String imdnIdFromCursor = getStringFromCursor(cursor, "imdn_message_id");
                String imdnIdFromIntent = intent.getStringExtra("message_imdn_id");

                if(BuildConfig.DEBUG) {
                    String chatIdFromCursor = getStringFromCursor(cursor, "session_id");
                    String chatIdFromIntent = intent.getStringExtra("chat_id");

                    Log.i("CHAT_ID", "Cursor: " + chatIdFromCursor +  " Intent: " + chatIdFromIntent);
                    Log.i("MESSAGE_ID", "Cursor: " + imdnIdFromCursor +  " Intent: " + imdnIdFromIntent);
                }

                if(imdnIdFromCursor.equals(imdnIdFromIntent)) {
                    PluginAction.pushMessageData(context, address, TelecomReceiver.getContactNameFromPhoneNumber(context, address), message);
                } else if(isFirstTry) {
                    processRcsReceiver(context, intent, false);
                }

                cursor.close();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    String getStringFromCursor(Cursor cursor, String key) {
        int indexCursor = cursor.getColumnIndex(key);
        return cursor.getString(indexCursor);
    }
}
