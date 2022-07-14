package com.example.floatwindowservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootBroadcastReceiver extends BroadcastReceiver {
    static final String ACTION = "android.intent.action.BOOT_COMPLETED";
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        if(intent.getAction().equals(ACTION)){
            Intent intent1=new Intent(context,FloatWindowService.class);
            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startService(intent1);
        }
    }
}
 
