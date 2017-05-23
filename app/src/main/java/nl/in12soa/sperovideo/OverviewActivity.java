package nl.in12soa.sperovideo;

import android.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import nl.in12soa.sperovideo.Models.Video;

public class OverviewActivity extends AppCompatActivity implements OverviewFragment.OnItemSelectedListener {

    private boolean hasOnePane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);

        hasOnePane = !getResources().getBoolean(R.bool.dual_pane);

        if (hasOnePane)
        {
            FragmentManager fm = getFragmentManager();
            if (fm.findFragmentByTag("list") == null)
            {
                fm.beginTransaction().add(R.id.framelayout, new OverviewFragment(), "list").commit();
            }
        }
    }

    @Override
    public void onItemSelected(Video video) {
        Toast.makeText(getApplicationContext(), "Sporter: " + video.sporter, Toast.LENGTH_LONG).show();
    }
}
