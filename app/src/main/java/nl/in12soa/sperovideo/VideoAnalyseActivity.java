package nl.in12soa.sperovideo;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bikomobile.multipart.Multipart;
import com.bikomobile.multipart.MultipartRequest;
import com.devbrackets.android.exomedia.listener.OnPreparedListener;
import com.devbrackets.android.exomedia.ui.widget.VideoView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import nl.in12soa.sperovideo.Services.ApiService;

public class VideoAnalyseActivity extends AppCompatActivity implements MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnCompletionListener, OnPreparedListener,
        MediaPlayer.OnVideoSizeChangedListener, SurfaceHolder.Callback, MediaController.MediaPlayerControl {

    private MediaController mediaController;
    private String addTagUrl;
    private String addVideoUrl;
    private String addCommentUrl;
    private SurfaceView surfaceView;
    private MediaPlayer mediaPlayer;
    private Uri videoUri;
    private Handler handler;
    private String videoPath;
    private String slowMotionRate;
    boolean onlineVideo = false;
    private MenuItem uploadMenuItem;

    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_analyse);

//        surfaceView = (SurfaceView) findViewById(R.id.surface_view);
        SharedPreferences localStorage = this.getSharedPreferences("SPEROVIDEO", 0);
        String userName = localStorage.getString("userName", null);
        addVideoUrl = "http://innosportlab.herokuapp.com/videos/" + userName;
        addTagUrl = "http://136.144.128.236:3001/tags";
        addCommentUrl = "http://136.144.128.236:3001/comments";
        handler = new Handler();
        Intent intent = getIntent();
        String filePath = intent.getStringExtra("filePath");
        String videoID = intent.getStringExtra("id");
        slowMotionRate = intent.getStringExtra("slowMotionRate");

        videoPath = filePath;

//        surfaceView.getHolder().addCallback(this);

        if (filePath != null) {
            videoUri = Uri.parse(filePath);
        } else {
            String url = "http://136.144.128.236:3001/videos/" + videoID + "/video";
            System.out.println(url);
            videoUri = Uri.parse(url);
            onlineVideo = true;
        }

        initializeMediaPlayer();
    }

    private void initializeMediaPlayer() {
        // Make sure to use the correct VideoView import
        videoView = (VideoView)findViewById(R.id.video_view);
        videoView.setOnPreparedListener(this);

        //For now we just picked an arbitrary item to play
        videoView.setVideoURI(videoUri);
        videoView.setPlaybackSpeed(Float.parseFloat(slowMotionRate));
//        mediaController = new MediaController(this);
//        if (onlineVideo) {
//            try {
//                if(mediaPlayer == null){
//                    mediaPlayer = new MediaPlayer();
//                }
//                mediaPlayer.reset();
//                mediaPlayer.setDataSource(videoUri.toString());
//                mediaPlayer.prepareAsync();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        } else {
//            if(mediaPlayer == null){
//                mediaPlayer.reset();
//                mediaPlayer = MediaPlayer.create(this, videoUri);
//            }
//        }
//        try {
//            mediaPlayer.setOnBufferingUpdateListener(this);
//            mediaPlayer.setOnCompletionListener(this);
//            mediaPlayer.setOnPreparedListener(this);
//            mediaPlayer.setScreenOnWhilePlaying(true);
//            mediaPlayer.setOnVideoSizeChangedListener(this);
//            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//            mediaPlayer.setPlaybackParams(mediaPlayer.getPlaybackParams().setSpeed(Float.parseFloat(slowMotionRate)));
//        } catch (Exception e) {
//            e.printStackTrace();
//            if (mediaPlayer != null) {
//                mediaPlayer.stop();
//                mediaPlayer.release();
//                mediaController.hide();
//                mediaController.setEnabled(false);
//            }
//        }

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_video, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (!onlineVideo) {
            View v = findViewById(R.id.comment_add);
            menu.findItem(R.id.comment_add).setVisible(false);
            menu.findItem(R.id.tag_add).setVisible(false);
            uploadMenuItem = menu.findItem(R.id.upload_video);
        } else {
            menu.findItem(R.id.upload_video).setVisible(false);

        }
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.tag_add:
                createDialog("Tag").show();
                return (true);
            case R.id.comment_add:
                createDialog("Comment").show();
                return (true);
            case R.id.upload_video:
                uploadVideo();
                return (true);
        }
        return (super.onOptionsItemSelected(item));
    }

    public void uploadVideo() {
        Toast.makeText(getApplicationContext(), "Bezig met het uploaden van de video...", Toast.LENGTH_LONG).show();
        uploadMenuItem.setVisible(false);
        Multipart multipart = new Multipart(this);
        File videoToUpload = new File(videoUri.toString());
        multipart.addFile("video/mp4", "file", videoToUpload.getName(), videoUri);

        MultipartRequest request = multipart.getRequest(addVideoUrl, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                Log.d("UPLOAD_VIDEO", "SUCCESS");
                uploadMenuItem.setVisible(true);
                Toast.makeText(getApplicationContext(), "Video is succesvol geupload!", Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("UPLOAD_VIDEO", "ERROR");
                uploadMenuItem.setVisible(true);
                Toast.makeText(getApplicationContext(), "Er is iets fout gegaan met het uploaden van de video!", Toast.LENGTH_LONG).show();
            }
        });
        ApiService.getInstance(getApplicationContext()).addToRequestQueue(request);
    }

    public AlertDialog createDialog(String item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if (item.equals("Tag")) {
            builder.setTitle(R.string.add_tag);
        } else {
            builder.setTitle(R.string.add_comment);
        }


        final EditText text = new EditText(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        text.setLayoutParams(lp);
        builder.setView(text);

        if (item.equals("Tag")) {
            builder.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (text.getText().length() > 0) {
                        Map<String, String> params = new HashMap<>();
                        params.put("tag", text.getText().toString());
                        params.put("videoId", getIntent().getStringExtra("id"));
                        //params.put("videoId", "591d867c2a9e2534342914b1");
                        JsonObjectRequest request = new JsonObjectRequest(
                                Request.Method.POST, addTagUrl, new JSONObject(params), new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Toast.makeText(getApplicationContext(), R.string.tag_success, Toast.LENGTH_SHORT).show();
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getApplicationContext(), R.string.tag_fail, Toast.LENGTH_LONG).show();
                            }
                        }
                        );
                        ApiService.getInstance(getApplicationContext()).addToRequestQueue(request);

                    }
                }
            });
        } else {
            builder.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (text.getText().length() > 0) {
                        Map<String, String> params = new HashMap<>();
                        params.put("comment", text.getText().toString());
                        params.put("videoId", getIntent().getStringExtra("id"));
                        JsonObjectRequest request = new JsonObjectRequest(
                                Request.Method.POST, addCommentUrl, new JSONObject(params), new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Toast.makeText(getApplicationContext(), R.string.comment_success, Toast.LENGTH_SHORT).show();
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getApplicationContext(), R.string.comment_fail, Toast.LENGTH_LONG).show();
                            }
                        }
                        );
                        ApiService.getInstance(getApplicationContext()).addToRequestQueue(request);

                    }
                }
            });
        }


        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //do not something
            }
        });

        AlertDialog dialog = builder.create();

        return dialog;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        Log.d("BUFFERING_VIDEO_", "%" + percent);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            mediaPlayer.setDisplay(holder);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Er is iets mis gegaan met het laden van de video!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mediaController.show();
        return false;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaController.setEnabled(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mp.release();
        mediaController.setEnabled(false);
    }

    @Override

    public void onPrepared() {
//        mediaController.setMediaPlayer(this);
//        mediaController.setAnchorView(surfaceView);
//        mediaController.setEnabled(true);
//        handler.post(new Runnable() {
//            public void run() {
//                mediaController.show();
//            }
//        });
//        mediaPlayer.start();\
        videoView.start();
    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {

    }

    @Override
    public void start() {
        mediaPlayer.start();
    }

    @Override
    public void pause() {
        mediaPlayer.pause();
    }

    @Override
    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    @Override
    public int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    @Override
    public void seekTo(int pos) {
        mediaPlayer.seekTo(pos);
    }

    @Override
    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }

}
