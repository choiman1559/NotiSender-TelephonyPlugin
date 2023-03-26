package com.noti.plugin.telephony;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class SettingActivity extends AppCompatActivity {

    MaterialButton Permit_Sms;
    MaterialButton Permit_Alarm;

    ActivityResultLauncher<String[]> startPermissionPermit = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
        for(Boolean isGranted : result.values()) {
            if(!isGranted) {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
                builder
                        .setTitle("Warning")
                        .setMessage("Unable to obtain permission: Please allow permission and try again.")
                        .setCancelable(false)
                        .setPositiveButton("Close", (dialog, which) -> this.finish())
                        .create()
                        .show();
                return;
            }
        }

        setButtonCompleted(Permit_Sms, "Sms Access Permitted");
        Application.checkPermission(SettingActivity.this);
    });

    ActivityResultLauncher<Intent> startAlarmAccessPermit = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if(Application.checkNotificationPermission(this)) {
            Application.checkPermission(SettingActivity.this);
            setButtonCompleted(Permit_Alarm, "Alarm Access Permitted");
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Application.checkPermission(this);
        SharedPreferences prefs = Application.getSharedPreferences(this);

        Permit_Sms = findViewById(R.id.Permit_Sms);
        Permit_Alarm = findViewById(R.id.Permit_Alarm);

        Permit_Sms.setOnClickListener(v -> {
            String[] permissions = new String[]{
                    android.Manifest.permission.SEND_SMS,
                    android.Manifest.permission.READ_SMS,
                    android.Manifest.permission.RECEIVE_SMS,
                    android.Manifest.permission.READ_CALL_LOG,
                    Manifest.permission.READ_PHONE_STATE
            };

            startPermissionPermit.launch(permissions);
        });

        Permit_Alarm.setOnClickListener(v -> startAlarmAccessPermit.launch(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")));

        if(Application.checkTelephonyPermission(this)) {
            setButtonCompleted(Permit_Sms, "Sms Access Permitted");
        }

        if(Application.checkNotificationPermission(this)) {
            setButtonCompleted(Permit_Alarm, "Alarm Access Permitted");
        }

        SwitchMaterial callReceiveEnabled = findViewById(R.id.callReceiveEnabled);
        SwitchMaterial messageReceiveEnabled = findViewById(R.id.messageReceiveEnabled);

        callReceiveEnabled.setChecked(prefs.getBoolean("callReceiveEnabled", false));
        messageReceiveEnabled.setChecked(prefs.getBoolean("messageReceiveEnabled", false));

        callReceiveEnabled.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("callReceiveEnabled", isChecked);
            editor.apply();
        });

        messageReceiveEnabled.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("messageReceiveEnabled", isChecked);
            editor.apply();
        });

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener((v) -> this.finish());
    }

    void setButtonCompleted(MaterialButton button, String message) {
        button.setEnabled(false);
        button.setText(message);
        button.setIcon(AppCompatResources.getDrawable(SettingActivity.this, R.drawable.baseline_check_24));
    }
}
