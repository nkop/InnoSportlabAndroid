package nl.in12soa.sperovideo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;

import nl.in12soa.sperovideo.Controllers.AnalyseController;
import nl.in12soa.sperovideo.Services.PaintService;

public class AnalyseActivity extends AppCompatActivity implements WifiP2pManager.ConnectionInfoListener{
    IntentFilter mIntentFilter;
    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;
    BroadcastReceiver mReceiver;
    PaintService ps;
    public TextView tvlogger;
    public SurfaceView vw1;
    public SurfaceHolder vw1_holder;
    public SurfaceView vw2;
    private MediaPlayer mp;
    private Button btn_drawtriangle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyse);
        tvlogger = (TextView)findViewById(R.id.tv4);
        vw1 = (SurfaceView)findViewById(R.id.sv_activity_analyse_sv1);
        vw1_holder = vw1.getHolder();
        vw2 = (SurfaceView)findViewById(R.id.sv_activity_analyse_sv2);


        vw2.setZOrderOnTop(true);    // necessary
        SurfaceHolder sfhTrackHolder = vw2.getHolder();
        sfhTrackHolder.setFormat(PixelFormat.TRANSPARENT);

        mp = new MediaPlayer();
        ps = new PaintService(this);
        btn_drawtriangle = (Button)findViewById(R.id.btn_draw_rect);
        setReceiver();
        setListeners();
    }

    private void setListeners(){
        btn_drawtriangle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ps.drawTriangle();
            }
        });
    }

    private void setReceiver(){
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        mReceiver = new AnalyseController(mManager, mChannel, this, tvlogger);
    }

    public void playVideo(String videoPath){
        try {
            mp.setDataSource(videoPath);
            mp.prepare();
            mp.setDisplay(vw1_holder);

        } catch (IOException e) {
            e.printStackTrace();
        }
        mp.start();
    }

    /* register the broadcast receiver with the intent values to be matched */
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

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo info) {

    }
}
