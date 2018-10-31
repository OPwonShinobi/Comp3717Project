package com.example.comp3717project.comp3717project;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Alex on 21/11/2017.
 */

public class HttpHelper {
    public static String convertSpacesIntoURLFormat(String str) {
        String[] strArray;
        strArray = str.split(" ");
        if (strArray.length == 1) {
            return strArray[0];
        }
        String strURL = strArray[0];
        for (int i = 1; i < strArray.length; i++) {
            strURL += "%20" + strArray[i];
        }
        return strURL;
    }

    public static String parseConnectionForString(URL url) {
        HttpURLConnection connection = null; //https connections also work here, this is its parent class
        String result;

        try {
            connection = (HttpURLConnection) url.openConnection();
            result = parseInputStreamForString(connection.getInputStream());
        } catch (Exception e) {
            result = parseInputStreamForString(connection != null ? connection.getErrorStream() : null);
            e.printStackTrace();
        }
        return result;
    }

    private static String parseInputStreamForString(InputStream inputStream) {
        String result = "";
        String line;

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            line = bufferedReader.readLine();
            while (line != null) {
                result += line;
                line = bufferedReader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    //parking_station.json objects
    public static ArrayList parseJSONArrayForParkingPayStations(JSONArray jsonArray) throws JSONException {
        if (jsonArray != null) {
            ArrayList<ParkingLot> detailsAsObj = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); ++i) {
                JSONObject ParkingStation = jsonArray.getJSONObject(i);
                String name = ParkingStation.getJSONObject("properties").getString("STATIONID");
                JSONArray coordList = ParkingStation.getJSONObject("geometry").getJSONArray("coordinates");
                double lon = coordList.getDouble(0);
                double lat = coordList.getDouble(1);
                detailsAsObj.add(new ParkingLot(name, lat, lon));
            }
            return detailsAsObj;
        }
        return null;
    }

    //parking_meters.json objects
    public static ArrayList parseJSONArrayForParkingMeters(JSONArray jsonArray) throws JSONException {
        if (jsonArray != null) {
            ArrayList<ParkingLot> detailsAsObj = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); ++i) {
                JSONObject jsonObj = jsonArray.getJSONObject(i);
                String name =  jsonObj.getString("Sign_ID");
                JSONArray coordList = jsonObj.getJSONObject("json_geometry").getJSONArray("coordinates");
                double lon = coordList.getDouble(0);
                double lat = coordList.getDouble(1);
                detailsAsObj.add(new ParkingLot(name, lat, lon));
            }
            return detailsAsObj;
        }
        return null;
    }

    //parse a custom result json returned by google
    public static ArrayList parseJSONObjectForGoogleParkingLots(JSONObject jsonObj) throws JSONException {
        String authStatus = jsonObj.getString("status");
        if (authStatus.equals("OK")) {
            ArrayList<ParkingLot> detailsAsObj = new ArrayList<>();
            JSONArray resultsArray = jsonObj.getJSONArray("results");
            for (int i = 0; i < resultsArray.length(); ++i) {
                JSONObject parkingLotJson = resultsArray.getJSONObject(i);
                JSONObject latLngPair = parkingLotJson.getJSONObject("geometry").getJSONObject("location");
                double lat = latLngPair.getDouble("lat");
                double lon = latLngPair.getDouble("lng");
                String name = parkingLotJson.getString("name");
                detailsAsObj.add(new ParkingLot(name, lat, lon));
            }
            return detailsAsObj;
        }
        return null;
    }

    //navigate route response json like this
    // status
    // routes(list) ->unnamed(obj index 0)-> overview_polyline->points(string) < u want this string 1000char+ str
    // routes(list) ->unnamed(obj)-> legs(array)->unnamed(obj index 0) ->start_address(string)
    // routes(list) ->unnamed(obj)-> legs(array)->unnamed(obj index 0) ->end_address(string)
    public static Route parseJSONObjectForDirections(JSONObject jsonObj) throws JSONException {
        String authStatus = jsonObj.getString("status");
        if (authStatus.equals("OK")) {
            JSONArray routes = jsonObj.getJSONArray("routes");
            //String copyrights = routes.getJSONObject(0).getString("copyrights");
            //JSOBObject overviewPolyLineObj = routes.getJSONObject(0).getJSONObject("overview_polyline");
            String startAddress = routes.getJSONObject(0).getJSONArray("legs").getJSONObject(0).getString("start_address");
            String endAddress = routes.getJSONObject(0).getJSONArray("legs").getJSONObject(0).getString("end_address");
            String polylinePoints = routes.getJSONObject(0).getJSONObject("overview_polyline").getString("points");
            return new Route(startAddress, endAddress, polylinePoints);
        }
        // Google want's my credit card info or it caps me at 1 api call/day. For this? no way
        // since cannot display popup from here, return empty route and let GoogleMapsActivity handle that
        if (authStatus.equals("OVER_QUERY_LIMIT")) {
            return new Route(authStatus,"","");
        }
        return null;
    }


    public static ArrayList parseJSONArrayForShoppingMalls(JSONArray jsonArr) throws JSONException {
        if (jsonArr != null) {
            ArrayList<Mall> mallsList = new ArrayList<>();
            for (int i = 0; i < jsonArr.length(); ++i) {
                JSONObject mall = jsonArr.getJSONObject(i);
                String name = mall.getString("BLDGNAM");
                String lon = mall.getString("X");
                String lat = mall.getString("Y");
                mallsList.add(new Mall(name, lon, lat));
            }
            return mallsList;
        }
        return null;
    }

    // parsing json array for parks.json and return the ArrayList
    public static ArrayList parseJSONArrayForParkDetails(JSONArray jsonArray) throws JSONException {
        ArrayList<Park> parkList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject parkJsonObj = jsonArray.getJSONObject(i);
            JSONObject parkGeometry = parkJsonObj.getJSONObject("geometry");
            JSONObject parkProperties = parkJsonObj.getJSONObject("properties");
            Park park = new Park(
                    parkProperties.getString("Name"),
                    parkProperties.getString("StrName"), //this could be null
                    parkProperties.getString("StrNum"), //idk y u need this
                    parkProperties.getString("Category") //or this
            );
            //set border info (includes address)
            String shapeType = parkGeometry.getString("type");
            if (!shapeType.equals("Polygon")) //im too tired to do multipolygons
                continue;

            JSONArray coordList = parkGeometry.getJSONArray("coordinates").getJSONArray(0);
            ArrayList<LatLng> polygonCoords = new ArrayList<>();
            for (int j = 0; j < coordList.length(); j++) {
                JSONArray latLngPair = coordList.getJSONArray(j);
                double lon = latLngPair.getDouble(0);
                double lat = latLngPair.getDouble(1);
                polygonCoords.add(new LatLng(lat, lon));
            }
            String polylineStr = PolyUtil.encode(polygonCoords);
            park.setStrPolyline(polylineStr);

            parkList.add(park);
        }
        return parkList;
    }

}
