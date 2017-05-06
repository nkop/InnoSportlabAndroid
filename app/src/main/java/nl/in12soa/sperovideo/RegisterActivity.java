package nl.in12soa.sperovideo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

/**
 * Created by mickd on 4-5-2017.
 */

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }

    //User gets registered here.
    public void registerUser(View v){
        Toast.makeText(getApplicationContext(), "Register clicked", Toast.LENGTH_LONG).show();
    }

}
