package nl.in12soa.sperovideo;

import android.app.FragmentManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import nl.in12soa.sperovideo.Models.Video;
import nl.in12soa.sperovideo.Services.ActionBarService;

public class OverviewActivity extends AppCompatActivity implements OverviewFragment.OnItemSelectedListener {

    private boolean hasOnePane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);
        ActionBarService.setActionBarTitle(R.string.analyse, getSupportActionBar());

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
        Intent intent = new Intent(getApplicationContext(), VideoAnalyseActivity.class);
        intent.putExtra("filePath", video.filePath);
        intent.putExtra("id", video._id);
        startActivity(intent);
    }
}
