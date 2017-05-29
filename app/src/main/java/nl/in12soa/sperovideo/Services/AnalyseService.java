package nl.in12soa.sperovideo.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;

import java.util.HashMap;
import java.util.Map;

import nl.in12soa.sperovideo.R;
import nl.in12soa.sperovideo.RemoteActivity;
import nl.in12soa.sperovideo.Models.Peer;
import nl.in12soa.sperovideo.Models.PeerListHandler;
import nl.in12soa.sperovideo.PeerListAdapter;


public class AnalyseService extends BroadcastReceiver {
    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    public PeerListHandler mPeerHandler;
    public PeerListAdapter pla;
    private boolean isconnected = false;
    private WifiP2pDnsSdServiceRequest serviceRequest;
    RemoteActivity mActivity;
    final HashMap<String, String> cameraServices = new HashMap<String, String>();
    public AnalyseService(RemoteActivity act, WifiP2pManager.Channel channelp, WifiP2pManager mgrp, PeerListAdapter plap){
        super();
        mActivity = act;
        mManager = mgrp;
        mChannel = channelp;
        pla = plap;
        mPeerHandler = new PeerListHandler();
    }

    public void peerDiscovery(){
        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                mActivity.setFeedback(mActivity.getString(R.string.peer_discovery_succesvol), false, 5000);
            }

            @Override
            public void onFailure(int reasonCode) {
                mActivity.setFeedback(mActivity.getString(R.string.peer_discovery_failed), false, 15000);
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
                isconnected = true;
                mActivity.setFeedback(mActivity.getString(R.string.connection_camera_success), false, 5000);
//                System.out.println("Connected to device");
            }

            @Override
            public void onFailure(int reason) {
                mActivity.setFeedback(mActivity.getString(R.string.connection_camera_failed), false, 5000);
            }
        });
    }

    public void disconnect(){
        mManager.removeServiceRequest(mChannel, serviceRequest, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int reason) {

            }
        });
        mManager.removeGroup(mChannel, new WifiP2pManager.ActionListener() {
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

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            // Check to see if Wi-Fi is enabled and notify appropriate activity
//            System.out.println(action);
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            // Call WifiP2pManager.requestPeers() to get a list of current peers
//            System.out.println(action);
            if (mManager != null) {
                mManager.requestPeers(mChannel, mPeerHandler);
                discoverService();
                discoverServices();
            }
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            if(isconnected) {
                mManager.requestConnectionInfo(mChannel, mActivity);
            }
//            System.out.println(action);
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            // Respond to this device's wifi state changing
//            System.out.println(action);
        }
    }


    private void discoverService() {
        WifiP2pManager.DnsSdTxtRecordListener txtListener = new WifiP2pManager.DnsSdTxtRecordListener() {
            @Override
            public void onDnsSdTxtRecordAvailable(
                    String fullDomain, Map record, WifiP2pDevice device) {
//                System.out.println("DnsSdTxtRecord available -" + record.toString());
                cameraServices.put(device.deviceAddress + "_id", record.get("service_id").toString());
                cameraServices.put(device.deviceAddress + "_type", record.get("service_type").toString());
            }
        };
        WifiP2pManager.DnsSdServiceResponseListener servListener = new WifiP2pManager.DnsSdServiceResponseListener() {
            @Override
            public void onDnsSdServiceAvailable(String instanceName, String registrationType,
                                                WifiP2pDevice resourceType) {

                // Update the device name with the human-friendly version from
                // the DnsTxtRecord, assuming one arrived.
                resourceType.deviceName = cameraServices
                        .containsKey(resourceType.deviceAddress + "_id") ? cameraServices
                        .get(resourceType.deviceAddress + "_id") : resourceType.deviceName;
                String type = cameraServices
                        .containsKey(resourceType.deviceAddress + "_type") ? cameraServices
                        .get(resourceType.deviceAddress + "_type") : resourceType.deviceName;
//                System.out.println(resourceType.deviceName);
                final Peer peer = new Peer(resourceType, type);
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pla.addItem(peer);
                    }
                });
            }
        };

        mManager.setDnsSdResponseListeners(mChannel, servListener, txtListener);
        serviceRequest = WifiP2pDnsSdServiceRequest.newInstance();
        mManager.addServiceRequest(mChannel,
                serviceRequest,
                new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        //service request added
//                        System.out.println("Service request added");
                    }

                    @Override
                    public void onFailure(int code) {
                        mActivity.setFeedback(mActivity.getString(R.string.error) + code, false, 15000);
                    }
                });
    }

    public void discoverServices(){
        mManager.discoverServices(mChannel, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                //service discovery succesfull
//                System.out.println("Service discovery succesfull");
            }

            @Override
            public void onFailure(int code) {
                // Command failed.  Check for P2P_UNSUPPORTED, ERROR, or BUSY
                if (code == WifiP2pManager.P2P_UNSUPPORTED) {
                    mActivity.setFeedback("wifi_direct_support_fail", false, 5000);
                }
            }
        });
    }
}
