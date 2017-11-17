package com.example.comp3717project.comp3717project;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;

//import com.google.android.gms.location.FusedLocationProviderClient;
//import com.google.android.gms.location.LocationServices;
//import com.google.android.gms.location.places.GeoDataClient;
//import com.google.android.gms.location.places.PlaceDetectionClient;
//import com.google.android.gms.location.places.PlaceLikelihood;
//import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
//import com.google.android.gms.location.places.Places;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

/**
 * This shows how to create a simple activity with a map and a marker on the map.
 */
public class GoogleMapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private MapView mapView;
    private GoogleMap gMap;
    EditText et_address;
//    private FusedLocationProviderClient mFusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_maps);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        GoogleMapOptions options = new GoogleMapOptions();
        options.mapType(GoogleMap.MAP_TYPE_SATELLITE)
                .compassEnabled(false)
                .rotateGesturesEnabled(false)
                .tiltGesturesEnabled(false);
        mapFragment.newInstance(options);

        Intent myIntent = getIntent(); // gets the previously created intent
        String firstKeyName = myIntent.getStringExtra("MyMessage"); //Passed intent variable

        et_address = (EditText) findViewById(R.id.map_address);
        et_address.setText(firstKeyName);

        Button searchBtn = (Button) findViewById(R.id.map_btn);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) { onSearch(); }
        });

        FloatingActionButton FAB = (FloatingActionButton)findViewById(R.id.locate_me_fab);
        FAB.setOnClickListener(new locateMeFABListener());
    }

    public void onSearch() {
        String location = et_address.getText().toString();
        List<Address> addressList = null;

        //moves keyboard away without clicking back
        View editText = this.getCurrentFocus();
        editText.clearAnimation();
        InputMethodManager imm = (InputMethodManager)getSystemService(this.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);

        //location will never be null unless et_address is not found roger!
        if(!location.equals(""))
        {
            Geocoder geocoder = new Geocoder(this);
            try {
                //y is it a list, we only ever use 1
                addressList = geocoder.getFromLocationName(location, 1);
            } catch (IOException e) {
                //e.printStackTrace(); this is useless in android
            }
            if (addressList.size() == 0) {
                Toast t = Toast.makeText(this, "No address found.\nPlease make sure address exists.", Toast.LENGTH_LONG);
                t.show();
                return;
            }
            Address address = addressList.get(0);
            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
            gMap.clear();
            gMap.addMarker(new MarkerOptions().position(latLng).title("Marker"));
            gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f));
            //gMap.animateCamera(CameraUpdateFactory.zoomTo(5.0f));
        } else {
            Toast t = Toast.makeText(this, "Please enter an address.", Toast.LENGTH_LONG);
            t.show();
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we
     * just add a marker near Africa.
     */
    @Override
    public void onMapReady(GoogleMap map) {
//        if (map == null) {
//            // Try to obtain the map from the SupportMapFragment.
//            SupportMapFragment mapFragment =
//                    (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
//            mapFragment.getMapAsync(this);
//
//            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
//                    == PackageManager.PERMISSION_GRANTED) {
//                map.setMyLocationEnabled(true);
//            } else {
//                Toast.makeText(GoogleMapsActivity.this, "You have to accept to enjoy all app's services!", Toast.LENGTH_LONG).show();
//                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
//                        == PackageManager.PERMISSION_GRANTED) {
//                    map.setMyLocationEnabled(true);
//                }
//            }
//
//            if (map != null) {
//
//
//                map.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
//
//                    @Override
//                    public void onMyLocationChange(Location arg0) {
//                        // TODO Auto-generated method stub
//
//                        CameraUpdate center= CameraUpdateFactory.newLatLng(new LatLng(arg0.getLatitude(), arg0.getLongitude()));
//                        CameraUpdate zoom= CameraUpdateFactory.zoomTo(12);
//
//                        map.moveCamera(center);
//                        map.animateCamera(zoom);
//                    }
//                });
//
//            }
//        }
        gMap = map;
        if (map == null)
        {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);

            if(map != null)
            {
                map.addMarker(new MarkerOptions().position(new LatLng(0,0)).title("Marker"));
                //map.setMyLocationEnabled(true);
            }
        }
    }

    private class locateMeFABListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            String msg = "Placeholder text.";
            Snackbar allahu = Snackbar.make(findViewById(R.id.background_linear_layout), msg, Snackbar.LENGTH_LONG);
            allahu.setAction("Undo", new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    Toast burnt = Toast.makeText(GoogleMapsActivity.this, "Undid.", Toast.LENGTH_LONG);
                    burnt.show();
                }
            });
            allahu.show();
        }
    }
}
