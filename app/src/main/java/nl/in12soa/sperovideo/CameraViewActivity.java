package nl.in12soa.sperovideo;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;
import android.widget.VideoView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import nl.in12soa.sperovideo.Observers.VideoFileObserver;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

public class CameraViewActivity extends AppCompatActivity implements SurfaceHolder.Callback{

    boolean isRecording = false;
    public MediaRecorder mMediarecorder;
    public VideoView vw1;
    public Camera camera;
    public static String newvideopath;
    SurfaceHolder mHolder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_view);
        vw1 = (VideoView) findViewById(R.id.sv_camerapreview);
        mHolder = vw1.getHolder();
        mHolder.addCallback(this);
    }


    private void setCamera() {
        if (isRecording) {
            // stop recording and release camera
            mMediarecorder.stop();  // stop the recording
            mMediarecorder.release(); // release the MediaRecorder object
            camera.lock();         // take camera access back from MediaRecorder

            // inform the user that recording has stopped
            Uri videouri = Uri.fromFile(new File(newvideopath));

            //TODO CAMERA TERUGSTUREN
//            mReceiver.sendData(videouri);
            isRecording = false;
        } else {
            // initialize video camera
            if (newvideopath != null) {
                // Camera is available and unlocked, MediaRecorder is prepared,
                // now you can start recording
                VideoFileObserver videoFileObserver = new VideoFileObserver(newvideopath, this);
                videoFileObserver.startWatching();
                mMediarecorder.start();

                isRecording = true;
            } else {
                // prepare didn't work, release the camera
                mMediarecorder.release();
                // inform user
            }
        }
    }


    private String prepareVideoRecorder(){
        camera = getCameraInstance();
        mMediarecorder = new MediaRecorder();

        // Step 1: Unlock and set camera to MediaRecorder
        camera.unlock();
        mMediarecorder.setCamera(camera);
        mMediarecorder.setMaxDuration(10000);
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
            System.out.println("IllegalStateException preparing MediaRecorder: " + e.getMessage());
            mMediarecorder.release();
            return null;
        } catch (IOException e) {
            System.out.println("IOException preparing MediaRecorder: " + e.getMessage());
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
        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                "VID_"+ timeStamp + ".mp4");
        return mediaFile;
    }

    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            System.out.println("No camera available");
        }
        return c; // returns null if camera is unavailable
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            newvideopath = prepareVideoRecorder();
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
}
