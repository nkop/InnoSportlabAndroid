package nl.in12soa.sperovideo;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import nl.in12soa.sperovideo.Services.CameraService;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CameraService.GROUPEXISTS = false;
        resetWifi();
        (findViewById(R.id.register_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent register = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(register);
            }
        });

        (findViewById(R.id.analyse_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent login = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(login);
            }
        });

        (findViewById(R.id.camera_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
                startActivity(intent);
            }
        });

        (findViewById(R.id.remote_button)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RemoteActivity.class);
                startActivity(intent);
            }
        });
    }

    private void resetWifi(){
        WifiManager wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(false);
        wifiManager.setWifiEnabled(true);
    }
}
