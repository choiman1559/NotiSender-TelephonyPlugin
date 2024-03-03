package com.noti.plugin.telephony;

import android.content.Context;

import com.noti.plugin.data.NotificationData;
import com.noti.plugin.data.PairDeviceInfo;
import com.noti.plugin.listener.PluginResponse;

public class PluginResponses implements PluginResponse {
    @Override
    public void onReceiveRemoteActionRequest(Context context, PairDeviceInfo device, String type, String args) {

    }

    @Override
    public void onReceiveRemoteDataRequest(Context context, PairDeviceInfo device, String type) {

    }

    @Override
    public void onReceiveException(Context context, Exception e) {
        e.printStackTrace();
    }

    @Override
    public void onNotificationReceived(Context context, NotificationData notification) {

    }
}
