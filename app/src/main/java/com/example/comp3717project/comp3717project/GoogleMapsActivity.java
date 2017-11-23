package com.example.comp3717project.comp3717project;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

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

import org.json.JSONObject;

import java.io.IOException;
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
    private ArrayList<ParkingPayStations> parkingStationArray;

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

        addParkingMeterLocations();
    }

    public void addParkingMeterLocations() {
        parkingStationArray = new ArrayList<ParkingPayStations>();
        parkingStationArray.add(new ParkingPayStations(49.20418453090815, -122.91340442716843));
        parkingStationArray.add(new ParkingPayStations(49.204503225410704, -122.91281265611559));
        parkingStationArray.add(new ParkingPayStations(49.20509807271738, -122.91171721680868));
        parkingStationArray.add(new ParkingPayStations(49.20423556721974, -122.9117484874497));
        parkingStationArray.add(new ParkingPayStations(49.20416647862545, -122.91131134034579));
        parkingStationArray.add(new ParkingPayStations(49.20422632852543, -122.91090184461366));
        parkingStationArray.add(new ParkingPayStations(49.2046369441439, -122.91045640704931));
        parkingStationArray.add(new ParkingPayStations(49.2029516117076, -122.913878308475));
        parkingStationArray.add(new ParkingPayStations(49.20233941773015, -122.91350467001372));
        parkingStationArray.add(new ParkingPayStations(49.201883691629554, -122.91310258521297));
        parkingStationArray.add(new ParkingPayStations(49.20116934356277, -122.91603671851404));
        parkingStationArray.add(new ParkingPayStations(49.20081791760789, -122.91513065856175));
        parkingStationArray.add(new ParkingPayStations(49.20286871325938, -122.91135581781107));
        parkingStationArray.add(new ParkingPayStations(49.20341286844059, -122.91033816837748));
        parkingStationArray.add(new ParkingPayStations(49.20391186560162, -122.90940215684569));
        parkingStationArray.add(new ParkingPayStations(49.20189305910532, -122.91213580849457));
        parkingStationArray.add(new ParkingPayStations(49.202418418071574, -122.91129563206509));
        parkingStationArray.add(new ParkingPayStations(49.20248553924138, -122.91108742930871));
        parkingStationArray.add(new ParkingPayStations(49.2020677907932, -122.91080462317406));
        parkingStationArray.add(new ParkingPayStations(49.20209454389161, -122.91052455880765));
        parkingStationArray.add(new ParkingPayStations(49.202093987366794, -122.91017724142871));
        parkingStationArray.add(new ParkingPayStations(49.20244583919352, -122.90945911827548));
        parkingStationArray.add(new ParkingPayStations(49.20379282809906, -122.90869160389644));
        parkingStationArray.add(new ParkingPayStations(49.20389877855257, -122.90855657863104));
        parkingStationArray.add(new ParkingPayStations(49.20384862620838, -122.90688017817034));
        parkingStationArray.add(new ParkingPayStations(49.204025985450784, -122.90653695151327));
        parkingStationArray.add(new ParkingPayStations(49.20522506548815, -122.90435531394584));
        parkingStationArray.add(new ParkingPayStations(49.20551700850255, -122.90381556350756));
        parkingStationArray.add(new ParkingPayStations(49.20531339367869, -122.90368603919241));
        parkingStationArray.add(new ParkingPayStations(49.20493109896177, -122.90438982152513));
    }

    public void addParkingMeterMarker(LatLng endLatLong) {
        Location parkingLocation = new Location("P1");
        Location endLocation = new Location("P2");
        float distance = 0;
        for (int i = 0; i < parkingStationArray.size(); i++) {
//          Location.distanceBetween(parkingStationArray.get(i).getLat(), parkingStationArray.get(i).getLon(), endLatLong.latitude, endLatLong.longitude, results);
            parkingLocation.setLatitude(parkingStationArray.get(i).getLat());
            parkingLocation.setLongitude(parkingStationArray.get(i).getLon());
            endLocation.setLatitude(endLatLong.latitude);
            endLocation.setLongitude(endLatLong.longitude);
            distance = parkingLocation.distanceTo(endLocation);
            Log.d("", "Location Distance is: " + distance);
            // location distance is set to be 150m radius
            if (distance <= 150) {
                gMap.addMarker(new MarkerOptions().position(new LatLng(parkingStationArray.get(i).getLat(), parkingStationArray.get(i).getLon())).title("Parking").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
            }
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
     */
    @Override
    public void onMapReady(GoogleMap map) {
        gMap = map;
        if (map == null) {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }
        if (map != null) {
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

        addParkingMeterMarker(endAddress);
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
//
//    private class AsyncParkingMeter extends AsyncTask<URL, Void, String> {
//        @Override
//        protected String doInBackground(URL... params) {
//            return HttpHelper.parseConnectionForString(params[0]);
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//            Route route = null;
//            try {
//                route = HttpHelper.parseJSONArrayForParkingMeterDetails(new JSONObject(result));
//            } catch (Exception e) {
//                Toast.makeText(GoogleMapsActivity.this, "something went wrong while parsing ur json", Toast.LENGTH_SHORT).show();
//            }
//            if (route == null) {
//                return;
//            }
//        }
//    }
}
