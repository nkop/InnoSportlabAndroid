package nl.in12soa.sperovideo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button afstandsbedieningButton;
//    private Button analyseButton;
    private Button cameraButton;
    private Button registerButton;
    private Context context;
//    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        analyseButton = (Button) findViewById(R.id.test_analyse);
        cameraButton = (Button) findViewById(R.id.test_cam);
        afstandsbedieningButton = (Button) findViewById(R.id.nfcActivityButton);
        registerButton = (Button) findViewById(R.id.register_button);

        context = this;

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent register = new Intent(context, RegisterActivity.class);
                startActivity(register);
            }
        });
        (findViewById(R.id.test_analyse)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent analyse = new Intent(context, AnalyseActivity.class);
                startActivity(analyse);
            }
        });
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
                startActivity(intent);
            }
        });
        afstandsbedieningButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RemoteActivity.class);
                startActivity(intent);
            }
        });

    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }

}
