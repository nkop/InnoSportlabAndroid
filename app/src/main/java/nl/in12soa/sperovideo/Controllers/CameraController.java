package nl.in12soa.sperovideo.Controllers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;

import java.net.InetAddress;

import arrcreations.prototype1_project.CameraActivity;
import arrcreations.prototype1_project.Client.ClientService;
import nl.in12soa.sperovideo.CameraActivity;
import nl.in12soa.sperovideo.Services.ClientService;

/**
 * Created by Ahmad on 3/1/2017.
 */

public class CameraController extends BroadcastReceiver {
    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private CameraActivity mActivity;
    public PeerListHandler mPeerHandler;
    public PeerListAdapter pla;
    public ClientService clientService;
    public Context ctx;
    private boolean isconnected = false;
    public CameraController(WifiP2pManager manager, WifiP2pManager.Channel channel,
                             CameraActivity activity, PeerListAdapter pla, Context ctxp) {
        super();
        this.pla = pla;
        mPeerHandler = new PeerListHandler(pla);
        this.mManager = manager;
        this.mChannel = channel;
        this.mActivity = activity;
        ctx = ctxp;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            // Check to see if Wi-Fi is enabled and notify appropriate activity
            mActivity.tv1.setText(action);
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            // Call WifiP2pManager.requestPeers() to get a list of current peers
            mActivity.tv2.setText(action);
            if (mManager != null) {
                mManager.requestPeers(mChannel, mPeerHandler);
            }
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            if(isconnected) {
                mManager.requestConnectionInfo(mChannel, new WifiP2pManager.ConnectionInfoListener() {
                    @Override
                    public void onConnectionInfoAvailable(WifiP2pInfo info) {
                        InetAddress inetAddress = info.groupOwnerAddress;
                        clientService = new ClientService(ctx, inetAddress.getHostAddress());
                        mActivity.tv3.setText("Connection estabalished");
                    }
                });
            }
            mActivity.tv2.setText(action);
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            // Respond to this device's wifi state changing
            mActivity.tv3.setText(action);
        }
    }

    public void peerDiscovery(){
        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                mActivity.tv3.setText("Peer discovery succesfull");

            }

            @Override
            public void onFailure(int reasonCode) {
                mActivity.tv3.setText("Peer discovery failed");
            }
        });
    }

    public void sendData(String data){
        clientService.sendData(data);
    }
    public void sendData(Uri videouri){
        clientService.sendData(videouri);
    }
    public void connect(WifiP2pDevice device) {
        // Picking the first device found on the network.
        final WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = device.deviceAddress;
        config.wps.setup = WpsInfo.PBC;
        final Context ctx = this.ctx;
        mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                mActivity.tv3.setText("Connected, wait for connectioninfoavailable");
                isconnected = true;
            }

            @Override
            public void onFailure(int reason) {
                mActivity.tv3.setText("Connection failed");
            }
        });
    }

    public void setClientService(ClientService csp){
        clientService = csp;
    }
}
