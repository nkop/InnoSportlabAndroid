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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nl.in12soa.sperovideo.Services.ActionBarService;

/**
 * Created by mickd on 15-5-2017.
 */

public class SettingsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private Spinner resolutionSpinner;
    private Spinner fpsSpinner;
    private ArrayList<String> resolutionList = new ArrayList<>();
    private ArrayList<String> resolutionListValues = new ArrayList<>();
    private ArrayList<String> fpsList = new ArrayList<>();
    private HashMap<String, String> resolutionMap = new HashMap<>();
    public static final String PREFS = "CameraSettings";
    public SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ActionBarService.setActionBarTitle(R.string.settings, getSupportActionBar());

        resolutionSpinner = (Spinner) findViewById(R.id.resolution_spinner);
        fpsSpinner = (Spinner) findViewById(R.id.fps_spinner);

//        getSupportedCameraResolutions();

        initializeResolutionList();
        initializeFpsList();

        preferences = getSharedPreferences(PREFS, 0);
        String resolution_quality  = preferences.getString("resolution_quality", null);
        String fps = preferences.getString("fps", null);
        if(resolution_quality!= null){
            int index = resolutionListValues.indexOf(resolution_quality);
            String indexShow = resolutionList.get(index);
            resolutionSpinner.setSelection(resolutionList.indexOf(indexShow));
        }
        if(fps != null){
            fpsSpinner.setSelection(fpsList.indexOf(fps));
        }

        resolutionSpinner.setOnItemSelectedListener(this);
        fpsSpinner.setOnItemSelectedListener(this);
    }

//    private void getSupportedCameraResolutions(){
//        Camera camera = CameraViewActivity.getCameraInstance();
//        Camera.Parameters params = camera.getParameters();
//        List<Camera.Size> resolutions = params.getSupportedVideoSizes();
//        for(Camera.Size s: resolutions){
//            this.resolutionList.add(s.width + "x" + s.height);
//        }
//    }

    private void initializeResolutionList(){
        resolutionList.add("Laag");
        resolutionList.add("Gemiddeld");
        resolutionList.add("Hoog");
        resolutionListValues.add("0");
        resolutionListValues.add("1");
        resolutionListValues.add("2");
        resolutionMap.put("Laag", "0");
        resolutionMap.put("Gemiddeld", "1");
        resolutionMap.put("Hoog", "2");
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
        SharedPreferences.Editor editor = preferences.edit();
        switch(parent.getId()){
            case R.id.resolution_spinner:
                String selectedResolution = parent.getSelectedItem().toString();
                String resolutionValue = resolutionMap.get(selectedResolution);
                editor.putString("resolution_quality", resolutionValue);
//                String selectedResolution = parent.getSelectedItem().toString();
//                String[] resolutionValues = selectedResolution.split("x");
//
//                editor.putString("resolutionX", resolutionValues[0]);
//                editor.putString("resolutionY", resolutionValues[1]);

                Toast.makeText(getApplicationContext(), "Resolutie opgeslagen", Toast.LENGTH_LONG).show();
                break;
            case R.id.fps_spinner:
                editor.putString("fps", parent.getSelectedItem().toString());
                Toast.makeText(getApplicationContext(), "Frames per seconde(fps) succesvol opgeslagen", Toast.LENGTH_LONG).show();
                break;
        }

        editor.apply();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
