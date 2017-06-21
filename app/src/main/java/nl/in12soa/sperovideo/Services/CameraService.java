package nl.in12soa.sperovideo.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.provider.Settings;

import java.util.HashMap;

import nl.in12soa.sperovideo.CameraActivity;
import nl.in12soa.sperovideo.R;

import static nl.in12soa.sperovideo.Services.ServerService.PORT;


public class CameraService extends BroadcastReceiver {

    public WifiP2pManager wifiP2pManager;
    public WifiP2pManager.Channel channel;
    private CameraActivity cameraActivity;
    public ServerService serverService;
    public WifiP2pDnsSdServiceInfo serviceInfo;
    public static boolean GROUPEXISTS = false;
    public CameraService(WifiP2pManager manager, WifiP2pManager.Channel channel,
                         CameraActivity activity) {
        super();
        this.wifiP2pManager = manager;
        this.channel = channel;
        this.cameraActivity = activity;
    }

    public void cleangroup(){
        wifiP2pManager.removeGroup(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                //TODO feedback

            }

            @Override
            public void onFailure(int reason) {
                    //TODO feedback
            }
        });

        wifiP2pManager.clearLocalServices(channel, new WifiP2pManager.ActionListener() {
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
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                if(!GROUPEXISTS) {
                    wifiP2pManager.createGroup(channel, new WifiP2pManager.ActionListener() {
                        @Override
                        public void onSuccess() {
                            cameraActivity.setFeedback(cameraActivity.getString(R.string.camera_service_started));
                            startRegistration();
                            startServer();
                        }

                        @Override
                        public void onFailure(int reason) {
                            cameraActivity.setFeedback(cameraActivity.getString(R.string.camera_service_failed));
                        }
                    });
                }else{
                    startServer();
                }
            } else {
                cameraActivity.setFeedback(cameraActivity.getString(R.string.wifi_direct_n_a));
            }
            System.out.println(action);
        } else if(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            //TODO Feedback
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
        }
    }

    private void startRegistration() {
        //  Create a string map containing information about your service.
        HashMap record = new HashMap();
        PORT = (int) (Math.random() * 60535) + 5000;

        record.put("listenport", String.valueOf(5002));
        record.put("service_id", "SperoCam_" + PORT);
        record.put("service_type", "Camera_" + PORT);
        record.put("available", "visible");

        // Service information.  Pass it an instance name, service type
        // _protocol._transportlayer , and the map containing
        // information other devices will want once they connect to this one.
        serviceInfo =
                WifiP2pDnsSdServiceInfo.newInstance("_speroservice", "_presence._tcp", record);

        // Add the local service, sending the service info, network channel,
        // and listener that will be used to indicate success or failure of
        // the request.
        wifiP2pManager.addLocalService(channel, serviceInfo, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                GROUPEXISTS = true;
                cameraActivity.setFeedback(cameraActivity.getString(R.string.scan_chip_or_manual));
            }

            @Override
            public void onFailure(int code) {
                String feedback;
                switch(code){
                    case WifiP2pManager.P2P_UNSUPPORTED:
                        feedback = cameraActivity.getString(R.string.wifi_direct_support_fail);
                        break;
                    case WifiP2pManager.BUSY:
                        feedback = cameraActivity.getString(R.string.wifi_direct_busy);
                        break;
                    case WifiP2pManager.ERROR:
                        feedback = cameraActivity.getString(R.string.error_searching_cameras);
                        break;
                    default:
                        feedback = cameraActivity.getString(R.string.unknown_error);
                        break;
                }
                cameraActivity.setFeedback(feedback);
            }
        });
    }
    private void startServer(){
        serverService = new ServerService(cameraActivity);
        serverService.execute();
    }
}
