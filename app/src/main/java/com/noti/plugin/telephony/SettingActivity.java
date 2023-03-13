package com.noti.plugin.telephony;

import android.Manifest;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class SettingActivity extends AppCompatActivity {

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
            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        SharedPreferences prefs = Application.getSharedPreferences(this);

        if(!Application.checkTelephonyPermission(this)) {
            String[] permissions = new String[]{
                    android.Manifest.permission.SEND_SMS,
                    android.Manifest.permission.READ_SMS,
                    android.Manifest.permission.RECEIVE_SMS,
                    android.Manifest.permission.READ_CALL_LOG,
                    Manifest.permission.READ_PHONE_STATE
            };

            startPermissionPermit.launch(permissions);
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
}
