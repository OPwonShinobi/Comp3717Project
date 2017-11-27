package com.example.comp3717project.comp3717project;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by lpmle on 2017-11-25.
 */

public class MapDBHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "NewWestParking.db";

    static class FavoriteTable implements BaseColumns {
        static final String TABLE_NAME = "FavoritePlace";
        static final String NAME = "Name";
        static final String MARKERTITLE = "MarkerTitle";
        static final String LATITUDE = "Latitude";
        static final String LONGITUDE = "Longitude";
    }
    private static final String SQL_CREATE_FAVORITE_TABLE =
            "CREATE TABLE " + FavoriteTable.TABLE_NAME + " (" +
                    FavoriteTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    FavoriteTable.NAME + " TEXT, " +
                    FavoriteTable.MARKERTITLE + " TEXT UNIQUE, " +
                    FavoriteTable.LATITUDE + " REAL, " +
                    FavoriteTable.LONGITUDE + " REAL);";

    private static MapDBHelper instance = null;

    public static synchronized MapDBHelper getInstance(Context context) {
        if (instance == null) {
            return new MapDBHelper(context);
        }
        return instance;
    }

    private MapDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_FAVORITE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + FavoriteTable.TABLE_NAME);
        onCreate(db);
    }
}
