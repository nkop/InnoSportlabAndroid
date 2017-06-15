package nl.in12soa.sperovideo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import nl.in12soa.sperovideo.Services.ActionBarService;
import nl.in12soa.sperovideo.Services.ApiService;

import static android.content.Intent.FLAG_ACTIVITY_NO_HISTORY;

public class LoginActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private ProgressBar spinner;
    private Button loginButton;
    private SharedPreferences settings;
    private Intent overviewIntent;

    private String id;
    private String emailString;
    private String rfid;

    private Map<String, String> params = new HashMap<>();

    public static String loginURL = "http://innosportlab.herokuapp.com/users/validate";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ActionBarService.setActionBarTitle(R.string.login, getSupportActionBar());

        email = (EditText)findViewById(R.id.email_input_login);
        password = (EditText)findViewById(R.id.password_input_login);
        spinner = (ProgressBar) findViewById(R.id.login_spinner);
        loginButton = (Button) findViewById(R.id.login_button);
        spinner.setVisibility(View.GONE);

        settings = getApplicationContext().getSharedPreferences("SPEROVIDEO", 0);
        overviewIntent = new Intent(getApplicationContext(), OverviewActivity.class);

        if(settings.getString("id", null) != null) {
            overviewIntent.putExtra("userID", id);
            startActivity(overviewIntent);
            enableLogin();
        }
    }

    public void doLogin(View v)
    {
        if(email.getText().toString().length() != 0 && password.getText().toString().length() != 0)
        {
            params.put("email", email.getText().toString());
            params.put("password", password.getText().toString());
            disableLogin();
            JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.POST, loginURL, new JSONObject(params), new Response.Listener<JSONObject>() {
                        //If succesfull
                        @Override
                        public void onResponse(JSONObject response) {
                            if (response != null && response.length() > 0)
                            {
                                try{
                                    id = response.getString("_id");
                                    emailString = response.getString("email");
                                    rfid = response.getString("rfid");
                                }
                                catch(Exception e)
                                {
                                    Toast.makeText(getApplicationContext(), "Ophalen gegevens mislukt. Probeer het later opnieuw", Toast.LENGTH_LONG).show();
                                    e.printStackTrace();
                                }

                                SharedPreferences.Editor editor = settings.edit();
                                editor.putString("id", id);
                                editor.putString("email", emailString);
                                editor.putString("rfid", rfid);
                                editor.apply();

                                overviewIntent.putExtra("userID", id);
                                startActivity(overviewIntent);
                                params.clear();
                                enableLogin();
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(), "Ophalen gegevens mislukt. Probeer het opnieuw", Toast.LENGTH_LONG).show();
                            }

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            try{
                                if(error.networkResponse != null)
                                {
                                    String parsedData = new String(error.networkResponse.data, "UTF-8");
                                    JSONObject obj = new JSONObject(parsedData);
                                    String message = obj.getString("message");
                                    Log.d("Error", message);
                                    if(message.equals("Invalid email or password"))
                                    {
                                        Toast.makeText(getApplicationContext(), "Ongeldig e-mail of wachtwoord", Toast.LENGTH_LONG).show();
                                    }
                                }
                                else
                                {
                                    Toast.makeText(getApplicationContext(), "Geen internetverbinding, kan niet inloggen", Toast.LENGTH_LONG).show();
                                }

                            } catch (UnsupportedEncodingException | JSONException e) {
                                e.printStackTrace();
                            }
                            enableLogin();
                        }
                    });

            //Add this JSON object request to the requestQueue of the api
            jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                    60000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            ApiService.getInstance(getApplicationContext()).addToRequestQueue(jsObjRequest);
        }
        else
        {
            Toast.makeText(getApplicationContext(), "Vul een e-mail en een wachtwoord in", Toast.LENGTH_LONG).show();
        }
    }

    private void enableLogin(){
        spinner.setVisibility(View.GONE);
        loginButton.setVisibility(View.VISIBLE);
    }

    private void disableLogin(){
        spinner.setVisibility(View.VISIBLE);
        loginButton.setVisibility(View.GONE);
    }
}
