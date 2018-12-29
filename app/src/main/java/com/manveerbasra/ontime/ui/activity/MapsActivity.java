package com.manveerbasra.ontime;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Activity to allow user to choose a location with map aid
 */
public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private final String TAG = "MapsActivity";

    public static final String EXTRA_PLACE = "com.manveerbasra.ontime.PLACE";

    private GoogleMap map;
    private Marker marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        setPlaceSearchBarListener();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Setup onPlaceSelected Listener for the Place Search Bar
     */
    private void setPlaceSearchBarListener() {
        PlaceAutocompleteFragment placeAutoComplete = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete);
        placeAutoComplete.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                if (marker != null) marker.remove(); // remove old marker

                // Add marker in selected place
                LatLng latLng = place.getLatLng();
                marker = map.addMarker(new MarkerOptions().position(latLng).title(place.getName().toString()));

                // Animate camera's movement to selected place
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(latLng)             // Sets the center of the map to place's coordinates
                        .zoom(14)                   // Sets the zoom
                        .build();                   // Creates a CameraPosition from the builder
                map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                Log.d(TAG, "Place selected: " + place.getName());
            }

            @Override
            public void onError(Status status) {
                Log.d(TAG, "An error occurred: " + status);
            }
        });
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_alarm, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_alarm_save) { // save button clicked
            Intent replyIntent = new Intent();

            // Get chosen marker's position
            LatLng position = marker.getPosition();

//            // Get marker's address
//            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
//            List<Address> addressList;
//            try {
//                addressList = geocoder.getFromLocation(position.latitude, position.longitude, 1);
//            } catch (IOException e) {
//                e.printStackTrace();
//                addressList = null;
//            }
//
//            // Get user-readable representation of address
//            String placeName = "";
//            if (addressList != null) {
//                placeName = addressList.get(0).getSubThoroughfare() + " "
//                        + addressList.get(0).getThoroughfare();
//            }
//
//            if (placeName.equals("") || placeName.equals("null null")) {
//                placeName = marker.getTitle();
//            }
//            placeName = placeName.replace("null", "").trim();

            // Add place as extra
            replyIntent.putExtra(EXTRA_PLACE, marker.getTitle());
            setResult(RESULT_OK, replyIntent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
