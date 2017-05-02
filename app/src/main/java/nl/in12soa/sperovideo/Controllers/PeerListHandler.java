package nl.in12soa.sperovideo.Controllers;

import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ahmad on 3/1/2017.
 */

public class PeerListHandler implements WifiP2pManager.PeerListListener {
    public PeerListAdapter peerListAdapter;
    public List<WifiP2pDevice> peersList = new ArrayList<WifiP2pDevice>();
    public PeerListHandler(PeerListAdapter pla){
        super();
        peerListAdapter = pla;
    }
    @Override
    public void onPeersAvailable(WifiP2pDeviceList peers) {
        peerListAdapter.peerAdapter.clear();
        peersList.addAll(peers.getDeviceList());
        for (WifiP2pDevice dev : peers.getDeviceList()) {
            peerListAdapter.peerArray.add(dev.deviceName);
            peerListAdapter.peerAdapter.notifyDataSetChanged();
        }
    }
}
