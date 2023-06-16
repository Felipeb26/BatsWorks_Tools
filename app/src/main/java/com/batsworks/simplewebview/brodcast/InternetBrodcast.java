package com.batsworks.simplewebview.brodcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import com.batsworks.simplewebview.config.internet.CheckInternet;

public class InternetBrodcast extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        CheckInternet.Status status = CheckInternet.networkInfo(context);
        if (status.equals(CheckInternet.Status.UNAVALIABLE)) {
            Toast.makeText(context.getApplicationContext(), "Internet não está habilitada poderá gerar instabilidade", Toast.LENGTH_SHORT).show();
        }
    }

}
