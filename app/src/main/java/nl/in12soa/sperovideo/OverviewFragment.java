package nl.in12soa.sperovideo;

import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import nl.in12soa.sperovideo.Adapters.VideoAdapter;
import nl.in12soa.sperovideo.Models.Video;
import nl.in12soa.sperovideo.Services.ApiService;


public class OverviewFragment extends Fragment {

    //Wouter maybe kan je beter OnItemSelectedListener implementen, ff naar kijken.
    OnItemSelectedListener listener;

    private ArrayList<Video> videoArray = new ArrayList<>();
    private String getVideosURL;
    private VideoAdapter videoAdapter;
    private View view;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.overview_fragment, container, false);

        String userID = getActivity().getIntent().getStringExtra("userID");
        getVideosURL = "http://innosportlab.herokuapp.com/users/" + userID + "/videos";

        getVideos();

        return view;
    }

    public interface OnItemSelectedListener
    {
        void onItemSelected(Video video);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listener = (OnItemSelectedListener) getActivity();
    }

    private void getVideos()
    {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, getVideosURL, new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {
                try{
                    JSONArray jsonArray = new JSONArray(response);
                    for(int i = 0; i < jsonArray.length(); i++)
                    {
                        JSONObject responseJSON = jsonArray.getJSONObject(i);
                        String id = responseJSON.getString("_id");
                        String filePath = responseJSON.getString("filePath");
                        String sporter = responseJSON.getString("sporter");
                        String date = responseJSON.getString("created_at");
                        Video video = new Video(id, filePath, sporter, date);
                        videoArray.add(video);
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
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try{
                    String parsedData = new String(error.networkResponse.data, "UTF-8");
                    JSONObject obj = new JSONObject(parsedData);
                    String message = obj.getString("message");
                    Log.d("Error", message);
                } catch (UnsupportedEncodingException | JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        //Add this JSON object request to the requestQueue of the api
        ApiService.getInstance(getActivity().getApplicationContext()).addToRequestQueue(stringRequest);
    }
}
