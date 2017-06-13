package nl.in12soa.sperovideo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class OverviewMenuFragment extends Fragment {

    private Button switchListButton;
    private String btn_text = "";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.overview_menu_fragment, container, false);

        Button logoutButton = (Button) view.findViewById(R.id.logout_button);
        final Button remoteButton = (Button) view.findViewById(R.id.remote_button_analyse);
        switchListButton = (Button) view.findViewById(R.id.switch_list_button);
        final OverviewActivity overviewActivity = (OverviewActivity)getActivity();
        if(OverviewActivity.ONLINE)
            switchListButton.setText("Offline video's");
        else
            switchListButton.setText("Online video's");
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });

        remoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity().getApplicationContext(), RemoteActivity.class);
                startActivity(intent);
            }
        });

        switchListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //if(switchListButton.getText().equals("Online video's"))
                if(!overviewActivity.ONLINE)
                {
                    overviewActivity.setOnline(true);
                    switchListButton.setText("Offline video's");
                }
                else {
                    overviewActivity.setOnline(false);
                    switchListButton.setText("Online video's");
                }

            }
        });

        return view;
    }

    private void logout()
    {
        SharedPreferences settings = getActivity().getApplicationContext().getSharedPreferences("SPEROVIDEO", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.remove("id");
        editor.remove("email");
        editor.remove("rfid");
        editor.apply();

        Intent intent = new Intent(getActivity().getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }
}
