package gr16.android.heavensgps.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import gr16.android.heavensgps.R;
import gr16.android.heavensgps.application.HGPSApplication;

public class MainMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        Button btn = (Button) findViewById(R.id.btn_begintrack);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HGPSApplication.activityIntentSwitch(new MapsActivity(), MainMenuActivity.this);
            }
        });
    }


}
