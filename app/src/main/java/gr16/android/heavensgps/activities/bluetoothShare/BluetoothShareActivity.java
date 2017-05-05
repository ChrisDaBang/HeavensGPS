package gr16.android.heavensgps.activities.bluetoothShare;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import gr16.android.heavensgps.R;

public class BluetoothShareActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_share);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        BluetoothShareFragment fragment = new BluetoothShareFragment();
        transaction.replace(R.id.content_fragment, fragment);
        transaction.commit();
    }
}
