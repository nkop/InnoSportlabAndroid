package nl.in12soa.sperovideo.Models;

import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ahmadrahimi on 5/12/17.
 */

public class PeerListHandler implements WifiP2pManager.PeerListListener {
    public List<WifiP2pDevice> peersList = new ArrayList<WifiP2pDevice>();
    public PeerListHandler(){
        super();
    }
    @Override
    public void onPeersAvailable(WifiP2pDeviceList peers) {

        for (WifiP2pDevice dev : peers.getDeviceList()) {
            System.out.println(dev.isServiceDiscoveryCapable());
        }
    }
}
