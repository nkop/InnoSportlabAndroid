package nl.in12soa.sperovideo.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;

import java.io.File;
import java.util.HashMap;

import nl.in12soa.sperovideo.CameraActivity;


public class CameraService extends BroadcastReceiver {

    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private CameraActivity mActivity;
    public ServerService dataService;
    public CameraService(WifiP2pManager manager, WifiP2pManager.Channel channel,
                         CameraActivity activity) {
        super();
        this.mManager = manager;
        this.mChannel = channel;
        this.mActivity = activity;
    }

    //Never used, Ahmad?!😡😡😡😡
    private void cleangroup(){
        mManager.removeGroup(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                System.out.println("Group Removed");

            }

            @Override
            public void onFailure(int reason) {
                System.out.println("Group remove failed");
            }
        });
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                mManager.createGroup(mChannel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        System.out.println("Group Creation succesfull");
                        startRegistration();
                        dataService = new ServerService(mActivity);
                        dataService.execute();
                    }

                    @Override
                    public void onFailure(int reason) {
                        System.out.println("Group Creation failed");
                    }
                });
            } else {
                // Wi-Fi P2P is not enabled
            }
            System.out.println(action);
        } else if(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            // Call WifiP2pManager.requestPeers() to get a list of current peers NOT FOR ANALYSE!!!
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {

        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            // Respond to this device's wifi state changing
        }
    }

    private void startRegistration() {
        //  Create a string map containing information about your service.
        HashMap record = new HashMap();
        record.put("listenport", String.valueOf(5002));
        record.put("service_id", "SperoCam_" + (int) (Math.random() * 1000));
        record.put("service_type", "Camera" + (int) (Math.random() * 1000));
        record.put("available", "visible");

        // Service information.  Pass it an instance name, service type
        // _protocol._transportlayer , and the map containing
        // information other devices will want once they connect to this one.
        WifiP2pDnsSdServiceInfo serviceInfo =
                WifiP2pDnsSdServiceInfo.newInstance("_speroservice", "_presence._tcp", record);

        // Add the local service, sending the service info, network channel,
        // and listener that will be used to indicate success or failure of
        // the request.
        mManager.addLocalService(mChannel, serviceInfo, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                System.out.println("Service added");
            }

            @Override
            public void onFailure(int arg0) {
                // Command failed.  Check for P2P_UNSUPPORTED, ERROR, or BUSY
            }
        });
    }


}
