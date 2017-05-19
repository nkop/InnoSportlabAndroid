package nl.in12soa.sperovideo;

import android.app.FragmentManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import nl.in12soa.sperovideo.Model.Video;

public class OverviewActivity extends AppCompatActivity implements OverviewFragment.OnItemSelectedListener {

    private boolean mHasOnePane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);

        mHasOnePane = !getResources().getBoolean(R.bool.dual_pane);

        if (mHasOnePane)
        {
            FragmentManager fm = getFragmentManager();
            if (fm.findFragmentByTag("list") == null)
            {
                fm.beginTransaction().add(R.id.container, new OverviewFragment(), "list").commit();
            }
        }
    }

    @Override
    public void onItemSelected(Video video) {
        Toast.makeText(getApplicationContext(), "Sporter: " + video.sporter, Toast.LENGTH_LONG).show();
    }
}
