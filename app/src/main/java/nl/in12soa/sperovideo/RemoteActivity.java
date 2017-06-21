package nl.in12soa.sperovideo;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;

import nl.in12soa.sperovideo.Models.Peer;
import nl.in12soa.sperovideo.Services.ActionBarService;
import nl.in12soa.sperovideo.Services.AnalyseService;
import nl.in12soa.sperovideo.Services.ClientService;

public class RemoteActivity extends AppCompatActivity implements WifiP2pManager.ConnectionInfoListener {

    private NfcAdapter nfcAdapter;

    private PeerListAdapter peerListAdapter;
    IntentFilter intentFilter;
    AnalyseService broadcastReceiver;
    RecyclerView recyclerView;
    ClientService clientService;
    Boolean cameraSelected;
    public Button btn_startcamera;

    public static final String PREFS = "CameraSettings";
    public SharedPreferences preferences;
    public SurfaceHolder surfaceHolder;
    Handler handler;
    Runnable hideRunnable;
    public boolean refreshPressed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyse);
        peerListAdapter = new PeerListAdapter(new ArrayList<Peer>(), this);
        preferences = getSharedPreferences(PREFS, 0);
        btn_startcamera = (Button)findViewById(R.id.start_camera_button);
        btn_startcamera.setEnabled(false);
        setReceiver();
        setListeners();
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        ActionBarService.setActionBarTitle(R.string.remote, getSupportActionBar());
        cameraSelected = false;
        surfaceHolder = ((SurfaceView) findViewById(R.id.surface_view)).getHolder();
        recyclerView = (RecyclerView) findViewById(R.id.rv_peerlist);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(peerListAdapter);
        clientService = new ClientService(this);


    }

    private void setReceiver() {
        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        WifiP2pManager wifiP2pManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        WifiP2pManager.Channel channel = wifiP2pManager.initialize(this, getMainLooper(), null);
        broadcastReceiver = new AnalyseService(this, channel, wifiP2pManager, peerListAdapter);
    }

    private void setListeners() {
        (findViewById(R.id.refresh_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFeedback(getString(R.string.searching_camera), true, 5000, true);
                refreshPressed = true;
                broadcastReceiver.peerDiscovery();
            }
        });
        btn_startcamera.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (cameraSelected) {
                    setFeedback(getString(R.string.video_recording), true, 0, false);
                    clientService.sendData("{ \"command\" : \"start_camera\", \"parameters\" : { \"framerate\" : " + preferences.getString("fps", "24") + ", \"resolution_quality\" : " + preferences.getString("resolution_quality", "1") + ", \"duration\" : 10000, \"device_name\" : \"" + Build.MODEL + "\" } }");
                }
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
            (findViewById(R.id.start_camera_button)).setEnabled(true);
        }
    }

    public void setFeedback(String text, boolean shl, int dur, final boolean end) {
        final boolean showloading = shl;
        final String value = text;
        final int duration = dur;
        final TextView feedbacktv = (TextView) findViewById(R.id.tv_feedback);
        final View feedbackpb = findViewById(R.id.pb_feedback);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                feedbacktv.setText(value);
                if (showloading) {
                    feedbackpb.setVisibility(View.VISIBLE);
                }
                feedbacktv.setVisibility(View.VISIBLE);
                if (hideRunnable != null) {
                    handler.removeCallbacks(hideRunnable);
                }
                hideRunnable = new Runnable() {
                    @Override
                    public void run() {
                        hideFeedback(feedbackpb, feedbacktv);
                    }
                };
                handler = new Handler();
                if (end) {
                    handler.postDelayed(hideRunnable, duration);
                }
            }
        });

    }

    public void hideFeedback(View feedbackpb, View feedbacktv) {
        feedbackpb.setVisibility(View.INVISIBLE);
        feedbacktv.setVisibility(View.INVISIBLE);
    }

    public void disconnect() {
        broadcastReceiver.disconnect();
        cameraSelected = false;
    }

    public void playVideo(Uri videoPath) {
        try {
            final MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(getApplicationContext(), videoPath);
            mediaPlayer.prepare();
            mediaPlayer.setDisplay(surfaceHolder);
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    peerListAdapter.empty();
//                }
//            });
//            disconnect();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mediaPlayer.reset();
                }
            });
            setFeedback(getString(R.string.playing_video), true, mediaPlayer.getDuration(), true);
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, intentFilter);
        enableForegroundDispatchSystem();
    }

    /* unregister the broadcast receiver */
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
        disableForegroundDispatchSystem();
    }

    private void enableForegroundDispatchSystem() {

        if (nfcAdapter != null && nfcAdapter.isEnabled()) {

            Intent intent = new Intent(this, RemoteActivity.class).addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);

            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

            IntentFilter[] intentFilters = new IntentFilter[]{};

            nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilters, null);
        }
    }

    private void disableForegroundDispatchSystem() {
        if (nfcAdapter != null && nfcAdapter.isEnabled()) {
            nfcAdapter.disableForegroundDispatch(this);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (nfcAdapter != null && nfcAdapter.isEnabled()) {
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
                    setFeedback(getString(R.string.video_recording), true, 0, false);
                    clientService.sendData("{ \"command\" : \"start_camera\", \"parameters\" : { \"framerate\" : " + preferences.getString("fps", "24") + ", \"resolution_quality\" : " + preferences.getString("resolution_quality", "1") + ", \"duration\" : 10000, \"device_name\" : \"" + Build.MODEL + "\" } }");
                }
            }

        } else {
            setFeedback("This device does not support NFC", true, 0, false);
        }
    }
}

