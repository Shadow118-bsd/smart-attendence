package com.example.attendance.data.model.request;

public class WifiEvidence {
    private boolean connected;
    private String ssid;
    private String bssid;
    private String localIp;

    public WifiEvidence(boolean connected, String ssid, String bssid, String localIp) {
        this.connected = connected;
        this.ssid = ssid;
        this.bssid = bssid;
        this.localIp = localIp;
    }

    public boolean isConnected() { return connected; }
    public String getSsid() { return ssid; }
    public String getBssid() { return bssid; }
    public String getLocalIp() { return localIp; }
}
