package com.android.nana.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.android.nana.main.MainActivity;

/**
 * Created by lenovo on 2017/10/20.
 */

public class NotificationReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        Intent intentAppointment = new Intent();
        intentAppointment.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intentAppointment.setClass(context, MainActivity.class);
        context.startActivity(intentAppointment);
    }
}
