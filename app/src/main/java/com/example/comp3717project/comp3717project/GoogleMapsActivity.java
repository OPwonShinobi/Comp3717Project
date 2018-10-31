package com.example.comp3717project.comp3717project;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
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
import com.google.android.gms.maps.model.PolygonOptions;
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

    private SQLiteDatabase db;

    private MapView mapView;
    private GoogleMap gMap;
    private EditText end_et_address;
    private EditText start_et_address;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private MarkerOptions mCurrentAddressMarker; //used to isolate current location marker

    private final LatLng mDefaultLocation = new LatLng(49.205681, -122.911256); //google places new west name above this coord
    private static final int DEFAULT_ZOOM = 17;
    private static final int ENTRY_ZOOM = 13;
    private final int DEFAULT_PATH_WIDTH = 5;
    private ArrayList<ParkingLot> parkingPayStationList;
    private ArrayList<ParkingLot> parkingMetersList;
    private ProgressDialog pDialog;

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

        Intent myIntent = getIntent();
        String firstKeyName = myIntent.getStringExtra("DEST_ADDRESS_EXTRA");

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

        //get park paystations on start up, and add it to a global
        String parkingPayStationURL = "http://opendata.newwestcity.ca/downloads/parking-pay-stations/PARKING_PAY_STATIONS.json";
        String parkingMeterURL = "http://opendata.newwestcity.ca/downloads/parking-meters/PARKING_METERS.json";
        try {
            new AsyncParkingLotDownloader().execute(new URL(parkingPayStationURL), new URL(parkingMeterURL));
        } catch (MalformedURLException e) {
            e.printStackTrace();
            String msg = parkingPayStationURL + "\nWarning, city URL badly formatted. Search aborted.";
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
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
        gMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener(){
            @Override
            public void onInfoWindowClick(Marker marker) {
                showMarkerOptionsDialog(marker);
            }
        });
    }

    private void showMarkerOptionsDialog(final Marker marker) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_edit_map_marker, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle("Address options");
        dialogBuilder.setMessage("Address: " + marker.getTitle());

        final double lat = marker.getPosition().latitude;
        final double lon = marker.getPosition().longitude;
        final String mTitle = marker.getTitle();

        final CheckBox favCheckBox = dialogView.findViewById(R.id.favCheckBox);
        final EditText favNameEdit = dialogView.findViewById(R.id.favNameText);

        // check if marker is already in favorite list
        favCheckBox.setChecked(false);
        favNameEdit.setVisibility(View.INVISIBLE);
        if (MainActivity.favoriteList.containsKey(mTitle)) {
            favCheckBox.setChecked(true);
            favNameEdit.setVisibility(View.VISIBLE);
            favNameEdit.setText(MainActivity.favoriteList.get(mTitle).getName());
        }

        favCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    favNameEdit.setVisibility(View.VISIBLE);
                    favNameEdit.setText(mTitle);
                } else {
                    favNameEdit.setVisibility(View.INVISIBLE);
                    favNameEdit.setText("");
                }
            }
        });

        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                RadioGroup startOrDestn = dialogView.findViewById(R.id.start_destn_radio_group);
                int checkedID = startOrDestn.getCheckedRadioButtonId(); //returns -1 if unchecked
                String addressString = "";
                switch (checkedID) {
                    case -1 : break; //no radio button checked
                    case R.id.set_destn_btn:
                        addressString = getAddress(lat, lon);
                        end_et_address.setText(addressString);
                        break;
                    case R.id.set_start_btn:
                        addressString = getAddress(lat, lon);
                        start_et_address.setText(addressString);
                        break;
                }

                String favName = favNameEdit.getText().toString();
                FavoritePlace favPlace = new FavoritePlace(favName, mTitle, lat, lon);

                if (favCheckBox.isChecked()) {
                    if (favName.equals("")) {
                        Toast.makeText(GoogleMapsActivity.this,
                                "Name of place cannot be empty", Toast.LENGTH_LONG).show();
                    } else {
                        // remove first if exist
                        // add place to favorite table on database
                        MapDBHelper dbHelper = MapDBHelper.getInstance(GoogleMapsActivity.this);
                        db = dbHelper.getWritableDatabase();

                        ContentValues values = new ContentValues();
                        values.put(MapDBHelper.FavoriteTable.NAME, favName);
                        values.put(MapDBHelper.FavoriteTable.MARKERTITLE, marker.getTitle());
                        values.put(MapDBHelper.FavoriteTable.LATITUDE, lat);
                        values.put(MapDBHelper.FavoriteTable.LONGITUDE, lon);

                        db.insertWithOnConflict(MapDBHelper.FavoriteTable.TABLE_NAME,
                                null,
                                values,
                                SQLiteDatabase.CONFLICT_REPLACE);
                        MainActivity.favoriteList.put(mTitle, favPlace);
                        db.close();
                    }
                } else {
                    if (MainActivity.favoriteList.containsKey(mTitle)) {
                        removeFromFavoriteListDB();
                        MainActivity.favoriteList.remove(mTitle);
                    }
                }
            }

            // remove selected marker from favorite place database
            private void removeFromFavoriteListDB() {
                MapDBHelper dbHelper = MapDBHelper.getInstance(GoogleMapsActivity.this);
                db = dbHelper.getWritableDatabase();
                String whereClause = MapDBHelper.FavoriteTable.MARKERTITLE + "=?";
                String[] whereArgs = new String[] {mTitle};
                db.delete(MapDBHelper.FavoriteTable.TABLE_NAME, whereClause, whereArgs);
                db.close();
            }
        });

        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {/*do nothing*/ }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    private void addParkingLotMarkers(LatLng endLatLong) {
        Location parkingLocation = new Location("P1");
        Location endLocation = new Location("P2");
        endLocation.setLatitude(endLatLong.latitude);
        endLocation.setLongitude(endLatLong.longitude);
        for (int i = 0; i < parkingPayStationList.size(); i++) {
            ParkingLot parkinglot = parkingPayStationList.get(i);
            parkingLocation.setLatitude(parkinglot.getLat());
            parkingLocation.setLongitude(parkinglot.getLon());
            float distance = parkingLocation.distanceTo(endLocation);
            Log.d("", "Location Distance is: " + distance);
            // location distance is set to be 150m radius
            if (distance <= 200) {
                gMap.addMarker(new MarkerOptions().position(new LatLng(parkingPayStationList.get(i).getLat(), parkingPayStationList.get(i).getLon()))
                	.title("Pay station\nparking. ID:" + parkinglot.getName())
                	.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                );
            }
        }
        onSearchForGoogleParkingLots(endLatLong);
    }

    private void onSearchForGoogleParkingLots(LatLng latLng) {
        // eg https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=-33.8670522,151.1957362&radius=500&type=restaurant&keyword=cruise&key
        String queryUrl = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=%1$s&radius=%2$s&type=parking&key=%3$s";
        String latLngStr = latLng.latitude + "," + latLng.longitude ;
        String radiusStr = "500"; //this is in metres
        String apiKey = getResources().getString(R.string.api_key);
        queryUrl = String.format(queryUrl, latLngStr, radiusStr, apiKey);
        try {
            new AsyncGoogleParkingLotDownloader().execute(new URL(queryUrl));
        } catch (MalformedURLException e) {
            e.printStackTrace();
            String msg = queryUrl + "\nWarning, parking lot query URL badly formatted. Search aborted.";
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        }
    }

	private void addGoogleParkingLotMarkers(ArrayList<ParkingLot> resultsList) {
		for (ParkingLot parkingLot : resultsList) {
			gMap.addMarker(new MarkerOptions().position(new LatLng(parkingLot.getLat(), parkingLot.getLon()))
				.title(parkingLot.getName())
				.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
			);
		}
 	}
 	
    private String getAddress(double latitude, double longitude) {
        if (latitude != 0 && longitude != 0) {
            try {
                Geocoder geocoder = new Geocoder(this);
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                String address = addresses.get(0).getAddressLine(0);
                //String city = addresses.get(0).getAddressLine(1);
                //String country = addresses.get(0).getAddressLine(2);
                //note, the first line includes the PO box, street address, city, country
                //2nd & 3rd address lines are all null
                return address; // + "," + city + "," + country;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "latitude and longitude are null", Toast.LENGTH_LONG).show();
        }
        return "";
    }

    public void onSearchForRoute() {
        String srcLocation = start_et_address.getText().toString().trim();
        String destnLocation = end_et_address.getText().toString().trim();
        hideMyKeyboard();

		if (!srcLocation.equals("") && !destnLocation.equals(""))
		{
            String api_key = getResources().getString(R.string.api_key); //overloaded google_maps_key
			String url = "https://maps.googleapis.com/maps/api/directions/json?origin=%1$s&destination=%2$s&mode=%3$s&key=%4$s";
            srcLocation = HttpHelper.convertSpacesIntoURLFormat(srcLocation);
            destnLocation = HttpHelper.convertSpacesIntoURLFormat(destnLocation);
            String modeOfTransport = "driving"; //or walking, bicycling, transit(departure_time = now), but left out bc should use parking
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
        String destnLocation = end_et_address.getText().toString().trim();
        hideMyKeyboard();
        if (!destnLocation.equals("")) {
            String api_key = getResources().getString(R.string.api_key); //overloaded google_maps_key
			String url = "https://maps.googleapis.com/maps/api/directions/json?origin=%1$s&destination=%2$s&key=%3$s";
            destnLocation = HttpHelper.convertSpacesIntoURLFormat(destnLocation);
            url = String.format(url, destnLocation, destnLocation, api_key);
            try {
                new AsyncRouteDownloader().execute(new URL(url));
            } catch(IOException ioe) {
                String msg = url + "\nWarning, URL badly formatted. Search for destination aborted.";
                Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
            }
        } else {
            Toast t = Toast.makeText(this, "Please enter a destination.", Toast.LENGTH_LONG);
            t.show();
        }
    }

    public boolean checkLocationPermission() {
        String permission1 = "android.permission.ACCESS_FINE_LOCATION";
        String permission2 = "android.permission.ACCESS_COARSE_LOCATION";
        int granted1 = this.checkCallingOrSelfPermission(permission1);
        int granted2 = this.checkCallingOrSelfPermission(permission2);
        return (granted1== PackageManager.PERMISSION_GRANTED && granted2 == PackageManager.PERMISSION_GRANTED);
    }

    public void onSearchForShoppingMalls() {
        //400+ lines of unindented mess
        String url = "http://opendata.newwestcity.ca/downloads/major-shopping/MAJOR_SHOPPING.json";
        try {
            new AsyncShoppingMallDownloader().execute(new URL(url));
        } catch(IOException ioe) {
            String msg = url + "\nWarning, city URL badly formatted. Search for route aborted.";
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        }
    }

    public void onSearchForParks() {
        // park.json parsing
        String parkUrl = "http://opendata.newwestcity.ca/downloads/parks/PARKS.json";
        try {
            new AsyncParkDownloader().execute(new URL(parkUrl));
        } catch (MalformedURLException e) {
            e.printStackTrace();
            String msg = parkUrl + "\nWarning, city URL badly formatted. Search aborted.";
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        }
    }

    //Note: DONT change the order of vars in here unless u know wat ur doing
    private void drawRoute(Route route) {
        gMap.clear();
        String polylineStr = route.getPolyline(); //getIntent().getStringExtra("POLYLINE_EXTRA");
        List<LatLng> polylineCoordList = PolyUtil.decode(polylineStr);
		LatLng startAddress = null;
		boolean routeForSingleAddress = false;

		String startAddressStr = route.getSrc();
		String endAddressStr = route.getDestn(); 
        //don't draw route polyline & start marker if same start & end
        if (!startAddressStr.equals(endAddressStr)) {
	        //add route as polyline
	        PolylineOptions routeOptions = new PolylineOptions();
	        for (LatLng point : polylineCoordList) {
	            routeOptions.add(point);
	        }
	        routeOptions.width(DEFAULT_PATH_WIDTH);
	        routeOptions.color(Color.BLUE);
	        gMap.addPolyline(routeOptions);

	        //add start marker
	        startAddress =  polylineCoordList.get(0);
	        gMap.addMarker(new MarkerOptions().position(startAddress).title(startAddressStr).icon(
	                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE))
	        );
	    } else {
	    	routeForSingleAddress = true;
	    }

        //add end marker
        LatLng endAddress = polylineCoordList.get(polylineCoordList.size()-1);
        gMap.addMarker(new MarkerOptions().position(endAddress).title(endAddressStr).icon(
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
        );
        if (routeForSingleAddress) {
        	startAddress = endAddress;
        }

        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startAddress, DEFAULT_ZOOM));

        addParkingLotMarkers(endAddress);
    }

    private void gotoMyLocation() {
    	boolean myLocationPermissionsGranted = checkLocationPermission();
        if (!myLocationPermissionsGranted) {
            return;
        }
        Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
        locationResult.addOnCompleteListener(GoogleMapsActivity.this, new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful()) {
                    Location lastKnownLocation = task.getResult();
                    LatLng myLatLng = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());

                    gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, DEFAULT_ZOOM));
                    //note: overwriting this will overwriting it on the map, updating just the 1 marker
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

        for (Mall mall : mallsList) {
            mallLocation.setLatitude(Double.parseDouble(mall.getLatitude()));
            mallLocation.setLongitude(Double.parseDouble(mall.getLongitude()));

           gMap.addMarker(new MarkerOptions()
                .position(new LatLng(mallLocation.getLatitude(), mallLocation.getLongitude()))
                .title(mall.getName())
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
            );
        }
    }

    private void markAllParksOnMap(ArrayList<Park> mapParkList) {
        for (Park park : mapParkList) {
            String parkName = park.getParkName();
            String polylineStr = park.getStrPolyline();
            List<LatLng> polylineCoords = PolyUtil.decode(polylineStr);

            LatLng tentativeAddress = polylineCoords.get(0);
            gMap.addMarker(new MarkerOptions()
                .position(tentativeAddress)
                .title(parkName)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
            );

            PolygonOptions parkBorderOptions = new PolygonOptions();
            for (LatLng coord : polylineCoords) {
                parkBorderOptions.add(coord);
            }
            parkBorderOptions.strokeWidth(DEFAULT_PATH_WIDTH);
            parkBorderOptions.strokeColor(Color.rgb(0, 153, 51)); //med green
            parkBorderOptions.fillColor(Color.argb(75, 153, 255, 102)); //light green
            gMap.addPolygon(parkBorderOptions);
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
            // added because google wants me to give them payment info WoW-style to use their free API
            // !null route has empty destn, the src contains a msg from google
            if (route.getDestn().equals("")) {
                Toast.makeText(GoogleMapsActivity.this, "A message from google: " + route.getSrc(), Toast.LENGTH_LONG).show();
                return;
            }
            drawRoute(route);
        }
    }

    private class AsyncShoppingMallDownloader extends AsyncTask<URL, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(GoogleMapsActivity.this);
            pDialog.setMessage("Please wait just a moment...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(URL... params) {
            return HttpHelper.parseConnectionForString(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            List<Mall> mallsList = null;
            try {
                mallsList = HttpHelper.parseJSONArrayForShoppingMalls(new JSONArray(result));
            } catch (Exception e) {
                Toast.makeText(GoogleMapsActivity.this, "something went wrong while parsing ur malls json", Toast.LENGTH_SHORT).show();
            }
            if (mallsList == null) {
                return;
            }

            if (pDialog.isShowing())
                pDialog.dismiss();
            markAllMallsOnMap(mallsList);
        }
    }

    private class AsyncParkingLotDownloader extends AsyncTask<URL, Void, Void> {
        private ArrayList<ParkingLot> mapParkingStations;

        @Override
        protected Void doInBackground(URL... params) {
            String stationsStr = HttpHelper.parseConnectionForString(params[0]);
            // String metersStr = HttpHelper.parseConnectionForString(params[1]);
            try {
                JSONObject unproccessedJson = new JSONObject(stationsStr);
                JSONArray stationsJsonArray = unproccessedJson.getJSONArray("features");
                mapParkingStations = HttpHelper.parseJSONArrayForParkingPayStations(stationsJsonArray);
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("WARNING", "parking station bullsht");
                //Toast.makeText(GoogleMapsActivity.this, "something went wrong while parsing ur parking json", Toast.LENGTH_SHORT).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            parkingPayStationList = mapParkingStations;
        }
    }

	private class AsyncGoogleParkingLotDownloader extends AsyncTask<URL, Void, String> {
	   @Override
	   protected String doInBackground(URL... params) {
	       return HttpHelper.parseConnectionForString(params[0]);
	   }

	   @Override
	   protected void onPostExecute(String result) {
	       ArrayList<ParkingLot> resultsList = null;
	       try {
	           resultsList = HttpHelper.parseJSONObjectForGoogleParkingLots(new JSONObject(result));
	       } catch (Exception e) {
	           Toast.makeText(GoogleMapsActivity.this, "something went wrong while parsing ur google parking meter json", Toast.LENGTH_SHORT).show();
	       }
	       if (resultsList == null) {
	           return;
	       }
	       addGoogleParkingLotMarkers(resultsList);
	   }
	}
   
    private class AsyncParkDownloader extends AsyncTask<URL, Void, Void> {
        private ArrayList<Park> mapParkList;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(GoogleMapsActivity.this);
            pDialog.setMessage("Please wait just a moment...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(URL... params) {
            String jsonStr = HttpHelper.parseConnectionForString(params[0]);
            try {
                JSONObject unproccessedJson = new JSONObject(jsonStr);
                JSONArray parkJsonArray = unproccessedJson.getJSONArray("features");
                mapParkList = HttpHelper.parseJSONArrayForParkDetails(parkJsonArray);
                int i = 0;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            markAllParksOnMap(mapParkList);
        }
    }
}
