package nl.in12soa.sperovideo;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.net.Inet4Address;
import java.net.InetAddress;

import nl.in12soa.sperovideo.Controllers.PeerListAdapter;
import nl.in12soa.sperovideo.Controllers.ServerListController;
import nl.in12soa.sperovideo.Services.ClientService;


public class ServerListActivity extends AppCompatActivity implements WifiP2pManager.ConnectionInfoListener{

    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;
    PeerListAdapter pla;
    IntentFilter mIntentFilter;
    ListView lvpeerlist;
    TextView tvlog;
    Button btn_refresh;
    ServerListController mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_list_activty);
        btn_refresh = (Button)findViewById(R.id.btn_serverlist_activity_refresh);
        tvlog = (TextView)findViewById(R.id.tv_serverlistactivity_log);
        lvpeerlist = (ListView)findViewById(R.id.lv_serverlistactivity_lv1);
        pla = new PeerListAdapter();
        pla.setPeerAdapter(new ArrayAdapter<String>(this, R.layout.simple_list_item_1, pla.getList()));
        lvpeerlist.setAdapter(pla.peerAdapter);
        setReceiver();
        setListeners();
    }

    private void setReceiver(){
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        mReceiver = new ServerListController(tvlog, this, mChannel, mManager, pla);
    }

    private void setListeners() {
        lvpeerlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                WifiP2pDevice wp2 = mReceiver.mPeerHandler.peersList.get(position);
                System.out.print(wp2.deviceName);
                System.out.println("PRESSED");
                mReceiver.connect(wp2);
            }
        });
        btn_refresh.setOnClickListener(new View.OnClickListener() {
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
        Intent camera = new Intent(this, CameraActivity.class);
        tvlog.setText("Connection Succesfull!!!");
        startActivity(camera);
    }

    public void goToCamera(Inet4Address address){
        final Intent intent = new Intent(this, CameraActivity.class);
        startActivity(intent);
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
