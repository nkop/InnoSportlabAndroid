package nl.in12soa.sperovideo;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    //    private EditText email;
//    private EditText password;
//    private Button loginButton;
    private Button afstandsbedieningButton;
    private Button analyseButton;
    private Button cameraButton;
    private Context context;
//    private Button registerButton;
//    private String emailString;
//    private String passwordString;

//    public static String loginURL = "http://innosportlab.herokuapp.com/auth/login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        email = (EditText)findViewById(R.id.email);
//        password = (EditText)findViewById(R.id.password);
//        loginButton = (Button)findViewById(R.id.loginButton);
        analyseButton = (Button) findViewById(R.id.test_analyse);
        cameraButton = (Button) findViewById(R.id.test_cam);
        afstandsbedieningButton = (Button) findViewById(R.id.nfcActivityButton);
        context = this;
        analyseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent analyse = new Intent(context, AnalyseActivity.class);
                startActivity(analyse);
            }
        });
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent camera = new Intent(context, ServerListActivity.class);
                startActivity(camera);
            }
        });
        afstandsbedieningButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), NFCActivity.class);
                startActivity(intent);
            }
        });

//        loginButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                emailString = email.getText().toString();
//                passwordString = password.getText().toString();
//
//                AsyncLogin aSyncLogin = new AsyncLogin();
//                aSyncLogin.execute();
//            }
//        });
    }

//    class AsyncLogin extends AsyncTask<Void, Void, String> {
//        @Override
//        protected String doInBackground(Void... voids) {
//
//            String response = "";
//
//            try {
//                URL url = new URL(loginURL);
//                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
//                httpURLConnection.setDoOutput(true);
//                httpURLConnection.setRequestMethod("POST");
//                httpURLConnection.setRequestProperty("Content-Type", "application/json");
//                httpURLConnection.connect();
//
//                JSONObject jsonObject = new JSONObject();
//                jsonObject.put("email", emailString);
//                jsonObject.put("password", passwordString);
//
//                DataOutputStream dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream());
//                dataOutputStream.writeBytes(jsonObject.toString());
//                dataOutputStream.flush();
//                dataOutputStream.close();
//
//                int responseCode = httpURLConnection.getResponseCode();
//
//                if (responseCode == HttpsURLConnection.HTTP_OK) {
//                    String line;
//                    BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
//                    while ((line = br.readLine()) != null) {
//                        response += line;
//                    }
//
//                    JSONObject responseJSON = new JSONObject(response.toString());
//                    if (responseJSON.has("_id")) {
//                        response = "success";
//
//                    } else {
//                        response = "unknown";
//                    }
//                } else {
//                    response = "failure";
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//            return response;
//        }

//        @Override
//        protected void onPostExecute(String result) {
//            super.onPostExecute(result);
//            if (result.equals("unknown")) {
//                Toast.makeText(getApplicationContext(), "Unknown combination of email and password", Toast.LENGTH_LONG).show();
//            } else if (result.equals("failure")) {
//                Toast.makeText(getApplicationContext(), "Failed to log in...", Toast.LENGTH_LONG).show();
//            } else if (result.equals("success")) {
//                Toast.makeText(getApplicationContext(), "Login succeed!", Toast.LENGTH_LONG).show();
//            }
//        }
    //}
}
