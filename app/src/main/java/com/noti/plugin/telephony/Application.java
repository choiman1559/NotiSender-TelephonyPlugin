package com.noti.plugin.telephony;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;

import com.noti.plugin.Plugin;

public class Application extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initPlugin(this);
    }

    public static void initPlugin(Context context) {
        Plugin plugin = Plugin.init(new PluginResponses());

        plugin.setPluginDescription("Plugin for Receive call & message information");
        plugin.setAppPackageName(context.getPackageName());
        plugin.setSettingClass(SettingActivity.class);
        plugin.setPluginTitle("Telephony Plugin");
        plugin.setRequireSensitiveAPI(false);

        try {
            plugin.setPluginHostInject(SendSmsWorker.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        checkPermission(context);
    }

    public static void checkPermission(Context context) {
        Plugin.getInstance().setPluginReady(checkTelephonyPermission(context));
    }

    public static boolean checkTelephonyPermission(Context context) {
        boolean isPermissionGranted = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] permissions = new String[]{
                    Manifest.permission.SEND_SMS,
                    Manifest.permission.READ_SMS,
                    Manifest.permission.RECEIVE_SMS,
                    Manifest.permission.READ_CALL_LOG,
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.READ_PHONE_STATE
            };

            for(String permission : permissions) {
                if (context.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                    isPermissionGranted = false;
                }
            }
        }

        return isPermissionGranted;
    }

    public static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(context.getPackageName() + "_preferences", MODE_PRIVATE);
    }
}
