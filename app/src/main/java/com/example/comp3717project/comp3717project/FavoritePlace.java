package com.example.comp3717project.comp3717project;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by lpmle on 2017-11-24.
 */

public class FavoritePlace implements Parcelable {
    String name;
    String markerTitle;
    double latitude;
    double longitude;

    public FavoritePlace(String name, String markerTitle, double latitude, double longitude) {
        this.name = name;
        this.markerTitle = markerTitle;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    private FavoritePlace(Parcel in) {
        name = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
    }

    public String getName() {
        return name;
    }

    public String getMarkerTitle() {
        return markerTitle;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
    }

    public static final Parcelable.Creator<FavoritePlace> CREATOR
            = new Parcelable.Creator<FavoritePlace>() {
        public FavoritePlace createFromParcel(Parcel in) {
            return new FavoritePlace(in);
        }

        public FavoritePlace[] newArray(int size) {
            return new FavoritePlace[size];
        }
    };
}
