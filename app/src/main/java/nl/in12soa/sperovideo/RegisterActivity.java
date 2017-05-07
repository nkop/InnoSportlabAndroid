package nl.in12soa.sperovideo;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
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
import java.util.regex.Pattern;

import nl.in12soa.sperovideo.Services.ApiService;

/**
 * Created by mickd on 4-5-2017.
 */

public class RegisterActivity extends AppCompatActivity {

    // NFC stuff
    private NfcAdapter nfcAdapter;


    //Instantiate the form fields here

    //Required
    EditText emailInput;
    EditText usernameInput;
    EditText passwordInput;
    EditText passwordConfirmInput;

    //Optional
    EditText firstNameInput;
    EditText lastNameInput;
    EditText cityInput;
    TextView nfcText;
    String nfcId;

    //Map to put params in for request
    private Map<String, String> params = new HashMap<>();

    //Url to post the user to
    //private String createUserURL = "https://innosportlab.herokuapp.com/users";
    private String validateSignupURL = "https://innosportlab.herokuapp.com/users/validateSignup";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        //Initialize form fields
        emailInput = (EditText) findViewById(R.id.email_input);
        usernameInput = (EditText) findViewById(R.id.username_input);
        passwordInput = (EditText) findViewById(R.id.password_input);
        passwordConfirmInput = (EditText) findViewById(R.id.password_confirm_input);
        firstNameInput = (EditText) findViewById(R.id.first_name_input);
        lastNameInput = (EditText) findViewById(R.id.last_name_input);
        cityInput = (EditText) findViewById(R.id.city_input);
        nfcText = (TextView) findViewById(R.id.registerNfc);

    }

    //User gets registered here.
    public void registerUser(View v) {
        if (formIsValid()) {
            createUserObject();
            //Create a json object request
            JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    //POST, URL, PARAMS, RESPONSE LISTENER
                    (Request.Method.POST, validateSignupURL, new JSONObject(params), new Response.Listener<JSONObject>() {

                        //If succesfull
                        @Override
                        public void onResponse(JSONObject response) {

                            Toast.makeText(getApplicationContext(), "Register successful", Toast.LENGTH_LONG).show();
                            params.clear();
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            try{
                                String parsedData = new String(error.networkResponse.data, "UTF-8");
                                JSONObject obj = new JSONObject(parsedData);
                                String message = obj.getString("message");
                                Log.d("Message", message);
                                if(message.contains("Username")){
                                    usernameInput.setError("Gebruikersnaam bestaat al");
                                }
                                if(message.contains("Email")){
                                    emailInput.setError("Email adres bestaat al");
                                }
                            }catch (UnsupportedEncodingException | JSONException e) {
                                e.printStackTrace();
                            }
                            //Toast.makeText(getApplicationContext(), "Registration not successful", Toast.LENGTH_LONG).show();
                        }
                    });
            //Add this JSON object request to the requestQueue of the api
            ApiService.getInstance(getApplicationContext()).addToRequestQueue(jsObjRequest);
        }
    }

    //Set the params object.
    private void createUserObject() {
        params.put("email", emailInput.getText().toString());
        params.put("userName", usernameInput.getText().toString());
        params.put("firstName", firstNameInput.getText().toString());
        params.put("lastName", lastNameInput.getText().toString());
        params.put("password", passwordInput.getText().toString());
        params.put("city", cityInput.getText().toString());
        params.put("confirmpassword", passwordConfirmInput.getText().toString());
        params.put("rfid", nfcId);
    }

    private boolean formIsValid() {
        //Make sure all fields are valid.
        return emailIsValid() && userNameIsValid() && passwordIsValid();
    }

    //Single field validations
    private boolean emailIsValid() {
        //Check if the e-mail is valid using regex pattern.
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        if (pattern.matcher(emailInput.getText().toString()).matches()) {
            return true;
        }
        emailInput.setError("Vul een geldig email-adres in");
        return false;
    }

    private boolean userNameIsValid() {
        //Minimum length of 1
        if (usernameInput.getText().toString().length() > 0) {
            return true;
        }
        usernameInput.setError("Vul een geldige gebruiksernaam in");
        return false;
    }

    private boolean passwordIsValid() {
        //Equal passwords
        Log.d("Password", passwordInput.getText().toString());
        Log.d("Password confirm", passwordConfirmInput.getText().toString());
        if (passwordInput.getText().toString().equals(passwordConfirmInput.getText().toString())) {
            //Minimum length of 6
            if (passwordInput.getText().length() >= 6) {
                return true;
            }
            passwordInput.setError("Wachtwoord moet minimaal 6 karakters lang zijn");
        } else {
            passwordConfirmInput.setError("Wachtwoorden moeten gelijk zijn");
        }
        return false;
    }

    // nfc Methods
    @Override
    protected void onResume() {
        super.onResume();

        enableForegroundDispatchSystem();
    }

    @Override
    protected void onPause() {
        super.onPause();

        disableForegroundDispatchSystem();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (intent.hasExtra(NfcAdapter.EXTRA_ID)) {
            byte[] serial = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
            String serialstring = "";

            for (int i = 0; i < serial.length; i++) {
                String x = Integer.toHexString(((int) serial[i] & 0xff));
                if (x.length() == 1) {
                    x = '0' + x;
                }
                serialstring += x + ' ';
            }
            nfcId = serialstring;
            nfcText.setText("Uw NFC id is: " + serialstring);
        }
    }

    private void enableForegroundDispatchSystem() {

        Intent intent = new Intent(this, RegisterActivity.class).addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        IntentFilter[] intentFilters = new IntentFilter[]{};

        nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilters, null);
    }

    private void disableForegroundDispatchSystem() {
        nfcAdapter.disableForegroundDispatch(this);
    }
}
