package nl.in12soa.sperovideo;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import nl.in12soa.sperovideo.Services.ActionBarService;
import nl.in12soa.sperovideo.Services.CameraService;

public class CameraActivity extends AppCompatActivity implements WifiP2pManager.ConnectionInfoListener{

    //Waarom zijn deze static?
    public IntentFilter intentFilter;
    public WifiP2pManager p2pManager;
    public WifiP2pManager.Channel p2pManagerChannel;
    public CameraService cameraService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        ActionBarService.setActionBarTitle(R.string.camera, getSupportActionBar());
        setReceiver();
    }

    private void setReceiver(){
        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        p2pManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        p2pManagerChannel = p2pManager.initialize(this, getMainLooper(), null);
        cameraService = new CameraService(p2pManager, p2pManagerChannel, this);
    }

    /* register the broadcast receiver with the intent values to be matched */
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(cameraService, intentFilter);
    }
    /* unregister the broadcast receiver */
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(cameraService);
    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo info) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
    }
}
