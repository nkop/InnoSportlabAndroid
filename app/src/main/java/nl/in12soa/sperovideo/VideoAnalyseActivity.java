package nl.in12soa.sperovideo;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import nl.in12soa.sperovideo.Services.ApiService;

public class VideoAnalyseActivity extends AppCompatActivity implements MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener,
        MediaPlayer.OnVideoSizeChangedListener, SurfaceHolder.Callback, MediaController.MediaPlayerControl{

    private MediaController mediaController;
    private String addTagUrl;
    private String addCommentUrl;
    private SurfaceView surfaceView;
    private MediaPlayer mediaPlayer;
    private Uri videoUri;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_analyse);

        surfaceView = (SurfaceView) findViewById(R.id.surface_view);

        addTagUrl = "https://innosportlab.herokuapp.com/tags";
        addCommentUrl = "https://innosportlab.herokuapp.com/comments";
        handler = new Handler();
        Intent intent = getIntent();
        String filePath = intent.getStringExtra("filePath");
        String videoID = intent.getStringExtra("id");

        surfaceView.getHolder().addCallback(this);

        if(filePath != null)
        {
            videoUri = Uri.parse(filePath);
        }
        else
        {
            String url = "http://innosportlab.herokuapp.com/videos/" + videoID + "/video";
            videoUri = Uri.parse(url);
        }

        initializeMediaPlayer();
    }

    private void initializeMediaPlayer(){
        try{
            mediaController = new MediaController(this);
            mediaPlayer = MediaPlayer.create(this, videoUri);
            mediaPlayer.setOnBufferingUpdateListener(this);
            mediaPlayer.setOnCompletionListener(this);
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setScreenOnWhilePlaying(true);
            mediaPlayer.setOnVideoSizeChangedListener(this);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        }catch(Exception e){
            e.printStackTrace();
        }

    }
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!getIntent().getStringExtra("id").equals("1"))
            getMenuInflater().inflate(R.menu.menu_video, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.tag_add:
                createDialog("Tag").show();
                return(true);
            case R.id.comment_add:
                createDialog("Comment").show();
                return(true);
        }
        return(super.onOptionsItemSelected(item));
    }

    public AlertDialog createDialog(String item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if(item.equals("Tag"))
        {
            builder.setTitle(R.string.add_tag);
        }
        else
        {
            builder.setTitle(R.string.add_comment);
        }


        final EditText text = new EditText(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        text.setLayoutParams(lp);
        builder.setView(text);

        if(item.equals("Tag"))
        {
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
        }
        else
        {
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

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try{
            mediaPlayer.setDisplay(surfaceView.getHolder());
            mediaPlayer.start();
        }catch(Exception e){
            e.printStackTrace();
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
        mediaPlayer.stop();
        mediaPlayer.release();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mediaController.setMediaPlayer(this);
        mediaController.setAnchorView(surfaceView);
        mediaController.setEnabled(true);

        handler.post(new Runnable(){
            public void run(){
                mediaController.show();
            }
        });

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
