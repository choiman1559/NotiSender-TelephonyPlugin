package com.noti.plugin.telephony;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.NotificationManagerCompat;

import com.noti.plugin.Plugin;
import com.noti.plugin.process.PluginAction;

import java.util.Set;

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

        checkPermission(context);
    }

    public static void checkPermission(Context context) {
        Plugin.getInstance().setPluginReady(checkTelephonyPermission(context) && checkNotificationPermission(context));
    }

    public static boolean checkTelephonyPermission(Context context) {
        boolean isPermissionGranted = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] permissions = new String[]{
                    Manifest.permission.SEND_SMS,
                    Manifest.permission.READ_SMS,
                    Manifest.permission.RECEIVE_SMS,
                    Manifest.permission.READ_CALL_LOG,
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

    public static boolean checkNotificationPermission(Context context) {
        boolean isPermissionGranted = true;

        Set<String> sets = NotificationManagerCompat.getEnabledListenerPackages(context);
        if (!sets.contains(context.getPackageName())) {
            isPermissionGranted = false;
        }

        return isPermissionGranted;
    }

    public static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(context.getPackageName() + "_preferences", MODE_PRIVATE);
    }
}
