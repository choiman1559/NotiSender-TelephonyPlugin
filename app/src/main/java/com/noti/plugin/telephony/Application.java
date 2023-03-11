package com.noti.plugin.telephony;

import android.content.Context;

import com.noti.plugin.Plugin;

public class Application extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        initPlugin(this);
    }

    public static void initPlugin(Context context) {
        Plugin plugin = Plugin.init();

        plugin.setPluginDescription("Plugin for Receive call & message information");
        plugin.setAppPackageName(context.getPackageName());
        plugin.setPluginReady(true);
        plugin.setSettingClass(SettingActivity.class);
    }
}
