package org.kivy.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class GenericBroadcastReceiver extends BroadcastReceiver {
    GenericBroadcastReceiverCallback listener;

    public GenericBroadcastReceiver(GenericBroadcastReceiverCallback listener2) {
        this.listener = listener2;
    }

    public void onReceive(Context context, Intent intent) {
        this.listener.onReceive(context, intent);
    }
}
