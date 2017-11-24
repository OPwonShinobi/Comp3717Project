package com.example.comp3717project.comp3717project;

/**
 * Note, this is a generalization of a parking meter address, and a parking pay station address.
 * Since they have the same members and functions, there was no point having 2 of them.
 * Created by Alex on 23/11/2017.
 */

public class ParkingLot {
    private String name;
    private double latitude;
    private double longitude;

    public ParkingLot(String name, double latitude, double longitude)
    {
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
    }

    public double getLat()
    {
        return latitude;
    }
    public double getLon()
    {
        return longitude;
    }
    public String getName() { return name; }
}
