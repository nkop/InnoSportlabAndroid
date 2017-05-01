package nl.in12soa.sperovideo;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.VideoView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import nl.in12soa.sperovideo.Controllers.CameraController;
import nl.in12soa.sperovideo.Controllers.PeerListAdapter;
import nl.in12soa.sperovideo.Services.ClientService;

public class CameraActivity extends AppCompatActivity implements WifiP2pManager.ConnectionInfoListener, SurfaceHolder.Callback{
    public TextView tv1;
    public TextView tv2;
    public TextView tv3;
    public ListView lv1;
    public boolean isRecording = false;
    private Button btn_refresh;
    private Button btn_senddata;
    private EditText et_senddata;
    public MediaRecorder mMediarecorder;
    public VideoView vw1;
    public Camera camera;
    private Button btn_startrec;
    SurfaceHolder mHolder;
    IntentFilter mIntentFilter;
    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;
    CameraController mReceiver;
    PeerListAdapter pla;
    String newvideopath;
    static final int REQUEST_VIDEO_CAPTURE = 1;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        tv1 = (TextView)findViewById(R.id.tv5);
        tv2 = (TextView)findViewById(R.id.tv6);
        tv3 = (TextView)findViewById(R.id.tv7);
        lv1 = (ListView)findViewById(R.id.lv1);
        btn_refresh = (Button)findViewById(R.id.btn_refresh);
        btn_senddata = (Button)findViewById(R.id.btn_activity_camera_senddata);
        et_senddata = (EditText)findViewById(R.id.et_activity_camera_senddata);
        vw1 = (VideoView) findViewById(R.id.vw_activity_camera_vw1);
        btn_startrec = (Button)findViewById(R.id.btn_activity_camera_startrec);
        pla = new PeerListAdapter();
        pla.setPeerAdapter(new ArrayAdapter<String>(this, R.layout.simple_list_item_1, pla.getList()));
        lv1.setAdapter(pla.peerAdapter);
        setReceiver();
        setListeners();
        mReceiver.clientService = new ClientService(this);
//        mHolder = vw1.getHolder();
////        camera = Camera.open();
////        camera.lock();
        setCamera();
    }

    private void setCamera() {
        btn_startrec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                dispatchTakeVideoIntent();
                if (isRecording) {
                    // stop recording and release camera
                    mMediarecorder.stop();  // stop the recording
                    mMediarecorder.release(); // release the MediaRecorder object
                    camera.lock();         // take camera access back from MediaRecorder

                    // inform the user that recording has stopped
                    btn_startrec.setText("Record");
                    Uri videouri = Uri.fromFile(new File(newvideopath));
                    mReceiver.sendData(videouri);
                    isRecording = false;
                } else {
                    // initialize video camera
                    newvideopath = prepareVideoRecorder();
                    if (newvideopath != null) {
                        // Camera is available and unlocked, MediaRecorder is prepared,
                        // now you can start recording
                        mMediarecorder.start();

                        // inform the user that recording has started
                        btn_startrec.setText("Stop");
                        isRecording = true;
                    } else {
                        // prepare didn't work, release the camera
                        mMediarecorder.release();
                        // inform user
                    }
                }
            }
        });
    }

    private void setListeners() {
        btn_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mReceiver.peerDiscovery();
            }
        });
        lv1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                WifiP2pDevice wp2 = mReceiver.mPeerHandler.peersList.get(position);
                System.out.print(wp2.deviceName);
                System.out.println("PRESSED");
                mReceiver.connect(wp2);
            }
        });

        btn_senddata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = et_senddata.getText().toString();
                mReceiver.sendData(data);
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
        mReceiver = new CameraController(mManager, mChannel, this, pla, getApplicationContext());
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

    private void dispatchTakeVideoIntent(){
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if(takeVideoIntent.resolveActivity(getPackageManager()) != null){
            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK){
            Uri videouri = data.getData();
            vw1.setVideoURI(videouri);
            System.out.println(vw1.isPlaying());
            vw1.start();
            mReceiver.sendData(videouri);
        }
    }
    /** Check if this device has a camera */
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            camera.setPreviewDisplay(mHolder);
            camera.startPreview();
        } catch (IOException e) {
            System.out.println();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    private String prepareVideoRecorder(){

        camera = getCameraInstance();
        mMediarecorder = new MediaRecorder();

        // Step 1: Unlock and set camera to MediaRecorder
        camera.unlock();
        mMediarecorder.setCamera(camera);

        // Step 2: Set sources
        mMediarecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mMediarecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        // Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
        mMediarecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));

        // Step 4: Set output file
        String filepath = getOutputMediaFile(MEDIA_TYPE_VIDEO).getAbsolutePath();
        mMediarecorder.setOutputFile(filepath);

        // Step 5: Set the preview output
        mMediarecorder.setPreviewDisplay(vw1.getHolder().getSurface());

        // Step 6: Prepare configured MediaRecorder
        try {
            mMediarecorder.prepare();
        } catch (IllegalStateException e) {
            tv3.setText("IllegalStateException preparing MediaRecorder: " + e.getMessage());
            mMediarecorder.release();
            return null;
        } catch (IOException e) {
            tv3.setText("IOException preparing MediaRecorder: " + e.getMessage());
            mMediarecorder.release();
            return null;
        }
        return filepath;
    }

    private static File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                System.out.println("failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }
}
