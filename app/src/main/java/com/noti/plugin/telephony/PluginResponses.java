package com.noti.plugin.telephony;

import android.content.Context;

import com.noti.plugin.listener.PluginResponse;

public class PluginResponses implements PluginResponse {
    @Override
    public void onReceiveRemoteAction(Context context, String type, String args) {

    }

    @Override
    public void onReceiveRemoteData(Context context, String type) {

    }

    @Override
    public void onReceiveException(Context context, Exception e) {
        e.printStackTrace();
    }
}
