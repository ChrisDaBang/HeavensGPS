package gr16.android.heavensgps.activities;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import gr16.android.heavensgps.R;
import gr16.android.heavensgps.application.HGPSApplication;
import gr16.android.heavensgps.application.PointInTime;

public class ReceivedLocationsMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_received_locations_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map2);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        List<PointInTime> locationsReceived = HGPSApplication.getLocations();
        if (locationsReceived.isEmpty())
        {
            Toast.makeText(HGPSApplication.getContext(), "No locations received, the lying bastard haven't saved any locations in their device!", Toast.LENGTH_LONG);
        }
        else
        {
            for (PointInTime pit : locationsReceived)
            {
                LatLng position = new LatLng(pit.getLatitude(), pit.getLongitude());
                mMap.addMarker(new MarkerOptions().position(position).title(pit.getDate().toString()));
            }

            PointInTime firstPIT = locationsReceived.get(0);
            LatLng sydney = new LatLng(firstPIT.getLatitude(), firstPIT.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        }
    }

    @Override
    public void onBackPressed()
    {
        HGPSApplication.activityIntentSwitch(new MainMenuActivity(), this);
    }
}
