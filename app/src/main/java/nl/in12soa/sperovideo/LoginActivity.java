package nl.in12soa.sperovideo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import nl.in12soa.sperovideo.Services.ApiService;

public class LoginActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;

    private Map<String, String> params = new HashMap<>();

    public static String loginURL = "http://innosportlab.herokuapp.com/users/validate";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = (EditText)findViewById(R.id.email);
        password = (EditText)findViewById(R.id.password);
    }

    public void doLogin(View v)
    {
        if(email.getText().toString().length() != 0 && password.getText().toString().length() != 0)
        {
            params.put("email", email.getText().toString());
            params.put("password", password.getText().toString());
            final Intent intent = new Intent(this, CameraActivity.class);
            JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.POST, loginURL, new JSONObject(params), new Response.Listener<JSONObject>() {
                        //If succesfull
                        @Override
                        public void onResponse(JSONObject response) {
                            Toast.makeText(getApplicationContext(), "Login succesful", Toast.LENGTH_LONG).show();
                            params.clear();
                            startActivity(intent);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            try{
                                String parsedData = new String(error.networkResponse.data, "UTF-8");
                                JSONObject obj = new JSONObject(parsedData);
                                String message = obj.getString("message");
                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                            } catch (UnsupportedEncodingException | JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });

            //Add this JSON object request to the requestQueue of the api
            ApiService.getInstance(getApplicationContext()).addToRequestQueue(jsObjRequest);
        }
        else
        {
            Toast.makeText(getApplicationContext(), "Username and password can't be empty", Toast.LENGTH_LONG).show();
        }
    }
}
