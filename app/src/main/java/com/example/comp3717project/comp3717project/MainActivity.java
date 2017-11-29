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

public class MainActivity extends AppCompatActivity {

    public enum JsonTypeTag {PARKING, PARKS, SHOPPING, ADDRESS};
    public static HashMap<String, FavoritePlace> favoriteList = new HashMap<>();
    public static ArrayAdapter<String> favPlaceAdapter;
    private SQLiteDatabase db;
    public static ImageView mainBgImg;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            Fragment selectedFragment = null;

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    selectedFragment = HomeFragment.newInstance(null);
                    break;
                case R.id.navigation_dashboard:
                    // should be changed to another fragment
                    selectedFragment = MainFragment.newInstance(null);
                    break;
                case R.id.navigation_favorites:
                    selectedFragment = FavoriteListFragment.newInstance(null);
                    break;
            }

            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            transaction.replace(R.id.content, selectedFragment);
            transaction.addToBackStack(null);
            transaction.commit();
            return true;
        }

    };

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

    public static String getFavoriteListAll() {
        return "SELECT " +
                MapDBHelper.FavoriteTable._ID + ", " +
                MapDBHelper.FavoriteTable.NAME + ", " +
                MapDBHelper.FavoriteTable.MARKERTITLE + ", " +
                MapDBHelper.FavoriteTable.LATITUDE + ", " +
                MapDBHelper.FavoriteTable.LONGITUDE +
                " FROM " + MapDBHelper.FavoriteTable.TABLE_NAME;
    }

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

    private class tempFABListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(MainActivity.this, GoogleMapsActivity.class);
            intent.putExtra("DEST_ADDRESS_EXTRA", "");
            intent.putExtra("SELECTED_ACTION_EXTRA", 4); //exceptional case
            startActivity(intent);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        db.close();
    }
}
