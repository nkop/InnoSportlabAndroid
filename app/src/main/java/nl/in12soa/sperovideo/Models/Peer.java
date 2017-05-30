package nl.in12soa.sperovideo.Models;

import android.net.wifi.p2p.WifiP2pDevice;

/**
 * Created by ahmadrahimi on 5/11/17.
 */

public class Peer {

    private String name;
    private String type;
    private WifiP2pDevice device;

    public Peer(WifiP2pDevice wifiP2pDevice, String type){
        this.name = wifiP2pDevice.deviceName;
        this.type = type;
        this.device = wifiP2pDevice;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public WifiP2pDevice getDevice() {
        return device;
    }
}
