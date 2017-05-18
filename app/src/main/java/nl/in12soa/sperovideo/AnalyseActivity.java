package nl.in12soa.sperovideo;

import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import java.net.InetAddress;
import java.util.ArrayList;
import nl.in12soa.sperovideo.Models.Peer;
import nl.in12soa.sperovideo.Services.AnalyseService;
import nl.in12soa.sperovideo.Services.ClientService;

public class AnalyseActivity extends AppCompatActivity implements WifiP2pManager.ConnectionInfoListener {
    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;
    PeerListAdapter pla;
    IntentFilter mIntentFilter;
    AnalyseService mReceiver;
    LinearLayoutManager mLinearLayoutManager;
    RecyclerView rv_peerlist;
    ClientService clientService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyse);
        pla = new PeerListAdapter(new ArrayList<Peer>(), this);
        setReceiver();
        setListeners();
        rv_peerlist = (RecyclerView)findViewById(R.id.rv_peerlist);
        mLinearLayoutManager = new LinearLayoutManager(this);
        rv_peerlist.setLayoutManager(mLinearLayoutManager);
        rv_peerlist.setAdapter(pla);
        clientService = new ClientService(this);
    }

    private void setReceiver(){
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        mReceiver = new AnalyseService(this, mChannel, mManager, pla);
    }

    private void setListeners() {
        (findViewById(R.id.btn_refresh)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mReceiver.peerDiscovery();
            }
        });
    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo info) {
        InetAddress inetAddress = info.groupOwnerAddress;
        ClientService.setHost(inetAddress.getHostAddress());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        clientService.sendData("command_start_camera");
    }


    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver, mIntentFilter);
    }
    /* unregister the broadcast receiver */
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }
}
