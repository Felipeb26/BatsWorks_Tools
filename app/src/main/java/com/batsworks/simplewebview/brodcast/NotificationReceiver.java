package com.batsworks.simplewebview.brodcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.batsworks.simplewebview.YoutubeDownload;

import java.util.Map;

public class NotificationReceiver extends BroadcastReceiver {

    private static final String ACTION_PAUSE = "actionpause";
    private static final String ACTION_PLAY = "actionplay";
    private static final String RESTART = "reopen";

    @Override
    public void onReceive(Context context, Intent intent) {
        Object action = intent.getAction();

        Intent serviceIntent = new Intent(context, YoutubeDownload.class);

        if (action == null)
            return;

        if (action.equals(ACTION_PAUSE)) {
            serviceIntent.putExtra("ActionName", ACTION_PAUSE).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(serviceIntent);
            return;
        }

        if (action.equals(ACTION_PLAY)) {
            serviceIntent.putExtra("ActionName", ACTION_PLAY).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(serviceIntent);
            return;
        }

        if (action instanceof Map) {
            serviceIntent.putExtra("ActionName", RESTART).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(serviceIntent);
            return;
        }

    }

}
