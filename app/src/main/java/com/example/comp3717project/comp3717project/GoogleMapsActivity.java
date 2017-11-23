package com.example.comp3717project.comp3717project;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * This shows how to create a simple activity with a map and a marker on the map.
 */
public class GoogleMapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private MapView mapView;
    private GoogleMap gMap;
    private EditText end_et_address;
    private EditText start_et_address;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLastKnownLocation; //will zoom to this on FAB click
    private final LatLng mDefaultLocation = new LatLng(49.205681, -122.911256); //google places new west name above this coord
    private static final int DEFAULT_ZOOM = 15;
    private static final int ENTRY_ZOOM = 13;

    // static Park list holding Park object
    private static ArrayList<Park> mapParkList = new ArrayList<>();

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

        end_et_address = (EditText) findViewById(R.id.destination_address_edit_text);
        end_et_address.setText(firstKeyName);
        start_et_address = (EditText) findViewById(R.id.starting_address_edit_text);

        Button searchBtn = (Button) findViewById(R.id.search_btn);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                onSearchForDestination();
            }
        });

        Button routeBtn = (Button) findViewById(R.id.route_btn);
        routeBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                onSearchForRoute();
            }
        });

        FloatingActionButton FAB = (FloatingActionButton) findViewById(R.id.locate_me_fab);
        FAB.setOnClickListener(new locateMeFABListener());
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this); //service used to locate phone location

        // park.json parsing
        String parkUrl = "http://opendata.newwestcity.ca/downloads/parks/PARKS.json";
        try {
            new AsyncJSONParser().execute(new URL(parkUrl));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public void onSearchForRoute() {
        String srcLocation = start_et_address.getText().toString().trim();
        String destnLocation = end_et_address.getText().toString().trim();
        hideMyKeyboard();
		if (!srcLocation.equals("") && !destnLocation.equals(""))
		{
            String api_key = getResources().getString(R.string.api_key); //overloaded google_maps_key
            //example: https://maps.googleapis.com/maps/api/directions/json?origin=Toronto&destination=Montreal&key=YOUR_API_KEY
			String url = "https://maps.googleapis.com/maps/api/directions/json?origin=%1$s&destination=%2$s&mode=%3$s&key=%4$s";
            srcLocation = HttpHelper.convertSpacesIntoURLFormat(srcLocation);
            destnLocation = HttpHelper.convertSpacesIntoURLFormat(destnLocation);
            String modeOfTransport = "driving"; //or walking, bicycling, transit(departure_time = now)
            url = String.format(url, srcLocation, destnLocation, modeOfTransport, api_key);
            try {
                new AsyncRouteDownloader().execute(new URL(url));
            } catch(IOException ioe) {
                String msg = url + "\nWarning, URL badly formatted. Search aborted.";
                Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
            }
		} else {
			//starting address empty, search for destination instead of a route
			onSearchForDestination();
		} 
    }

    private void hideMyKeyboard() {
        View editText = this.getCurrentFocus();
        editText.clearAnimation();
        InputMethodManager imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    public void onSearchForDestination() {
        String location = end_et_address.getText().toString();
        List<Address> addressList = null;

        //moves keyboard away without clicking back
        hideMyKeyboard();

        //location will never be null unless et_address is not found roger!
        if (!location.equals("")) {
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
            gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
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
        if (map == null) {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }
        if (map != null) {
            //why are we adding a marker off the coast of Africa???
            //map.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
            gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, ENTRY_ZOOM));
            //map.setMyLocationEnabled(true);
        }
    }

    //Note: DONT change the order of vars in here unless u know wat ur doing
    private void drawRoute(Route route) {
        gMap.clear();
        //add route as polyline
        String polylineStr = route.getPolyline(); //getIntent().getStringExtra("POLYLINE_EXTRA");
        List<LatLng> polylineCoordList = PolyUtil.decode(polylineStr);
        PolylineOptions routeOptions = new PolylineOptions();
        for (LatLng point : polylineCoordList) {
            routeOptions.add(point);
        }
        routeOptions.color(Color.GREEN);
        gMap.addPolyline(routeOptions);

        //add start marker
        String startAddressStr = route.getSrc(); //getIntent().getStringExtra("START_EXTRA");
        LatLng startAddress =  polylineCoordList.get(0);
        gMap.addMarker(new MarkerOptions().position(startAddress).title(startAddressStr).flat(false));

        //add end marker
        String endAddressStr = route.getDestn(); //getIntent().getStringExtra("END_EXTRA");
        LatLng endAddress = polylineCoordList.get(polylineCoordList.size()-1);
        gMap.addMarker(new MarkerOptions().position(endAddress).title(endAddressStr));

        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startAddress, DEFAULT_ZOOM));
    }

    private void gotoMyLocation() {
        if (ActivityCompat.checkSelfPermission(GoogleMapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(GoogleMapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
        {
            return;
        }
        Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
        locationResult.addOnCompleteListener(GoogleMapsActivity.this, new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful()) {
                    // Set the map's camera position to the current location of the device.
                    mLastKnownLocation = task.getResult();
                    LatLng myLatLng = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
                    gMap.clear();
                    gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, DEFAULT_ZOOM));
                    gMap.addMarker(new MarkerOptions().position(myLatLng).title("You are here"));
                } else {
                    Log.d("", "Current location is null. Using defaults.");
                    Log.e("", "Exception: %s", task.getException());
                    gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                }
            }
        });
    }

    private class locateMeFABListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            try {
                gotoMyLocation();
            } catch (SecurityException e)  {
                Log.e("Exception: %s", e.getMessage());
            }
        }
    }

    private class AsyncRouteDownloader extends AsyncTask<URL, Void, String> {
        @Override
        protected String doInBackground(URL... params) {
            return HttpHelper.parseConnectionForString(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            Route route = null;
            try {
                route = HttpHelper.parseJSONObjectForDirections(new JSONObject(result));
            } catch (Exception e) {
                Toast.makeText(GoogleMapsActivity.this, "something went wrong while parsing ur json", Toast.LENGTH_SHORT).show();
            }
            if (route == null) {
                return;
            }
            //Intent i = new Intent(GoogleMapsActivity.this, MainActivity.class);
//            Intent i = getIntent();
//            i.putExtra("START_EXTRA", routeDetails.getSrc());
//            i.putExtra("END_EXTRA", routeDetails.getDestn());
//            i.putExtra("POLYLINE_EXTRA", routeDetails.getPolyline());
            drawRoute(route);
        }
    }

    private class AsyncJSONParser extends AsyncTask<URL, Void, Void> {

        @Override
        protected Void doInBackground(URL... params) {
            String jsonStr = HttpHelper.parseConnectionForString(params[0]);
            try {
                JSONArray parkJsonArray = new JSONArray(jsonStr);
                mapParkList = HttpHelper.parseJSONArrayForParkDetails(parkJsonArray);
                int count = mapParkList.size();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}
