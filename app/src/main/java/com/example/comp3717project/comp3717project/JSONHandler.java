package com.example.comp3717project.comp3717project;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class JSONHandler extends AsyncTask<Void, Void, Void> {

    private static final String TAG = JSONHandler.class.getSimpleName();
    private List<Park> parkList;

    private AssetManager manager;
    private InputStream is;
    private byte[] buffer;
    private String filename;
    private MainActivity.JsonTypeTag typeTag;

    public JSONHandler(String filename, Context context, MainActivity.JsonTypeTag typeTag) {
        manager = context.getAssets();
        is = null;
        this.filename = filename;
        parkList = new ArrayList<>();
        this.typeTag = typeTag;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        try {
            is = manager.open(filename);
            int size = is.available();
            buffer = new byte[size];
            is.read(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Void doInBackground(Void... param) {
        String jsonStr;
        try {
            jsonStr = new String(buffer, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            jsonStr = null;
        }

        Log.e(TAG, "Response from json file: " + jsonStr);

        if (jsonStr != null) {
            switch (typeTag) {
                case PARKING:
                    // process parking JSON file
                    break;
                case PARKS:
                    // process parks JSON file
                    processParkJSON(jsonStr);
                    break;
                case SHOPPING:
                    // process shopping JSON file
                    break;
                case ADDRESS:
                    // process address
                    break;
            }

        } else {
            Log.e(TAG, "Couldn't get json from server.");
        }

        return null;
    }

    private void processParkJSON(String jsonStr) {
        try {
            // Getting JSON Array node
            JSONArray parkJsonArray = new JSONArray(jsonStr);

            // looping through All Contacts
            for (int i = 0; i < parkJsonArray.length(); i++) {
                JSONObject parkJsonObj = parkJsonArray.getJSONObject(i);

                String parkName = new String();
                String strName = new String();
                String strNum = new String();
                String category = new String();
                JSONObject geometryJSON;
                JSONArray coordJSONArray;
                List<List<Double>> coordList = new ArrayList<>();

                if (!parkJsonObj.isNull("Name")) {
                    parkName = parkJsonObj.getString("Name");
                }
                if (!parkJsonObj.isNull("StrName")) {
                    strName = parkJsonObj.getString("StrName");
                }
                if (!parkJsonObj.isNull("StrNum")) {
                    strNum = parkJsonObj.getString("StrNum");
                }
                if (!parkJsonObj.isNull("Category")) {
                    category = parkJsonObj.getString("Category");
                }

                Park park = new Park(parkName, strName, strNum, category, coordList);
                parkList.add(park);
            }
        } catch (final JSONException e) {
            Log.e(TAG, "Json parsing error: " + e.getMessage());
        }
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        try {
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*
        // Dismiss the progress dialog
        if (pDialog.isShowing())
            pDialog.dismiss();

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(CountryListActivity.this
                , android.R.layout.simple_list_item_1, countryNameList);
        lvCountries.setAdapter(arrayAdapter); // Attach the adapter to a ListView
        lvCountries.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(CountryListActivity.this
                        , CountryDetailActivity.class);
                intent.putExtra("index", i);
                startActivity(intent);
            }
        });*/
    }

    public List<Park> getParkList() {
        return parkList;
    }

}
