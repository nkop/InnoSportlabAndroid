package nl.in12soa.sperovideo;

import android.content.SharedPreferences;
import android.hardware.Camera;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import nl.in12soa.sperovideo.Services.ActionBarService;

/**
 * Created by mickd on 15-5-2017.
 */

public class SettingsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private Spinner resolutionSpinner;
    private Spinner fpsSpinner;
    private ArrayList<String> resolutionList = new ArrayList<>();
    private ArrayList<String> fpsList = new ArrayList<>();
    public static final String PREFS = "Preferences";
    public SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ActionBarService.setActionBarTitle(R.string.settings, getSupportActionBar());

        resolutionSpinner = (Spinner) findViewById(R.id.resolution_spinner);
        fpsSpinner = (Spinner) findViewById(R.id.fps_spinner);

        getSupportedCameraResolutions();

        initializeResolutionList();
        initializeFpsList();

        resolutionSpinner.setOnItemSelectedListener(this);
        fpsSpinner.setOnItemSelectedListener(this);
    }

    private void getSupportedCameraResolutions(){
        Camera camera = CameraViewActivity.getCameraInstance();
        Camera.Parameters params = camera.getParameters();
        List<Camera.Size> resolutions = params.getSupportedVideoSizes();
        for(Camera.Size s: resolutions){
            this.resolutionList.add(s.width + "x" + s.height);
        }
    }

    private void initializeResolutionList(){
        ArrayAdapter<String> resolutionAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, resolutionList);
        resolutionSpinner.setAdapter(resolutionAdapter);

    }

    private void initializeFpsList(){
        fpsList.add("30");
        fpsList.add("60");
        fpsList.add("120");
        ArrayAdapter<String> fpsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, fpsList);
        fpsSpinner.setAdapter(fpsAdapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch(parent.getId()){
            case R.id.resolution_spinner:
                System.out.println(parent.getSelectedItem().toString());
                break;
            case R.id.fps_spinner:
                System.out.println(parent.getSelectedItem().toString());
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
