package nl.in12soa.sperovideo.Controllers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pManager;
import android.widget.TextView;


import nl.in12soa.sperovideo.AnalyseActivity;
import nl.in12soa.sperovideo.Services.DataServiceAsyncTask;

/**
 * Created by Ahmad on 3/1/2017.
 */

public class AnalyseController extends BroadcastReceiver{
    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private AnalyseActivity mActivity;
    public DataServiceAsyncTask dataServiceAsyncTask;
    TextView logger;

    public AnalyseController(WifiP2pManager manager, WifiP2pManager.Channel channel,
                                       AnalyseActivity activity, TextView loggerp) {
        super();
        this.mManager = manager;
        this.mChannel = channel;
        this.mActivity = activity;
        logger = loggerp;
//
    }

    private void cleangroup(){
        mManager.removeGroup(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                logger.setText("Group Removed");

            }

            @Override
            public void onFailure(int reason) {
                logger.setText("Group remove failed");
            }
        });
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            cleangroup();
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                mManager.createGroup(mChannel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        logger.setText("Group Creation succesfull");
                        dataServiceAsyncTask = new DataServiceAsyncTask(mActivity);
                        dataServiceAsyncTask.execute();
                    }

                    @Override
                    public void onFailure(int reason) {
                      logger.setText("Group Creation failed");
                    }
                });
            } else {
                // Wi-Fi P2P is not enabled
            }
            mActivity.tvlogger.setText(action);
        } else if(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            // Call WifiP2pManager.requestPeers() to get a list of current peers NOT FOR ANALYSE!!!
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {

        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            // Respond to this device's wifi state changing
        }
    }


}
