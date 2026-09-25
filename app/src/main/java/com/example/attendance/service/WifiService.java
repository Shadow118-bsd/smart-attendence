package com.example.attendance.service;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.Log;

import com.example.attendance.data.model.request.WifiEvidence;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

public class WifiService {
    private static final String TAG = "WifiService";
    private final Context context;

    public WifiService(Context context) {
        this.context = context.getApplicationContext();
    }

    public WifiEvidence collectWifiEvidence() {
        boolean isConnected = false;
        String ssid = "UNKNOWN";
        String bssid = "UNKNOWN";
        String localIp = getLocalIpAddress();

        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm != null) {
                Network activeNetwork = cm.getActiveNetwork();
                if (activeNetwork != null) {
                    NetworkCapabilities nc = cm.getNetworkCapabilities(activeNetwork);
                    if (nc != null && nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        isConnected = true;
                    }
                }
            }

            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            if (wifiManager != null && isConnected) {
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                if (wifiInfo != null) {
                    ssid = wifiInfo.getSSID();
                    if (ssid != null && ssid.startsWith("\"") && ssid.endsWith("\"")) {
                        ssid = ssid.substring(1, ssid.length() - 1);
                    }
                    bssid = wifiInfo.getBSSID();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error collecting Wi-Fi evidence: " + e.getMessage());
        }

        return new WifiEvidence(isConnected, ssid, bssid, localIp);
    }

    public boolean isInternetReachable() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            Network activeNetwork = cm.getActiveNetwork();
            if (activeNetwork != null) {
                NetworkCapabilities nc = cm.getNetworkCapabilities(activeNetwork);
                return nc != null && nc.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
            }
        }
        return false;
    }

    private String getLocalIpAddress() {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        if (sAddr != null && sAddr.indexOf(':') < 0) { // IPv4
                            return sAddr;
                        }
                    }
                }
            }
        } catch (Exception ignored) { }
        return "127.0.0.1";
    }
}
