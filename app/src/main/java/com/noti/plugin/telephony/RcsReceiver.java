package com.noti.plugin.telephony;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.noti.plugin.process.PluginAction;

import org.jetbrains.annotations.TestOnly;

public class RcsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences prefs = Application.getSharedPreferences(context);
        if (prefs.getBoolean("messageReceiveEnabled", false)) {
            if (intent.getAction().equals("com.samsung.rcs.framework.instantmessaging.action.RECEIVE_NEW_MESSAGE")) {
                if(prefs.getBoolean("tryNativeMethod", false)) {
                    processRcsReceiverButNativeMethod(context, intent);
                } else {
                    processRcsReceiver(context, intent, true);
                }
            }
        }
    }

    @TestOnly
    void processRcsReceiverButNativeMethod(Context context, Intent intent) {
        String chatId = intent.getStringExtra("chat_id");
        int messageType = intent.getIntExtra("message_type", -1);
        int messageDirection = intent.getIntExtra("message_direction", 0);
        String messageImdnId = intent.getStringExtra("message_imdn_id");
        String referenceMessageImdnId = intent.getStringExtra("reference_message_imdn_id");
        String referenceMessageType = intent.getStringExtra("reference_message_type");
        String referenceMessageValue = intent.getStringExtra("reference_message_value");

        Log.d("CS/EvReceiver[IM]", "handleNewMessage(RECEIVE_NEW_MESSAGE) : imdnId = " + messageImdnId + ", direction = " + messageDirection + ", messageType = " + messageType + ", chatId = " + chatId + ", reOriginalKey = " + referenceMessageImdnId + ", reType = " + referenceMessageType + ", reValue = " + referenceMessageValue);

        if (!chatId.isEmpty() && !messageImdnId.isEmpty()) {
            String dbQuerySelector = "(imdn_message_id = ? AND direction = ? AND chat_id = ?)";
            Uri parse = Uri.parse("content://com.samsung.rcs.im/message/");
            String[] strArr = new String[]{chatId, String.valueOf(messageDirection), messageImdnId};

            Cursor cursor = context.getContentResolver().query(parse, null, dbQuerySelector, strArr, null);
            if (cursor != null) {
                try {
                    if (cursor.moveToNext()) {
                        String remoteUri = getStringFromCursor(cursor, "remote_uri");
                        if (-1 != remoteUri.indexOf(58)) {
                            int length = remoteUri.length();
                            int indexOf = remoteUri.indexOf(58);
                            remoteUri = remoteUri.substring(indexOf + 1, -1 != remoteUri.indexOf(64) ? remoteUri.indexOf(64) : length);
                        }

                        int length2 = remoteUri.length();
                        if (!remoteUri.isEmpty()) {
                            final String KR_ADDRESS_PREFIX_TYPE1 = "+82";
                            final String KR_ADDRESS_PREFIX_TYPE2 = "0082";
                            final String KR_ADDRESS_PREFIX_TYPE3 = "+822";
                            final String KR_ADDRESS_PREFIX_TYPE4 = "00822";

                            if (remoteUri.startsWith(KR_ADDRESS_PREFIX_TYPE1)) {
                                if (-1 != remoteUri.indexOf(43)) {
                                    int indexOf2 = remoteUri.indexOf(43);
                                    if (length2 >= 6 && length2 <= 11 && (!remoteUri.startsWith(KR_ADDRESS_PREFIX_TYPE3) || length2 != 11)) {
                                        remoteUri = remoteUri.substring(indexOf2 + 3, length2);
                                    } else {
                                        remoteUri = "0" + remoteUri.substring(indexOf2 + 3, length2);
                                    }
                                }
                            } else if (remoteUri.startsWith(KR_ADDRESS_PREFIX_TYPE2) && -1 != remoteUri.indexOf(48)) {
                                int indexOf3 = remoteUri.indexOf(48);
                                if (length2 >= 7 && length2 <= 12 && (!remoteUri.startsWith(KR_ADDRESS_PREFIX_TYPE4) || length2 != 12)) {
                                    remoteUri = remoteUri.substring(indexOf3 + 4, length2);
                                } else {
                                    remoteUri = "0" + remoteUri.substring(indexOf3 + 4, length2);
                                }
                            }
                        }

                        int indexOf4 = remoteUri.indexOf(59);
                        String address = indexOf4 > 0 ? remoteUri.substring(0, indexOf4) : remoteUri;
                        String body = getStringFromCursor(cursor, "body");

                        PluginAction.pushMessageData(context, address, TelecomReceiver.getContactNameFromPhoneNumber(context, address), body);
                        return;
                    }
                } catch (Throwable th2) {
                    try {
                        cursor.close();
                    } catch (Throwable th3) {
                        th2.addSuppressed(th3);
                    }
                    throw th2;
                }
            }
            Log.d("CS/EvReceiver[IM]", "handleNewMessage rcs chat message query returns null/empty.");
        } else {
            Log.d("CS/EvReceiver[IM]", "handleNewMessage RECEIVE_NEW_MESSAGE Chat ID is Null");
        }
    }

    void processRcsReceiver(Context context, Intent intent, boolean isFirstTry) {
        try {
            Thread.sleep(3000);
            Uri uri = Uri.parse("content://im/chat");
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, "date DESC LIMIT 40 OFFSET 0");

            if (cursor != null && cursor.moveToFirst()) {
                String address = getStringFromCursor(cursor, "address");
                String message = getStringFromCursor(cursor, "body");

                String imdnIdFromCursor = getStringFromCursor(cursor, "imdn_message_id");
                String imdnIdFromIntent = intent.getStringExtra("message_imdn_id");

                if(BuildConfig.DEBUG) {
                    String chatIdFromCursor = getStringFromCursor(cursor, "session_id");
                    String chatIdFromIntent = intent.getStringExtra("chat_id");

                    Log.i("CHAT_ID", "Cursor: " + chatIdFromCursor +  " Intent: " + chatIdFromIntent);
                    Log.i("MESSAGE_ID", "Cursor: " + imdnIdFromCursor +  " Intent: " + imdnIdFromIntent);
                }

                if(imdnIdFromCursor.equals(imdnIdFromIntent)) {
                    PluginAction.pushMessageData(context, address, TelecomReceiver.getContactNameFromPhoneNumber(context, address), message);
                } else if(isFirstTry) {
                    processRcsReceiver(context, intent, false);
                }

                cursor.close();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    String getStringFromCursor(Cursor cursor, String key) {
        int indexCursor = cursor.getColumnIndex(key);
        return cursor.getString(indexCursor);
    }
}
