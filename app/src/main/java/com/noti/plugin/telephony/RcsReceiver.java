package com.noti.plugin.telephony;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;

import com.noti.plugin.process.PluginAction;

public class RcsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences prefs = Application.getSharedPreferences(context);
        if (prefs.getBoolean("messageReceiveEnabled", false)) {

            if (intent.getAction().equals("com.samsung.rcs.framework.instantmessaging.action.RECEIVE_NEW_MESSAGE")) {
                Uri uri = Uri.parse("content://im/chat");
                Cursor cursor = context.getContentResolver().query(uri, null, null, null, "date DESC LIMIT 40 OFFSET 0");
                if (cursor != null) {
                    String address = cursor.getString(3);
                    String message = cursor.getString(9);

                    PluginAction.pushMessageData(context, address, message);
                    cursor.close();
                }
            }
        }
    }
}
