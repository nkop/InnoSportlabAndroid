package nl.in12soa.sperovideo.Services;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mickd on 6-4-2017.
 */

public class ApiService extends AppCompatActivity {

    //Current instance of this class
    private static ApiService mInstance;
    //RequestQueue for Volley
    private RequestQueue mRequestQueue;
    //Context
    private static Context mCtx;

    private ApiService(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();
    }

    //Returns instance of this class. If instance == null, it creates a new one.
    public static synchronized ApiService getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new ApiService(context);
        }
        return mInstance;
    }

    //Returns the RequestQueue for Volley. If request queue == null, it creates a new one.
    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    //Adds a new requets to the queue.
    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }
}
