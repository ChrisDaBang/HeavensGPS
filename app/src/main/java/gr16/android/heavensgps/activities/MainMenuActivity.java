package gr16.android.heavensgps.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import gr16.android.heavensgps.R;
import gr16.android.heavensgps.activities.bluetoothShare.BluetoothShareActivity;
import gr16.android.heavensgps.application.HGPSApplication;

public class MainMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        Button btnBeginTrack = (Button) findViewById(R.id.btn_begin_track);
        btnBeginTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HGPSApplication.activityIntentSwitch(new MapsActivity(), MainMenuActivity.this);
            }
        });

        Button btnShareTrack = (Button) findViewById(R.id.btn_share_track);
        btnShareTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HGPSApplication.activityIntentSwitch(new BluetoothShareActivity(), MainMenuActivity.this);
            }
        });
    }


}
