package nl.in12soa.sperovideo;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.widget.VideoView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import nl.in12soa.sperovideo.Services.ServerService;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

public class CameraViewActivity extends AppCompatActivity implements SurfaceHolder.Callback, MediaRecorder.OnInfoListener {

    private boolean isRecording = false;
    public MediaRecorder mediaRecorder;
    public VideoView videoView;
    //Camera deprecated, geen andere optie? Vanaf android 5.0 is nieuwe camera api beschikbaar. Maybe is dit prima
    public Camera camera;
    public static String newVideoPath;
    private SurfaceHolder surfaceHolder;
    private HashMap<String, Integer> videoSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_view);
        videoView = (VideoView) findViewById(R.id.camera_preview);
        surfaceHolder = videoView.getHolder();
        surfaceHolder.addCallback(this);
        videoSettings = new HashMap<>();
        Bundle extras = getIntent().getExtras();
        videoSettings.put("resolution_y", extras.getInt("resultion_y"));
        videoSettings.put("resolution_x", extras.getInt("resultion_x"));
        videoSettings.put("framerate", extras.getInt("framerate"));
        videoSettings.put("duration", extras.getInt("duration"));
    }


    private void setCamera() {
        if (isRecording) {
            // stop recording and release camera
            mediaRecorder.stop();  // stop the recording
            mediaRecorder.release(); // release the MediaRecorder object
            camera.lock();         // take camera access back from MediaRecorder
            // inform the user that recording has stopped
            Uri.fromFile(new File(newVideoPath));

            //TODO CAMERA TERUGSTUREN
//            mReceiver.sendData(videouri);
            isRecording = false;
        } else {
            // initialize video camera
            if (newVideoPath != null) {
                // Camera is available and unlocked, MediaRecorder is prepared,
                // now you can start recording
                mediaRecorder.start();

                isRecording = true;
            } else {
                // prepare didn't work, release the camera
                mediaRecorder.release();
                // inform user
            }
        }
    }


    private String prepareVideoRecorder() {
        camera = getCameraInstance();
        mediaRecorder = new MediaRecorder();

        // Step 1: Unlock and set camera to MediaRecorder
        camera.unlock();
        mediaRecorder.setCamera(camera);
        mediaRecorder.setMaxDuration(videoSettings.get("duration"));
        mediaRecorder.setOnInfoListener(this);
        // Step 2: Set sources
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
//        mMediarecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
//        mMediarecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
//        mMediarecorder.setVideoFrameRate(videoSettings.get("framerate"));
//        mMediarecorder.setVideoSize(videoSettings.get("resolution_y"),videoSettings.get("resolution_x"));
        // Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
        mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));

        // Step 4: Set output file
        String filepath = getOutputMediaFile(MEDIA_TYPE_VIDEO).getAbsolutePath();
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

    private File getOutputMediaFile(int type) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
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

    /**
     * A safe way to get an instance of the Camera object.
     */
    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        } catch (Exception e) {
            System.out.println("No camera available");
        }
        return c; // returns null if camera is unavailable
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            newVideoPath = prepareVideoRecorder();
            setCamera();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }


    //check functions
    //Never used, Ahmad?!😡😡😡😡😡

    /**
     * Check if this device has a camera
     */
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    @Override
    public void onInfo(MediaRecorder mr, int what, int extra) {
        if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
            mediaRecorder.stop();
            mediaRecorder.release();
            System.out.println(MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED);
            System.out.println(extra);
            System.out.println("Camera is done");
            ServerService.VIDEOURI = Uri.fromFile(new File(newVideoPath));
            setResult(5);
            finish();
        }
    }
}
