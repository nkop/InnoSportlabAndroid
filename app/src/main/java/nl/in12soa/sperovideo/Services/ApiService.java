package nl.in12soa.sperovideo.Services;

import android.content.Context;;
import android.support.v7.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

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
