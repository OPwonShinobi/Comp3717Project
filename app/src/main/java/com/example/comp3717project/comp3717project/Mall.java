package com.example.comp3717project.comp3717project;

/**
 * Created by Alex on 22/11/2017.
 */

public class Mall {
    private String name;
    private String longitude;
    private String latitude;
    //private String polyline; sadly this is a list, and thats too much trouble

    public Mall(String name, String longitude, String latitude) {
        this.name = name;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public String getName() {
        return name;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getLatitude() {
        return latitude;
    }
}
