package com.example.comp3717project.comp3717project;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

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
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_favorite_list, container, false);
        favListView = rootView.findViewById(R.id.favListView);

        MapDBHelper dbHelper = MapDBHelper.getInstance(getActivity());
        db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(MainActivity.getFavoriteListAll(), null);

        ArrayList<String> list = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndexOrThrow(MapDBHelper.FavoriteTable.NAME));
                list.add(name);
            } while (cursor.moveToNext());
        }

        ArrayAdapter<String> favPlaceAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, list);
        favListView.setAdapter(favPlaceAdapter);
        //setListAdapter(favPlaceAdapter);

        db.close();
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }
}
