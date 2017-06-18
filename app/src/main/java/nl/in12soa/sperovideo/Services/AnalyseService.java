package nl.in12soa.sperovideo.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;

import java.util.HashMap;
import java.util.Map;

import nl.in12soa.sperovideo.R;
import nl.in12soa.sperovideo.RemoteActivity;
import nl.in12soa.sperovideo.Models.Peer;
import nl.in12soa.sperovideo.PeerListAdapter;


public class AnalyseService extends BroadcastReceiver {
    private WifiP2pManager wifiP2pManager;
    private WifiP2pManager.Channel channel;
    public PeerListAdapter peerListAdapter;
    private boolean isconnected = false;
    private WifiP2pDnsSdServiceRequest serviceRequest;
    RemoteActivity remoteActivity;
    final HashMap<String, String> cameraServices = new HashMap<String, String>();

    public AnalyseService(RemoteActivity act, WifiP2pManager.Channel channelp, WifiP2pManager mgrp, PeerListAdapter plap){
        super();
        remoteActivity = act;
        wifiP2pManager = mgrp;
        channel = channelp;
        peerListAdapter = plap;
    }

    public void peerDiscovery(){
        wifiP2pManager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                remoteActivity.setFeedback(remoteActivity.getString(R.string.peer_discovery_succesvol), false, 5000, true);
            }

            @Override
            public void onFailure(int reasonCode) {
                remoteActivity.setFeedback(remoteActivity.getString(R.string.peer_discovery_failed), false, 15000, false);
            }
        });
    }

    public void connect(WifiP2pDevice device) {
        // Picking the first device found on the network.
        final WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = device.deviceAddress;
        config.wps.setup = WpsInfo.PBC;
        wifiP2pManager.connect(channel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                isconnected = true;
                remoteActivity.setFeedback(remoteActivity.getString(R.string.connection_camera_success), false, 5000, false);
            }

            @Override
            public void onFailure(int reason) {
                remoteActivity.setFeedback(remoteActivity.getString(R.string.connection_camera_failed), false, 5000, false);
            }
        });
    }

    public void disconnect(){
        wifiP2pManager.removeServiceRequest(channel, serviceRequest, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int reason) {

            }
        });
        wifiP2pManager.removeGroup(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int reason) {

            }
        });
    }

    @Override
    public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            System.out.println("onreceive_1");
            switch (action) {
                case WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION:
                    if (wifiP2pManager != null && remoteActivity.refreshPressed) {
                        wifiP2pManager.requestPeers(channel, new WifiP2pManager.PeerListListener() {
                            @Override
                            public void onPeersAvailable(WifiP2pDeviceList wifiP2pDeviceList) {
                            }
                        });
                        discoverService();
                        discoverServices();
                        remoteActivity.refreshPressed = false;
                    }
                    break;
                case WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION:
                    if (isconnected) {
                        wifiP2pManager.requestConnectionInfo(channel, remoteActivity);
                    }
                    break;
            }
    }


    private void discoverService() {
        WifiP2pManager.DnsSdTxtRecordListener txtListener = new WifiP2pManager.DnsSdTxtRecordListener() {
            @Override
            public void onDnsSdTxtRecordAvailable(
                    String fullDomain, Map record, WifiP2pDevice device) {
                if(record.containsKey("service_id")){
                    cameraServices.put(device.deviceAddress + "_id", record.get("service_id").toString());
                    cameraServices.put(device.deviceAddress + "_type", record.get("service_type").toString());
                }
            }
        };
        WifiP2pManager.DnsSdServiceResponseListener servListener = new WifiP2pManager.DnsSdServiceResponseListener() {
            @Override
            public void onDnsSdServiceAvailable(String instanceName, String registrationType,
                                                WifiP2pDevice resourceType) {
                resourceType.deviceName = cameraServices
                        .containsKey(resourceType.deviceAddress + "_id") ? cameraServices
                        .get(resourceType.deviceAddress + "_id") : resourceType.deviceName;
                String type = cameraServices
                        .containsKey(resourceType.deviceAddress + "_type") ? cameraServices
                        .get(resourceType.deviceAddress + "_type") : resourceType.deviceName;
                final Peer peer = new Peer(resourceType, type);
                remoteActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        peerListAdapter.addItem(peer);
                    }
                });
            }
        };

        wifiP2pManager.setDnsSdResponseListeners(channel, servListener, txtListener);
        serviceRequest = WifiP2pDnsSdServiceRequest.newInstance();
        wifiP2pManager.addServiceRequest(channel,
                serviceRequest,
                new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        remoteActivity.setFeedback(remoteActivity.getString(R.string.cameras_found),true,2000, true);
                    }

                    @Override
                    public void onFailure(int code) {
                        remoteActivity.setFeedback(remoteActivity.getString(R.string.error) + code, false, 15000, false);
                    }
                });
    }

    public void discoverServices(){
        wifiP2pManager.discoverServices(channel, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                remoteActivity.setFeedback(remoteActivity.getString(R.string.loading_cameras), false, 4000, true);
            }

            @Override
            public void onFailure(int code) {
                // Command failed.  Check for P2P_UNSUPPORTED, ERROR, or BUSY
                String feedback;
                switch(code){
                    case WifiP2pManager.P2P_UNSUPPORTED:
                        feedback = remoteActivity.getString(R.string.wifi_direct_support_fail);
                        break;
                    case WifiP2pManager.BUSY:
                        feedback = remoteActivity.getString(R.string.wifi_direct_busy);
                        break;
                    case WifiP2pManager.ERROR:
                        feedback = remoteActivity.getString(R.string.error_searching_cameras);
                        break;
                    default:
                        feedback = remoteActivity.getString(R.string.unknown_error);
                        break;
                }
                remoteActivity.setFeedback(feedback, false, 5000, false);
            }
        });
    }
}
