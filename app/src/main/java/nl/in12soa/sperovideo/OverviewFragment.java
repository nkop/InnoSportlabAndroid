package nl.in12soa.sperovideo;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileFilter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import nl.in12soa.sperovideo.Adapters.VideoAdapter;
import nl.in12soa.sperovideo.Models.Video;
import nl.in12soa.sperovideo.Services.ApiService;

public class OverviewFragment extends Fragment {

    OnItemSelectedListener listener;

    private ArrayList<Video> videoArray = new ArrayList<>();
    private File[] videoLocalArray;
    private VideoAdapter videoAdapter;
    private View view;
    private String getVideosURL;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.overview_fragment, container, false);
        // check if user just logged in or if stored in localStorage
        String userID = getActivity().getIntent().getStringExtra("userID");
        if (userID == null) {
            SharedPreferences sharedPreferences = getActivity().getApplicationContext().getSharedPreferences("SPEROVIDEO", 0);
            userID = sharedPreferences.getString("id", null);
        }
        getVideosURL = "http://innosportlab.herokuapp.com/users/" + userID + "/videos";
        getVideos();
/*
//        online = false;
        if (!OverviewActivity.ONLINE) {

        }

        videoAdapter = new VideoAdapter(getActivity().getApplicationContext(), videoArray);
        ListView listView = (ListView) view.findViewById(R.id.video_list);
        videoAdapter.notifyDataSetChanged();
        listView.setAdapter(videoAdapter);

        AdapterView.OnItemClickListener mMessageClickedHandler = new
                AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView parent, View v, int position, long id) {
                        Video video = videoAdapter.getItem(position);
                        listener.onItemSelected(video);
                    }
                };

        listView.setOnItemClickListener(mMessageClickedHandler);
*/

        return view;
    }


    public interface OnItemSelectedListener {
        void onItemSelected(Video video);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listener = (OnItemSelectedListener) getActivity();
    }

    public void getVideos() {
        videoArray = new ArrayList<>();
        videoArray.clear();
        if (!OverviewActivity.ONLINE) {
            try {
                String path = Environment.getExternalStorageDirectory() + "/" + getActivity().getApplicationContext().getPackageName();
                File dir = new File(path);
                if (!dir.isDirectory()) {
                    dir.mkdir();
                }
                videoLocalArray = dir.listFiles(new FileFilter() {
                    @Override
                    public boolean accept(File file) {
                        if (file.isFile()) {
                            if (file.getName().equals(".nomedia"))
                                return false;
                            return checkFileExtension(file);
                        } else if (file.isDirectory()) {
                            return false;
                        } else {
                            return false;
                        }
                    }
                });
                if (videoLocalArray != null) {
                    for (int i = 0; i < videoLocalArray.length; i++) {
                        Video video = new Video("1", videoLocalArray[i].getAbsolutePath(), null, null);
                        videoArray.add(video);
                    }
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "There are no videos yet.", Toast.LENGTH_LONG).show();
                }
                setVideoAdapter(videoArray);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            StringRequest stringRequest = new StringRequest(Request.Method.GET, getVideosURL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject responseJSON = jsonArray.getJSONObject(i);
                            String id = responseJSON.getString("_id");
                            String sporter = responseJSON.getString("sporter");
                            String date = responseJSON.getString("created_at");
                            Video video = new Video(id, null, sporter, date);
                            videoArray.add(video);
                        }
                        setVideoAdapter(videoArray);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    try {
                        String parsedData = new String(error.networkResponse.data, "UTF-8");
                        JSONObject obj = new JSONObject(parsedData);
                        String message = obj.getString("message");
                        Log.d("Error", message);
                    } catch (UnsupportedEncodingException | NullPointerException | JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            //Add this JSON object request to the requestQueue of the api
            ApiService.getInstance(getActivity().getApplicationContext()).addToRequestQueue(stringRequest);
        }
    }

    public void setVideoAdapter(ArrayList<Video> videoArray)
    {
        videoAdapter = new VideoAdapter(getActivity().getApplicationContext(), videoArray);
        ListView listView = (ListView) view.findViewById(R.id.video_list);
        videoAdapter.notifyDataSetChanged();
        listView.setAdapter(videoAdapter);

        AdapterView.OnItemClickListener mMessageClickedHandler = new
                AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView parent, View v, int position, long id) {
                        Video video = videoAdapter.getItem(position);
                        listener.onItemSelected(video);
                    }
                };

        listView.setOnItemClickListener(mMessageClickedHandler);
    }

    private boolean checkFileExtension(File fileName) {
        String ext = getFileExtension(fileName);
        if (ext == null) return false;
        try {
            if (SupportedFileFormat.valueOf(ext.toUpperCase()) != null) {
                return true;
            }
        } catch (IllegalArgumentException e) {
            return false;
        }
        return false;
    }

    public String getFileExtension(File f) {
        return getFileExtension(f.getName());
    }

    public String getFileExtension(String fileName) {
        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            return fileName.substring(i + 1);
        } else
            return null;
    }

    public enum SupportedFileFormat {
        MP4("mp4");

        private String filesuffix;

        SupportedFileFormat(String filesuffix) {
            this.filesuffix = filesuffix;
        }

        public String getFilesuffix() {
            return filesuffix;
        }
    }

}
