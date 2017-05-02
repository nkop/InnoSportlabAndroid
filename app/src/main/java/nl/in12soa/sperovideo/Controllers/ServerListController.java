package nl.in12soa.sperovideo.Controllers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.widget.TextView;
import nl.in12soa.sperovideo.ServerListActivity;

/**
 * Created by Ahmad on 3/13/2017.
 */

public class ServerListController extends BroadcastReceiver {

    TextView tvlog;
    ServerListActivity mActivity;

    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    public PeerListHandler mPeerHandler;
    public PeerListAdapter pla;
    private boolean isconnected = false;
    public ServerListController(TextView logp, ServerListActivity act, WifiP2pManager.Channel channelp, WifiP2pManager mgrp, PeerListAdapter plap){
        super();
        tvlog = logp;
        mActivity = act;
        mManager = mgrp;
        mChannel = channelp;
        pla = plap;
        mPeerHandler = new PeerListHandler(pla);

    }

    public void peerDiscovery(){
        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                tvlog.setText("Peer discovery succesfull");

            }

            @Override
            public void onFailure(int reasonCode) {
                tvlog.setText("Peer discovery failed");
            }
        });
    }

    public void connect(WifiP2pDevice device) {
        // Picking the first device found on the network.
        final WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = device.deviceAddress;
        config.wps.setup = WpsInfo.PBC;
        mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                tvlog.setText("Connected, wait for connectioninfoavailable");
                isconnected = true;
            }

            @Override
            public void onFailure(int reason) {
                tvlog.setText("Connection failed");
            }
        });
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            // Check to see if Wi-Fi is enabled and notify appropriate activity
            tvlog.setText(action);
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            // Call WifiP2pManager.requestPeers() to get a list of current peers
            tvlog.setText(action);
            if (mManager != null) {
                mManager.requestPeers(mChannel, mPeerHandler);
            }
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            if(isconnected) {
                mManager.requestConnectionInfo(mChannel, mActivity);
            }
            tvlog.setText(action);
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            // Respond to this device's wifi state changing
            tvlog.setText(action);
        }
    }
}
