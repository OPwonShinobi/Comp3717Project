package com.example.comp3717project.comp3717project;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by lpmle on 2017-11-26.
 */

public class FavoriteListFragment extends Fragment {

    private SQLiteDatabase db;
    private ListView favListView;

    public FavoriteListFragment() {
        // Required empty public constructor
    }

    public static FavoriteListFragment newInstance(Bundle bundle) {
        FavoriteListFragment fragment = new FavoriteListFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // set background image opacity (between 0~255)
        MainActivity.mainBgImg.setImageAlpha(100);

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_favorite_list, container, false);
        favListView = rootView.findViewById(R.id.favListView);

        // put favorite list content from db to listview adapter
        updateFavoriteListView(MainActivity.getFavoriteListAll());

        favListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Bundle b = new Bundle();
                String str = (String) favListView.getItemAtPosition(i);

                // gets the marker title from dtatbase and put as destination
                MapDBHelper dbHelper = MapDBHelper.getInstance(getActivity());
                db = dbHelper.getReadableDatabase();
                String[] columns = new String[]{MapDBHelper.FavoriteTable.NAME
                        , MapDBHelper.FavoriteTable.MARKERTITLE
                        , MapDBHelper.FavoriteTable.LATITUDE
                        , MapDBHelper.FavoriteTable.LONGITUDE};
                Cursor cursor = db.query(MapDBHelper.FavoriteTable.TABLE_NAME
                        , columns
                        , MapDBHelper.FavoriteTable.NAME + "=?"
                        , new String[] {str}
                        , null
                        , null
                        , null);
                List<String> list = new ArrayList<>();
                while(cursor.moveToNext()) {
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(MapDBHelper.FavoriteTable.NAME));
                    String markerTitle = cursor.getString(cursor.getColumnIndexOrThrow(MapDBHelper.FavoriteTable.MARKERTITLE));
                    list.add(markerTitle);
                }

                Intent intent = new Intent(getActivity(), GoogleMapsActivity.class);
                intent.putExtra("DEST_ADDRESS_EXTRA", list.get(0));
                startActivity(intent);
            }
        });

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    public boolean updateFavoriteListView(String sqlQuery) {
        ArrayList<String> list = new ArrayList<>();
        boolean hasContent = false;

        if (MainActivity.favoriteList.size() != 0) {
            for (HashMap.Entry<String, FavoritePlace> entry : MainActivity.favoriteList.entrySet()) {
                list.add(entry.getValue().getName());
            }
            hasContent = true;
        }


        ArrayAdapter<String> favPlaceAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, list);
        favListView.setAdapter(favPlaceAdapter);
        return hasContent;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateFavoriteListView(MainActivity.getFavoriteListAll());
    }
}
