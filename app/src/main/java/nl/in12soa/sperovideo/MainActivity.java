package nl.in12soa.sperovideo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {


    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button cameraButton = (Button) findViewById(R.id.test_cam);
        Button remoteButton = (Button) findViewById(R.id.nfcActivityButton);
        Button registerButton = (Button) findViewById(R.id.register_button);
        Button analyseButton = (Button) findViewById(R.id.analyse_button);

        this.context = this;

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent register = new Intent(context, RegisterActivity.class);
                startActivity(register);
            }
        });
<<<<<<< HEAD
=======
        (findViewById(R.id.test_analyse)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent analyse = new Intent(context, AnalyseActivity.class);
                startActivity(analyse);
            }
        });
>>>>>>> cd210812e8c365bebe39adbdf6ca7d7dee7c7809
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });
        remoteButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), NFCActivity.class);
                startActivity(intent);
            }
        });

        analyseButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(), AnalyseActivity.class);
                startActivity(intent);
            }
        });

    }

}
