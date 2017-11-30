package com.example.comp3717project.comp3717project;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * MainActivity.java
 * Main entry point of the program.
 *
 * @author Alex Xia, Luke Lee, Roger Zhang
 * @version Nov. 29, 2017
 */
public class MainActivity extends AppCompatActivity {

    public enum JsonTypeTag {PARKING, PARKS, SHOPPING, ADDRESS};
    public static HashMap<String, FavoritePlace> favoriteList = new HashMap<>();
    public static ArrayAdapter<String> favPlaceAdapter;
    private SQLiteDatabase db;
    public static ImageView mainBgImg;
    private static int navSelected;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        /**
         * Directs the user to corresponding fragment on selected navigation item.
         *
         * @param item  navigation items
         * @return true if navigation completes successfully
         */
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            Fragment selectedFragment = null;

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    selectedFragment = HomeFragment.newInstance(null);
                    navSelected = 0;
                    break;
                case R.id.navigation_dashboard:
                    // main functionality starts here
                    selectedFragment = MainFragment.newInstance(null);
                    navSelected = 1;
                    break;
                case R.id.navigation_favorites:
                    selectedFragment = FavoriteListFragment.newInstance(null);
                    navSelected = 2;
                    break;
            }

            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            transaction.replace(R.id.content, selectedFragment);
            transaction.addToBackStack(null);
            transaction.commit();
            return true;
        }

    };

    /**
     * Gets called when the activity first loads and creates the activity.
     *
     * @param savedInstanceState    a bundle containing saved instances
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainBgImg = findViewById(R.id.main_background);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        updateFavoriteList(getFavoriteListAll());

        //Manually displaying the home fragment - one time only
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content, HomeFragment.newInstance(null));
        transaction.commit();

        FloatingActionButton FAB = (FloatingActionButton)findViewById(R.id.locate_me_fab);
        FAB.setOnClickListener(new tempFABListener());
    }

    /**
     * Contains the SQL query statement to retrieve list of all favorite places from database.
     *
     * @return a string representing the SQL query
     */
    public static String getFavoriteListAll() {
        return "SELECT " +
                MapDBHelper.FavoriteTable._ID + ", " +
                MapDBHelper.FavoriteTable.NAME + ", " +
                MapDBHelper.FavoriteTable.MARKERTITLE + ", " +
                MapDBHelper.FavoriteTable.LATITUDE + ", " +
                MapDBHelper.FavoriteTable.LONGITUDE +
                " FROM " + MapDBHelper.FavoriteTable.TABLE_NAME;
    }

    /**
     * Takes a SQL query as input and updates the list of favorite places.
     *
     * @param sqlQuery  SQL query
     * @return true if database has content to update the list
     */
    public boolean updateFavoriteList(String sqlQuery) {
        MapDBHelper dbHelper = MapDBHelper.getInstance(this);
        db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(sqlQuery, null);

        ArrayList<String> list = new ArrayList<>();
        boolean hasContent = false;

        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndexOrThrow(MapDBHelper.FavoriteTable.NAME));
                String markerTitle = cursor.getString(cursor.getColumnIndexOrThrow(MapDBHelper.FavoriteTable.MARKERTITLE));
                String lat = cursor.getString(cursor.getColumnIndexOrThrow(MapDBHelper.FavoriteTable.LATITUDE));
                String lon = cursor.getString(cursor.getColumnIndexOrThrow(MapDBHelper.FavoriteTable.LONGITUDE));
                list.add(name);
                favoriteList.put(markerTitle, new FavoritePlace(name, markerTitle, Double.valueOf(lat), Double.valueOf(lon)));
            } while (cursor.moveToNext());
            hasContent = true;
        }

        favPlaceAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);

        cursor.close();
        return hasContent;
    }

    /**
     * An inner listener class that sets onClick event on the floating action bar.
     * Goes to GoogleMapActivity and set a marker on current location.
     */
    private class tempFABListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(MainActivity.this, GoogleMapsActivity.class);
            intent.putExtra("DEST_ADDRESS_EXTRA", "");
            intent.putExtra("SELECTED_ACTION_EXTRA", 4); //exceptional case
            startActivity(intent);
        }
    }

    /**
     * Overrides the activity's onDestroy method; closes the database variable before app terminates.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        db.close();
    }
}
