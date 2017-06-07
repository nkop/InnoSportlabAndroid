package nl.in12soa.sperovideo;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

public class VideoAnalyseActivity extends AppCompatActivity {

    private String filePath;
    private VideoView videoView;
    private MediaController mediaController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_analyse);

        Intent intent = getIntent();
        filePath = intent.getStringExtra("filePath");
        videoView = (VideoView) findViewById(R.id.videoView);

        if (mediaController == null) {
            mediaController = new MediaController(VideoAnalyseActivity.this);
        }

        Uri uri = Uri.parse(filePath);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);
        videoView.setVideoURI(uri);
        videoView.start();
    }
}
