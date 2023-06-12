package com.batsworks.simplewebview.config;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;

public class NetworkConfig {

    Context context;

    public NetworkConfig(Context context) {
        this.context = context;
    }

    private void net() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        //should check null because in airplane mode it will be null
        NetworkCapabilities nc = cm.getNetworkCapabilities(cm.getActiveNetwork());
        int downSpeed = nc.getLinkDownstreamBandwidthKbps();
        int upSpeed = nc.getLinkUpstreamBandwidthKbps();
    }

}
