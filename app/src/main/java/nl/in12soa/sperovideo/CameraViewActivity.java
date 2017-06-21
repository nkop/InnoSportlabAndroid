package nl.in12soa.sperovideo;

import java.util.*;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.widget.TextView;
import android.widget.VideoView;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import nl.in12soa.sperovideo.Services.ServerService;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

public class CameraViewActivity extends AppCompatActivity implements SurfaceHolder.Callback, MediaRecorder.OnInfoListener {

    private boolean isRecording = false;
    public MediaRecorder mediaRecorder;
    public VideoView videoView;
    public Camera camera;
    public static String newVideoPath;
    private HashMap<String, Integer> videoSettings;

    //timer settings
    private long startHTime = 0L;
    private Handler customHandler = new Handler();
    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_view);
        videoView = (VideoView) findViewById(R.id.camera_preview);
        SurfaceHolder surfaceHolder = videoView.getHolder();
        surfaceHolder.addCallback(this);
        videoSettings = new HashMap<>();
        Bundle extras = getIntent().getExtras();
        videoSettings.put("resolution_quality", extras.getInt("resolution_quality"));
        videoSettings.put("framerate", extras.getInt("framerate"));
        videoSettings.put("duration", extras.getInt("duration"));
    }


    private void setCamera() {
        if(checkCameraHardware()) {
            if (isRecording) {
                mediaRecorder.stop();
                mediaRecorder.release();
                camera.lock();
                Uri.fromFile(new File(newVideoPath));
                isRecording = false;
            } else {
                // initialize video camera
                if (newVideoPath != null) {
                    mediaRecorder.start();


                    isRecording = true;
                } else {
                    mediaRecorder.release();
                }
            }
        }else{
            System.out.println("no camera");
        }
    }

    private String prepareVideoRecorder() {
        camera = getCameraInstance();
        camera.setDisplayOrientation(90);
        mediaRecorder = new MediaRecorder();
        Camera.Size size = translateVideoSize(videoSettings.get("resolution_quality"));
        camera.unlock();
        mediaRecorder.setCamera(camera);
        mediaRecorder.setMaxDuration(videoSettings.get("duration"));
        mediaRecorder.setOnInfoListener(this);

        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setVideoSize(size.width, size.height);
//        mediaRecorder.setVideoFrameRate(20);
//        mediaRecorder.setVideoEncodingBitRate(3000000);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);

//        //settings
//        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
//        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
//        mediaRecorder.setVideoFrameRate(videoSettings.get("framerate"));
////        mMediarecorder.setVideoSize(videoSettings.get("resolution_y"),videoSettings.get("resolution_x"));
//        mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));

        // Step 4: Set output file
        String filepath = getOutputMediaFile().getAbsolutePath();
        mediaRecorder.setOutputFile(filepath);

        // Step 5: Set the preview output
        mediaRecorder.setPreviewDisplay(videoView.getHolder().getSurface());

        // Step 6: Prepare configured MediaRecorder
        try {
            mediaRecorder.prepare();
        } catch (IllegalStateException e) {
            System.out.println("IllegalStateException preparing MediaRecorder: " + e.getMessage());
            mediaRecorder.release();
            return null;
        } catch (IOException e) {
            System.out.println("IOException preparing MediaRecorder: " + e.getMessage());
            mediaRecorder.release();
            return null;
        }
        return filepath;
    }

    private File getOutputMediaFile() {

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Spero");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                System.out.println("failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                "VID_" + timeStamp + ".mp4");
        return mediaFile;
    }

    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception e) {
            System.out.println("No camera available");
        }
        return c;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            newVideoPath = prepareVideoRecorder();
            setCamera();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    private boolean checkCameraHardware() {
        return this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    @Override
    public void onInfo(MediaRecorder mr, int what, int extra) {
        if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
            mediaRecorder.stop();
            mediaRecorder.release();
            camera.stopPreview();
            camera.release();
            camera = null;
            ServerService.VIDEOURI = Uri.fromFile(new File(newVideoPath));
            setResult(5);
            finish();
        }
    }


    private Camera.Size translateVideoSize(int size){
        List<Camera.Size> videoSizes = camera.getParameters().getSupportedVideoSizes();
        switch(size){
            case 0:
                return videoSizes.get(0);
            case 1:
                return videoSizes.get(videoSizes.size()/2);
            case 2:
                return videoSizes.get(videoSizes.size()-1);
            default:
                return videoSizes.get(0);
        }

    }

}
