package com.example.comp3717project.comp3717project;

/**
 * Created by lpmle on 2017-11-24.
 */

public class FavoritePlace {
    String name;
    double latitude;
    double longitude;

    public FavoritePlace(String name, double latitude, double longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
