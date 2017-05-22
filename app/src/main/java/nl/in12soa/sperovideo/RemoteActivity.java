package nl.in12soa.sperovideo;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;

import nl.in12soa.sperovideo.Models.Peer;
import nl.in12soa.sperovideo.Services.AnalyseService;
import nl.in12soa.sperovideo.Services.ClientService;

public class RemoteActivity extends AppCompatActivity implements WifiP2pManager.ConnectionInfoListener {

    private NfcAdapter nfcAdapter;

    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;
    PeerListAdapter pla;
    IntentFilter mIntentFilter;
    AnalyseService mReceiver;
    LinearLayoutManager mLinearLayoutManager;
    RecyclerView rv_peerlist;
    ClientService clientService;
    Boolean cameraSelected;

    public SurfaceView vw1;
    public SurfaceHolder vw1_holder;
    private MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyse);
        pla = new PeerListAdapter(new ArrayList<Peer>(), this);
        setReceiver();
        setListeners();
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        cameraSelected = false;

        vw1 = (SurfaceView) findViewById(R.id.surfaceView);
        vw1_holder = vw1.getHolder();
        rv_peerlist = (RecyclerView) findViewById(R.id.rv_peerlist);
        mLinearLayoutManager = new LinearLayoutManager(this);
        rv_peerlist.setLayoutManager(mLinearLayoutManager);
        rv_peerlist.setAdapter(pla);
        clientService = new ClientService(this);


    }

    private void setReceiver() {
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
        if (inetAddress != null) {
            ClientService.setHost(inetAddress.getHostAddress());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            cameraSelected = true;
        }
    }

    public void playVideo(Uri videoPath) {
        try {
            mp = new MediaPlayer();
            mp.setDataSource(getApplicationContext(), videoPath);
            mp.prepare();
            mp.setDisplay(vw1_holder);

        } catch (IOException e) {
            e.printStackTrace();
        }
        mp.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver, mIntentFilter);
        enableForegroundDispatchSystem();
    }

    /* unregister the broadcast receiver */
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
        disableForegroundDispatchSystem();
    }

    private void enableForegroundDispatchSystem() {

        Intent intent = new Intent(this, RemoteActivity.class).addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        IntentFilter[] intentFilters = new IntentFilter[]{};

        nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilters, null);
    }

    private void disableForegroundDispatchSystem() {
        nfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (intent.hasExtra(NfcAdapter.EXTRA_ID)) {
            byte[] serial = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
            String serialstring = "";

            for (int i = 0; i < serial.length; i++) {
                String x = Integer.toHexString(((int) serial[i] & 0xff));
                if (x.length() == 1) {
                    x = '0' + x;
                }
                serialstring += x + ' ';
            }
            if (cameraSelected) {
                clientService.sendData("{ \"command\" : \"start_camera\", \"parameters\" : { \"framerate\" : 30, \"resolution_y\" : 640, \"resolution_x\" : 480, \"duration\" : 10000 } }");
            }
        }
    }
}

