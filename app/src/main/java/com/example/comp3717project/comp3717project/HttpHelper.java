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

    //parking_meters.json objects
    public static ArrayList parseJSONArrayForParkingMeterDetails(JSONArray jsonArray) throws JSONException {
        ArrayList<String> detailsAsObj = new ArrayList<>();
        JSONObject jsonObj = jsonArray.getJSONObject(0);
        detailsAsObj.add(jsonObj.getString("json_featuretype"));
        //detailsAsObj.add( jsonObj.getString("Y") );
        //detailsAsObj.add( jsonObj.getString("X") );
        //detailsAsObj.add( jsonObj.getString("Sign_Definition") );
        detailsAsObj.add(String.valueOf(jsonObj.getJSONObject("json_geometry").getJSONArray("coordinates").getInt(0))); //lat double
        detailsAsObj.add(String.valueOf(jsonObj.getJSONObject("json_geometry").getJSONArray("coordinates").getInt(0))); //lon double
        return detailsAsObj;
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
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject parkJsonObj = jsonArray.getJSONObject(i);
                Park park = new Park(
                        parkJsonObj.getString("Name"),
                        parkJsonObj.getString("StrName"), //this could be null
                        parkJsonObj.getString("StrNum"), //idk y u need this
                        parkJsonObj.getString("Category") //or this
                );
                //set border info (includes address)
                JSONArray coordList = parkJsonObj.getJSONObject("json_geometry").getJSONArray("coordinates").getJSONArray(0);
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
        } catch (Exception e) {
            int i = 0;
        }
        int i = 0;
        return parkList;
    }

}
