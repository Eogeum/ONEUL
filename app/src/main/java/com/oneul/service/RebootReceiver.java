package com.oneul.service;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class RebootReceiver extends BroadcastReceiver {
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, RealService.class));
    }
}