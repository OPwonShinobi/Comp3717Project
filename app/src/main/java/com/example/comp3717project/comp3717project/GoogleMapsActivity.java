package com.example.comp3717project.comp3717project;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
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
    private MarkerOptions mCurrentAddressMarker; //used to isolate current location marker

    private final LatLng mDefaultLocation = new LatLng(49.205681, -122.911256); //google places new west name above this coord
    private static final int DEFAULT_ZOOM = 15;
    private static final int ENTRY_ZOOM = 13;
    private ArrayList<ParkingPayStations> parkingStationArray;


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
        String firstKeyName = myIntent.getStringExtra("DEST_ADDRESS_EXTRA"); //Passed intent variable

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
                onSearchForRoute(false);
            }
        });

        FloatingActionButton FAB = (FloatingActionButton) findViewById(R.id.locate_me_fab);
        FAB.setOnClickListener(new locateMeFABListener());
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this); //service used to locate phone location

        addParkingMeterLocations();

//        gMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
//            @Override
//            public void onMapClick(LatLng latLng) {
//                latLng.latitude;
//                latLng.longitude;
//            }
//        });
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

    public void addParkingMeterMarkers(LatLng endLatLong) {
        Location parkingLocation = new Location("P1");
        Location endLocation = new Location("P2");
        endLocation.setLatitude(endLatLong.latitude);
        endLocation.setLongitude(endLatLong.longitude);
        for (int i = 0; i < parkingStationArray.size(); i++) {
//          Location.distanceBetween(parkingStationArray.get(i).getLat(), parkingStationArray.get(i).getLon(), endLatLong.latitude, endLatLong.longitude, results);
            parkingLocation.setLatitude(parkingStationArray.get(i).getLat());
            parkingLocation.setLongitude(parkingStationArray.get(i).getLon());
            float distance = parkingLocation.distanceTo(endLocation);
            Log.d("", "Location Distance is: " + distance);
            // location distance is set to be 150m radius
            if (distance <= 150) {
                gMap.addMarker(new MarkerOptions().position(new LatLng(parkingStationArray.get(i).getLat(), parkingStationArray.get(i).getLon())).title("Parking").icon(
                        BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                );
            }
        }
    }

    public void onSearchForRoute(Boolean byMarker) {
        String srcLocation = start_et_address.getText().toString().trim();
        String destnLocation = end_et_address.getText().toString().trim();
        hideMyKeyboard();
        if (byMarker == true){
            start_et_address.setText("");
            end_et_address.setText("");
        }
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
            gMap.addMarker(new MarkerOptions().position(latLng).title(address.getAddressLine(0)).icon(
                    BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
            );

            gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
        } else {
            Toast t = Toast.makeText(this, "Please enter a destination.", Toast.LENGTH_LONG);
            t.show();
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        gMap = map;
        if (map == null) {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this); // this could result in an infinite loop if map is always null
        }
        if (map != null) {
            gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, ENTRY_ZOOM));
        }
        int intentActionID = getIntent().getIntExtra("SELECTED_ACTION_EXTRA", 0); //0: nothing, 1 address, 2 shop, 3 park
        switch (intentActionID) {
            case 1 : //entered address
                onSearchForDestination();
                break;
            case 2 : //get all shopping malls
                onSearchForShoppingMalls();
                break;
            case 3 : //get all parks
                onSearchForParks();
                break;
            case 4 : //exceptional, fab clicked goto own address\\
                gotoMyLocation();
                break;
        }
        gMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(final Marker marker) {
                LatLng pos = marker.getPosition();
                Log.d("", "latlong is : " + pos.latitude + " - " + pos.longitude);
                getRouteToMarker(pos);
                return true;
            }
        });
    }

    private void getRouteToMarker(LatLng latlng){
        if (checkLocationPermission()) {
            gMap.setMyLocationEnabled(true);
        }

//        double lat = gMap.getMyLocation().getLatitude();
//        double lng = gMap.getMyLocation().getLongitude();
//        Log.d("", "latlong is : " + lat + " - " + lng + " 2. " + latlng.latitude + "-" + latlng.longitude);
        end_et_address.setText(latlng.latitude + "," + latlng.longitude);
        start_et_address.setText("49.205681,-122.911256");
        onSearchForRoute(true);
    }

    public boolean checkLocationPermission() {
        String permission = "android.permission.ACCESS_FINE_LOCATION";
        int res = this.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    public void onSearchForShoppingMalls() {
        //400+ lines of unindented mess
        String url = "http://opendata.newwestcity.ca/downloads/major-shopping/MAJOR_SHOPPING.json";
        try {
            new AsyncShoppingMallDownloader().execute(new URL(url));
        } catch(IOException ioe) {
            String msg = url + "\nWarning, city URL badly formatted. Search aborted.";
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        }
    }

    public void onSearchForParks() {
        // park.json parsing
        String parkUrl = "http://opendata.newwestcity.ca/downloads/parks/PARKS.json";
        try {
            new AsyncJSONParser().execute(new URL(parkUrl));
        } catch (MalformedURLException e) {
            e.printStackTrace();
            String msg = parkUrl + "\nWarning, city URL badly formatted. Search aborted.";
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
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
        routeOptions.color(Color.BLUE);
        gMap.addPolyline(routeOptions);

        //add start marker
        String startAddressStr = route.getSrc(); //getIntent().getStringExtra("START_EXTRA");
        LatLng startAddress =  polylineCoordList.get(0);
        gMap.addMarker(new MarkerOptions().position(startAddress).title(startAddressStr).icon(
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE))
        );

        //add end marker
        String endAddressStr = route.getDestn(); //getIntent().getStringExtra("END_EXTRA");
        LatLng endAddress = polylineCoordList.get(polylineCoordList.size()-1);
        gMap.addMarker(new MarkerOptions().position(endAddress).title(endAddressStr).icon(
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
        );

        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startAddress, DEFAULT_ZOOM));

        addParkingMeterMarkers(endAddress);
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

                    gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, DEFAULT_ZOOM));
                    mCurrentAddressMarker = new MarkerOptions().position(myLatLng).title("You are here");
                    gMap.addMarker(mCurrentAddressMarker);
                } else {
                    Log.d("", "Current location is null. Using defaults.");
                    Log.e("", "Exception: %s", task.getException());
                    gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                }
            }
        });
    }

    private void markAllMallsOnMap(List<Mall> mallsList) {
            Location mallLocation = new Location("P1");
            Location referencePoint = new Location("P2");
            referencePoint.setLatitude(mDefaultLocation.latitude);
            referencePoint.setLongitude(mDefaultLocation.longitude);
        for (Mall mall : mallsList) {
                mallLocation.setLatitude(Double.parseDouble(mall.getLatitude()));
                mallLocation.setLongitude(Double.parseDouble(mall.getLongitude()));
                float distance = mallLocation.distanceTo(referencePoint);
                Log.d("", "Location Distance is: " + distance);
                // location distance is disabled for now
//                if (distance <= 150) {
                    gMap.addMarker(new MarkerOptions()
                            .position(new LatLng(mallLocation.getLatitude(), mallLocation.getLongitude()))
                            .title(mall.getName())
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                    );
//                }
            }
    }

    private void markAllParksOnMap() {
        if (mapParkList.size() != 0) {
            for (Park park : mapParkList) {
                String location = park.getStrNum() + " " + park.getStrName() + ", New Westminster, CA";
                List<Address> addressList = null;

                if(!location.equals(""))
                {
                    Geocoder geocoder = new Geocoder(this);
                    try {
                        addressList = geocoder.getFromLocationName(location, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Address address = addressList.get(0);
                    LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                    gMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .title(park.getParkName())
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                }
            }
        }
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
            drawRoute(route);
        }
    }

    private class AsyncShoppingMallDownloader extends AsyncTask<URL, Void, String> {
        @Override
        protected String doInBackground(URL... params) {
            return HttpHelper.parseConnectionForString(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            List<Mall> mallsList = null;
            try {
                mallsList = HttpHelper.parseJSONObjectForShoppingMalls(new JSONArray(result));
            } catch (Exception e) {
                Toast.makeText(GoogleMapsActivity.this, "something went wrong while parsing ur json", Toast.LENGTH_SHORT).show();
            }
            if (mallsList == null) {
                return;
            }
            markAllMallsOnMap(mallsList);
            //drawRoute(route);
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

    private class AsyncJSONParser extends AsyncTask<URL, Void, Void> {

        @Override
        protected Void doInBackground(URL... params) {
            String jsonStr = HttpHelper.parseConnectionForString(params[0]);
            try {
                JSONArray parkJsonArray = new JSONArray(jsonStr);
                mapParkList = HttpHelper.parseJSONArrayForParkDetails(parkJsonArray);
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(GoogleMapsActivity.this, "something went wrong while parsing ur json", Toast.LENGTH_SHORT).show();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            markAllParksOnMap();
            //drawRoute(route);
        }
    }
}
