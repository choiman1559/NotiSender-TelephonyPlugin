package com.noti.plugin.telephony;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.telephony.SmsManager;
import android.widget.Toast;

import com.noti.plugin.listener.PluginResponse;

public class PluginResponses implements PluginResponse {
    @Override
    public void onReceiveRemoteAction(Context context, String type, String args) {
        if (type.equals("send_sms") && Application.getSharedPreferences(context).getBoolean("messageReceiveEnabled", true)) {
            String[] data = args.split("\\|");
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(data[0], null, data[1], null, null);
            new Handler(Looper.getMainLooper()).postDelayed(() -> Toast.makeText(context, "Reply message by NotiSender\nfrom " + data[2], Toast.LENGTH_SHORT).show(), 0);
        }
    }

    @Override
    public void onReceiveRemoteData(Context context, String type) {

    }

    @Override
    public void onReceiveException(Context context, Exception e) {
        e.printStackTrace();
    }
}
