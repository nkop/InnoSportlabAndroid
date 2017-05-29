package nl.in12soa.sperovideo;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.VideoView;

public class VideoAnalyseActivity extends AppCompatActivity {

    private String filePath;
    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_analyse);

        Intent intent = getIntent();
        filePath = intent.getStringExtra("filePath");

        videoView = (VideoView)findViewById(R.id.videoView);

        // Change to filepath later
        String videoURL = "http://techslides.com/demos/sample-videos/small.mp4";
//        Uri uri = Uri.parse(Environment.getExternalStorageDirectory()+"/"+getApplicationContext().getPackageName()+"/wifip2pshared-1493715957919.mp4");
        Uri uri = Uri.parse(filePath);
        //Uri uri = Uri.parse(videoURL);
        videoView.setVideoURI(uri);
        videoView.start();
    }
}
