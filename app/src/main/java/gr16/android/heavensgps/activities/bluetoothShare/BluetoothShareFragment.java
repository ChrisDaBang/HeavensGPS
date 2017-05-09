package gr16.android.heavensgps.activities.bluetoothShare;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import gr16.android.heavensgps.R;
import gr16.android.heavensgps.activities.MainMenuActivity;
import gr16.android.heavensgps.activities.ReceivedLocationsMapsActivity;
import gr16.android.heavensgps.application.HGPSApplication;
import gr16.android.heavensgps.application.PointInTime;
import gr16.android.heavensgps.database.LocationDAO;

public class BluetoothShareFragment extends Fragment {

    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;

    private TextView labelSharedMsg;
    private TextView labelStatus;
    private Button btnShare;
    private Button btnFindDevice;
    private Button btnMakeDiscoverable;

    private String mConnectedDeviceName = null;
    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothShareService mShareService = null;

    public BluetoothShareFragment() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter == null) {
            FragmentActivity activity = getActivity();
            Toast.makeText(activity, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            activity.finish();
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        } else if (mShareService == null) {
            mShareService = new BluetoothShareService(getActivity(), mHandler);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mShareService != null) {
            mShareService.stop();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mShareService != null) {
            if (mShareService.getState() == BluetoothShareService.STATE_NONE) {
                mShareService.start();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bluetooth_share, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        labelSharedMsg = (TextView) view.findViewById(R.id.label_shared_text);
        labelStatus = (TextView) view.findViewById(R.id.label_status);

        btnShare = (Button) view.findViewById(R.id.btn_share);
        btnShare.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendLocation();
            }
        });
        btnFindDevice = (Button) view.findViewById(R.id.btn_find_device);
        btnFindDevice.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent serverIntent = new Intent(getActivity(), DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
            }
        });
        btnMakeDiscoverable = (Button) view.findViewById(R.id.btn_make_discoverable);
        btnMakeDiscoverable.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ensureDiscoverable();
            }
        });
    }

    private void ensureDiscoverable() {
        if (mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            startActivity(discoverableIntent);
        }
    }

    private void sendLocation() {
        if (mShareService.getState() != BluetoothShareService.STATE_CONNECTED) {
            Toast.makeText(getActivity(), "Not connected", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            LocationDAO db = new LocationDAO(HGPSApplication.getContext());
            List<PointInTime> locations = db.getAllLocations();

            if (!locations.isEmpty()) {
                ByteArrayOutputStream b = new ByteArrayOutputStream();
                ObjectOutputStream o = new ObjectOutputStream(b);
                for (PointInTime l : locations) {
                    o.writeObject(l);
                }
                mShareService.write(b.toByteArray());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            FragmentActivity activity = getActivity();
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothShareService.STATE_CONNECTED:
                            labelStatus.setText("Connected to " + mConnectedDeviceName);
                            break;
                        case BluetoothShareService.STATE_CONNECTING:
                            labelStatus.setText("Connecting");
                            break;
                        case BluetoothShareService.STATE_LISTEN:
                        case BluetoothShareService.STATE_NONE:
                            labelStatus.setText("Not connected");
                            break;
                    }
                    break;
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    ByteArrayInputStream b = new ByteArrayInputStream(readBuf);
                    try {
                        ObjectInputStream o = new ObjectInputStream(b);
                        List<PointInTime> locations = new ArrayList<>();
                        while (o.available() != 0) {
                            Object obj = o.readObject();
                            if (obj != null) {
                                locations.add((PointInTime) obj);
                            }
                        }
                        //We have recieved the data and will take you to a map showing it.
                        HGPSApplication.setLocations(locations);
                        HGPSApplication.activityIntentSwitch(new ReceivedLocationsMapsActivity(), new BluetoothShareActivity());

                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                    mConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
                    if (null != activity) {
                        Toast.makeText(activity, "Connected to "
                                + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    }
                    break;
                case Constants.MESSAGE_TOAST:
                    if (null != activity) {
                        Toast.makeText(activity, msg.getData().getString(Constants.TOAST),
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE:
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data);
                }
                break;
            case REQUEST_ENABLE_BT:
                if (resultCode == Activity.RESULT_OK) {
                    mShareService = new BluetoothShareService(getActivity(), mHandler);
                } else {
                    Toast.makeText(getActivity(), "Bluetooth is off, going back",
                            Toast.LENGTH_SHORT).show();
                    HGPSApplication.activityIntentSwitch(new MainMenuActivity(), new BluetoothShareActivity());
                }
        }
    }

    private void connectDevice(Intent data) {
        String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        mShareService.connect(device);
    }
}
