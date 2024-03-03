package com.noti.plugin.telephony;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.telephony.SmsManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.noti.plugin.listener.PluginHostInject;

public class SendSmsWorker extends PluginHostInject {
    @Override
    public void onHostInject(Context context, @NonNull String dataType, @Nullable String deviceInfo, @Nullable String arg) {
        super.onHostInject(context, dataType, deviceInfo, arg);
        SharedPreferences prefs = Application.getSharedPreferences(context);

        if (dataType.equals(TelecomReceiver.ACTION_REQUEST_SEND_SMS) &&
                (arg != null && deviceInfo != null) &&
                (prefs.getBoolean("messageReceiveEnabled", false) || prefs.getBoolean("callReceiveEnabled", false))) {

            String[] data = arg.split("\\|");
            String[] device = deviceInfo.split("\\|");

            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(data[0], null, data[1], null, null);
            new Handler(Looper.getMainLooper()).postDelayed(() -> Toast.makeText(context, "Reply message by NotiSender\nfrom " + device[1], Toast.LENGTH_SHORT).show(), 0);
        }
    }
}
