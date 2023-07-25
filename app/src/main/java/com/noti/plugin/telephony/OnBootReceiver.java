package com.noti.plugin.telephony;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.noti.plugin.Plugin;

public class OnBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED) && Plugin.getInstanceAllowNull() == null) {
            Application.initPlugin(context);
        }
    }
}
