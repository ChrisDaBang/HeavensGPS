package gr16.android.heavensgps.activities;

import android.Manifest;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.function.Consumer;

import gr16.android.heavensgps.R;
import gr16.android.heavensgps.application.HGPSApplication;
import gr16.android.heavensgps.application.LatLngCallback;
import gr16.android.heavensgps.database.LocationDAO;

// See this link for location tutorial. -> http://blog.teamtreehouse.com/beginners-guide-location-android
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera

        if (Build.VERSION.SDK_INT >= 23)
        {
            checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        Button btn = (Button) findViewById(R.id.btn_maps_saveLocation);
        final FragmentActivity self = this;

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCurrentPosition(new LatLngCallback() {
                    @Override
                    public void call(LatLng latLng) {
                        LocationDAO db = new LocationDAO(HGPSApplication.getContext());
                        db.saveLocation(latLng.latitude, latLng.longitude);
                        Toast.makeText(self, "Location saved", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        mMap.setMyLocationEnabled(true);
        setCurrentPosition(new LatLngCallback() {
            @Override
            public void call(LatLng location) {
                mMap.addMarker(new MarkerOptions().position(location).title("You are here"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 13));
            }
        });
    }

    private void setCurrentPosition(final LatLngCallback callback)
    {
        try {
            LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
            Criteria c = new Criteria();
            c.setAccuracy(Criteria.ACCURACY_FINE);

            lm.requestSingleUpdate(c, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    LatLng currentPosition = new LatLng(location.getLatitude(), location.getLongitude());

                    if (callback != null)
                    {
                        callback.call(currentPosition);
                    }
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            }, null);
        }
        catch (SecurityException e)
        {
            Toast.makeText(this, "GPS is not available", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed()
    {
        HGPSApplication.activityIntentSwitch(new MainMenuActivity(), this);
    }
}
