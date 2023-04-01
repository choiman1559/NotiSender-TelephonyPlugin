package com.noti.plugin.telephony;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

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
    MaterialButton Test_RCS;

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

        setButtonCompleted(Permit_Sms);
        Application.checkPermission(SettingActivity.this);
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Application.checkPermission(this);
        SharedPreferences prefs = Application.getSharedPreferences(this);

        Permit_Sms = findViewById(R.id.Permit_Sms);
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

        Test_RCS = findViewById(R.id.Test_RCS);
        Test_RCS.setVisibility(BuildConfig.DEBUG ? View.VISIBLE : View.GONE);
        Test_RCS.setOnClickListener(v -> {
            Uri uri = Uri.parse("content://im/chat");
            Cursor cursor = getContentResolver().query(uri ,null, null, null, "date DESC LIMIT 40 OFFSET 0");
            if(cursor != null){
                int columnCount = cursor.getColumnCount();
                if (cursor.moveToFirst()) {
                    for(int i = 0; i < columnCount; i++) {
                        Log.e("__T", "[" + i + "=" + cursor.getColumnName(i) + "] " + cursor.getString(i));
                    }
                }
                cursor.close();
            }
        });

        if(Application.checkTelephonyPermission(this)) {
            setButtonCompleted(Permit_Sms);
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

    @SuppressLint("SetTextI18n")
    void setButtonCompleted(MaterialButton button) {
        button.setEnabled(false);
        button.setText("Sms Access Permitted");
        button.setIcon(AppCompatResources.getDrawable(SettingActivity.this, R.drawable.baseline_check_24));
    }
}
