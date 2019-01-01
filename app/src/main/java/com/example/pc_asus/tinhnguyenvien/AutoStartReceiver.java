package com.example.pc_asus.tinhnguyenvien;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AutoStartReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("abc","zô Receiver.....");
        Intent i = new Intent();
        i.setClassName(context.getPackageName(), CheckConnectionService.class.getName());
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startService(i);
    }
}