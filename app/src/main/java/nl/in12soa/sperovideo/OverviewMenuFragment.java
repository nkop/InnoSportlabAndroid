package nl.in12soa.sperovideo;


import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class OverviewMenuFragment extends Fragment {

    private Button switchListButton;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.overview_menu_fragment, container, false);

        Button logoutButton = (Button) view.findViewById(R.id.logout_button);
        Button remoteButton = (Button) view.findViewById(R.id.remote_button_analyse);
        switchListButton = (Button) view.findViewById(R.id.switch_list_button);

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

                if(switchListButton.getText().equals("Online video's"))
                {
                    OverviewOnlineFragment onlineFragment = new OverviewOnlineFragment();
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();

                    transaction.replace(R.id.overview_fragment, onlineFragment);
                    transaction.addToBackStack(null);

                    transaction.commit();

                    switchListButton.setText("Lokale video's");
                }
                else{
                    OverviewFragment localFragment = new OverviewFragment();
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();

                    transaction.replace(R.id.overview_fragment, localFragment);
                    transaction.addToBackStack(null);

                    transaction.commit();

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
