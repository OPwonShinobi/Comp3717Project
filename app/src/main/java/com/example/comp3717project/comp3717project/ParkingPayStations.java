package com.example.comp3717project.comp3717project;

public class ParkingPayStations
{
    private String name;
    private double latitude;
    private double longitude;

    public ParkingPayStations(String name, double latitude, double longitude)
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
