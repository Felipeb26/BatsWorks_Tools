package com.batsworks.simplewebview.config.internet;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class CheckInternet {

    public static Status networkInfo(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (networkInfo == null) {
            return Status.UNAVALIABLE;
        } else {
            return Status.AVALIABLE;
        }
    }

    public enum Status {
        UNAVALIABLE("unavaliable"),
        AVALIABLE("avaliable");

        private final String status;

        Status(String status) {
            this.status = status;
        }

        public String getStatus() {
            return status;
        }
    }

}
