package gr16.android.heavensgps.activities.bluetoothShare;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Set;

import gr16.android.heavensgps.R;

public class DeviceListActivity extends Activity {
    private static final String TAG = "DeviceListActivity";

    public static String EXTRA_DEVICE_ADDRESS = "device_address";
    private BluetoothAdapter mBtAdapter;
    private ArrayAdapter<String> mNewDevicesArrayAdapter;

    ArrayList<BluetoothDevice> pairedBluetoothDevices;
    ArrayList<BluetoothDevice> detectedBluetoothDevices;

    ListItemClickedDetected mDeviceClickListenerDetected;
    ListItemClickedPaired mDeviceClickListenerPaired;
    BluetoothDevice bdDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);

        Button btnScan = (Button) findViewById(R.id.btn_scan);
        btnScan.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                doDiscovery();
            }
        });

        ArrayAdapter<String> pairedDevicesArrayAdapter =
                new ArrayAdapter<String>(this, R.layout.device_name);
        mNewDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);

        mDeviceClickListenerDetected = new ListItemClickedDetected();
        mDeviceClickListenerPaired = new ListItemClickedPaired();

        ListView lvDetected = (ListView) findViewById(R.id.lv_detected);
        lvDetected.setAdapter(mNewDevicesArrayAdapter);
        lvDetected.setOnItemClickListener(mDeviceClickListenerDetected);

        ListView lvPaired = (ListView) findViewById(R.id.lv_paired);
        lvPaired.setAdapter(pairedDevicesArrayAdapter);
        lvPaired.setOnItemClickListener(mDeviceClickListenerPaired);

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);

        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);

        mBtAdapter = BluetoothAdapter.getDefaultAdapter();

        pairedBluetoothDevices = new ArrayList<>();
        detectedBluetoothDevices = new ArrayList<>();

        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();

        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                pairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                pairedBluetoothDevices.add(device);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Make sure we're not doing discovery anymore
        if (mBtAdapter != null) {
            mBtAdapter.cancelDiscovery();
        }

        // Unregister broadcast listeners
        this.unregisterReceiver(mReceiver);
    }

    private void doDiscovery() {
        Log.d(TAG, "doDiscovery()");

        // If we're already discovering, stop it
        if (mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
        }

        // Request discover from BluetoothAdapter
        mBtAdapter.startDiscovery();
    }

    class ListItemClickedDetected implements AdapterView.OnItemClickListener
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // Cancel discovery because it's costly and we're about to connect
            Log.d(TAG, "0");
            mBtAdapter.cancelDiscovery();
            Log.d(TAG, "1");
            bdDevice = detectedBluetoothDevices.get(position);

            // Create the result Intent and include the MAC address
            Intent intent = new Intent();
            intent.putExtra(EXTRA_DEVICE_ADDRESS, bdDevice.getAddress());
            Log.d(TAG, "3");
            // Set result and finish this Activity
            setResult(Activity.RESULT_OK, intent);
            Log.d(TAG, "4");
            finish();
            Log.d(TAG, "5");
        }
    }

    class ListItemClickedPaired implements AdapterView.OnItemClickListener
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // Cancel discovery because it's costly and we're about to connect
            mBtAdapter.cancelDiscovery();

            bdDevice = pairedBluetoothDevices.get(position);

            // Create the result Intent and include the MAC address
            Intent intent = new Intent();
            intent.putExtra(EXTRA_DEVICE_ADDRESS, bdDevice.getAddress());

            // Set result and finish this Activity
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // If it's already paired, skip it, because it's been listed already
                if (device.getBondState() != BluetoothDevice.BOND_BONDED && device.getName() != null) {
                    boolean flag = true;    // flag to indicate that particular device is already in the arlist or not
                    for(int i = 0; i<detectedBluetoothDevices.size();i++)
                    {
                        if(device.getAddress().equals(detectedBluetoothDevices.get(i).getAddress()))
                        {
                            flag = false;
                        }
                    }
                    if(flag == true)
                    {
                        mNewDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                        detectedBluetoothDevices.add(device);
                    }

                }
                // When discovery is finished, change the Activity title
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {

            }
        }
    };
}
