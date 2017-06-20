package nl.in12soa.sperovideo;

import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.os.Environment;
import android.support.v4.app.FragmentManager;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nl.in12soa.sperovideo.Models.Video;
import nl.in12soa.sperovideo.Services.ActionBarService;

public class OverviewActivity extends AppCompatActivity implements OverviewFragment.OnItemSelectedListener {

    private boolean hasOnePane;
    public static boolean ONLINE = false;
    private NfcAdapter nfcAdapter;
    private List<String> slowMotionRates = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);
        ActionBarService.setActionBarTitle(R.string.analyse, getSupportActionBar());
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        initializeSlowMotionRatesList();

        hasOnePane = !getResources().getBoolean(R.bool.dual_pane);

        if (hasOnePane) {
            FragmentManager fm = getSupportFragmentManager();
            if (fm.findFragmentByTag("list") == null) {
                fm.beginTransaction().add(R.id.framelayout, new OverviewFragment(), "list").commit();
            }
        }
    }

    @Override
    public void onItemSelected(final Video video) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.pick_slowmotion);
        builder.setItems(slowMotionRates.toArray(new CharSequence[slowMotionRates.size()]), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String rate = slowMotionRates.get(which);
                if(rate == null){
                    rate = "1";
                }
                Intent intent = new Intent(getApplicationContext(), VideoAnalyseActivity.class);
                intent.putExtra("filePath", video.filePath);
                intent.putExtra("id", video._id);
                intent.putExtra("slowMotionRate", rate);
                startActivity(intent);
            }
        });
        builder.create();
        builder.show();
    }

    public void setOnline(boolean online) {
        ONLINE = online;
        OverviewFragment overviewFragment = (OverviewFragment) getSupportFragmentManager().findFragmentById(R.id.overview_fragment);
        overviewFragment.getVideos();
    }

    // nfc Methods
    @Override
    public void onResume() {
        super.onResume();

        enableForegroundDispatchSystem();
    }

    @Override
    public void onPause() {
        super.onPause();

        disableForegroundDispatchSystem();
    }

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (nfcAdapter != null && nfcAdapter.isEnabled()) {
            if (intent.hasExtra(NfcAdapter.EXTRA_ID)) {
                final String serialString = getRfid(intent);

                File[] videoLocalArray = getFiles(serialString);

                Video video = findNewestVideo(videoLocalArray, serialString);
                if (video != null)
                    onItemSelected(video);
            }
        }
    }

    private String getRfid(Intent intent) {
        final byte[] serial = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
        String serialstring = "";

        for (int i = 0; i < serial.length; i++) {
            String x = Integer.toHexString(((int) serial[i] & 0xff));
            if (x.length() == 1) {
                x = '0' + x;
            }
            serialstring += x + ' ';
        }
        return serialstring;
    }

    private File[] getFiles(String pSerialString) {
        final String serialString = pSerialString;
        String path = Environment.getExternalStorageDirectory() + "/" + this.getApplicationContext().getPackageName();
        File dir = new File(path);
        if (!dir.isDirectory()) {
            dir.mkdir();
        }

        return dir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                Pattern p = Pattern.compile("^" + serialString + "-video-[\\d]+.mp4$");
                Matcher m = p.matcher(file.getName());
                if (m.find())
                    return true;
                return false;
            }
        });
    }

    private Video findNewestVideo(File[] videoLocalArray, String serialString) {
        ArrayList<Video> videoArray = new ArrayList<>();
        if (videoLocalArray != null && videoLocalArray.length > 0) {
            for (int i = 0; i < videoLocalArray.length; i++) {
                Pattern p = Pattern.compile(serialString + "-video-([\\d]+).mp4");
                Matcher m = p.matcher(videoLocalArray[i].getName());
                if (m.find()) {
                    MatchResult mr = m.toMatchResult();
                    Video video = new Video(mr.group(1), videoLocalArray[i].getAbsolutePath(), null, null);
                    videoArray.add(video);
                }
            }
            int j = 0;
            for (int i = 1; i < videoArray.size(); i++) {
                if (Long.parseLong(videoArray.get(i)._id) > Long.parseLong(videoArray.get(j)._id)) {
                    j = i;
                }
            }
            return videoArray.get(j);
        }
        return null;
    }

    private void enableForegroundDispatchSystem() {

        if (nfcAdapter != null && nfcAdapter.isEnabled()) {

            Intent intent = new Intent(this, OverviewActivity.class).addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);

            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

            IntentFilter[] intentFilters = new IntentFilter[]{};

            nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilters, null);
        }
    }

    private void disableForegroundDispatchSystem() {
        if (nfcAdapter != null && nfcAdapter.isEnabled()) {
            nfcAdapter.disableForegroundDispatch(this);
        }
    }

    private void initializeSlowMotionRatesList() {
        slowMotionRates.add("0.25");
        slowMotionRates.add("0.5");
        slowMotionRates.add("0.75");
        slowMotionRates.add("1");
    }

}
