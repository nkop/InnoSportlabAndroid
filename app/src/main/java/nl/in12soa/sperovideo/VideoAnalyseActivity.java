package nl.in12soa.sperovideo;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import nl.in12soa.sperovideo.Services.ApiService;

public class VideoAnalyseActivity extends AppCompatActivity {

    private String filePath;
    private String videoID;
    private VideoView videoView;
    private MediaController mediaController;
    private String addTagUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_analyse);

        addTagUrl = "https://innosportlab.herokuapp.com/tags";

        Intent intent = getIntent();
        filePath = intent.getStringExtra("filePath");
        videoID = intent.getStringExtra("id");


        videoView = (VideoView) findViewById(R.id.videoView);

        if (mediaController == null) {
            mediaController = new MediaController(VideoAnalyseActivity.this);
        }

        Uri uri;
        if(filePath != null)
        {
            uri = Uri.parse(filePath);
        }
        else
        {
            String url = "http://innosportlab.herokuapp.com/videos/" + videoID + "/video";
            uri = Uri.parse(url);
        }

        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);
        videoView.setVideoURI(uri);
        videoView.start();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        if (getIntent().getStringExtra("id") != null)
            getMenuInflater().inflate(R.menu.menu_video, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.tag_add:
                createDialog().show();
                return(true);
        }
        return(super.onOptionsItemSelected(item));
    }

    public AlertDialog createDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.add_tag);

        final EditText tagText = new EditText(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
                );
        tagText.setLayoutParams(lp);
        builder.setView(tagText);

        builder.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (tagText.getText().length() > 0) {
                    Map<String, String> params = new HashMap<>();
                    params.put("tag", tagText.getText().toString());
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

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //do not something
            }
        });

        AlertDialog dialog = builder.create();

        return dialog;
    }
}
