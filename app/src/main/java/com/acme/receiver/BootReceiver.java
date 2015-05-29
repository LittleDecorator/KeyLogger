package com.acme.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.acme.util.Utils;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Utils.startTaskService();
        Utils.startSenderService();
    }
}
