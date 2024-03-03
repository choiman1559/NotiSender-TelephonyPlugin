package com.noti.plugin.telephony;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.telephony.SmsManager;
import android.widget.Toast;

import com.noti.plugin.data.NotificationData;
import com.noti.plugin.data.PairDeviceInfo;
import com.noti.plugin.listener.PluginResponse;

public class PluginResponses implements PluginResponse {
    @Override
    public void onReceiveRemoteActionRequest(Context context, PairDeviceInfo device, String type, String args) {
        SharedPreferences prefs = Application.getSharedPreferences(context);
        if (type.equals("send_sms") && (prefs.getBoolean("messageReceiveEnabled", false) || prefs.getBoolean("callReceiveEnabled", false))) {
            String[] data = args.split("\\|");
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(data[0], null, data[1], null, null);
            new Handler(Looper.getMainLooper()).postDelayed(() -> Toast.makeText(context, "Reply message by NotiSender\nfrom " + data[2], Toast.LENGTH_SHORT).show(), 0);
        }
    }

    @Override
    public void onReceiveRemoteDataRequest(Context context, PairDeviceInfo device, String type) {

    }

    @Override
    public void onReceiveException(Context context, Exception e) {
        e.printStackTrace();
    }

    @Override
    public void onNotificationReceived(Context context, NotificationData notification) {

    }
}
