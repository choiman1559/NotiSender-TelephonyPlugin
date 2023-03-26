package com.noti.plugin.telephony;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.provider.Telephony;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.noti.plugin.process.PluginAction;

public class NotiListenerService extends NotificationListenerService {
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);
        SharedPreferences prefs = Application.getSharedPreferences(this);

        if(prefs.getBoolean("messageReceiveEnabled", false) && sbn.getPackageName().equals(Telephony.Sms.getDefaultSmsPackage(this))) {
            Cursor cursor = getContentResolver().query(Telephony.Sms.Inbox.CONTENT_URI, new String[]{"address", "body"}, null, null, "date DESC LIMIT 1");
            cursor.moveToFirst();
            String address = cursor.getString(cursor.getColumnIndexOrThrow("address"));
            String message = cursor.getString(cursor.getColumnIndexOrThrow("body"));
            cursor.close();

            if(BuildConfig.DEBUG) Log.d("NotificationListener", "SMS incoming" + address + ": " + message);
            PluginAction.pushMessageData(this, address, message);
        }
    }
}
