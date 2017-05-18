package nl.in12soa.sperovideo.Observers;

import android.os.FileObserver;

import nl.in12soa.sperovideo.CameraViewActivity;

/**
 * Created by ahmadrahimi on 5/14/17.
 */

public class VideoFileObserver extends FileObserver {

    CameraViewActivity cameraViewActivity;

    public VideoFileObserver(String path, CameraViewActivity cameraViewActivity) {
        super(path);
        this.cameraViewActivity = cameraViewActivity;
    }

    @Override
    public void onEvent(int event, String path) {
        if(event == FileObserver.CLOSE_WRITE){
            cameraViewActivity.finish();
        }
    }
}
