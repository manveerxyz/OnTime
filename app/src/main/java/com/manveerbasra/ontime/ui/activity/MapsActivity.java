package com.manveerbasra.ontime.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;

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
import com.manveerbasra.ontime.R;

/**
 * Activity to allow user to choose a location with map aid
 */
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private final String TAG = "MapsActivity";

    public static final String EXTRA_PLACE = "com.manveerbasra.ontime.MapsActivity.PLACE";
    public static final String BUNDLE_POINT = "com.manveerbasra.ontime.MapsActivity.BUNDLE.POINT";
    public static final String EXTRA_LATLNG = "com.manveerbasra.ontime.MapsActivity.LATLNG";

    private GoogleMap mMap;
    private Marker mMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        setPlaceSearchBarListener();
        setFABClickListener();

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
                if (mMarker != null) mMarker.remove(); // remove old marker

                // Add marker in selected place
                LatLng latLng = place.getLatLng();
                mMarker = mMap.addMarker(new MarkerOptions().position(latLng).title(place.getName().toString()));

                // Animate camera's movement to selected place
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(latLng)             // Sets the center of the map to place's coordinates
                        .zoom(14)                   // Sets the zoom
                        .build();                   // Creates a CameraPosition from the builder
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                Log.d(TAG, "Place selected: " + place.getName());
            }

            @Override
            public void onError(Status status) {
                Log.d(TAG, "An error occurred: " + status);
            }
        });
    }

    /**
     * Setup onClick Listener for FAB save button
     */
    public void setFABClickListener() {
        FloatingActionButton fab = findViewById(R.id.fab_loc_save);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mMarker != null) {
                    Intent replyIntent = new Intent();

                    Bundle args = new Bundle();
                    args.putParcelable(EXTRA_LATLNG, mMarker.getPosition());

                    // Add extras
                    replyIntent.putExtra(BUNDLE_POINT, args);
                    replyIntent.putExtra(EXTRA_PLACE, mMarker.getTitle());
                    setResult(RESULT_OK, replyIntent);
                    finish();
                } else {
                    Snackbar.make(findViewById(R.id.fab_loc_save), getString(R.string.loc_not_selected), Snackbar.LENGTH_SHORT).show();
                }
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
        mMap = googleMap;
    }

}
