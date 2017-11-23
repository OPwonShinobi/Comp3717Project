package com.example.comp3717project.comp3717project;

public class ParkingPayStations
{
    private double latitude;
    private double longitude;

    public ParkingPayStations(double latitude, double longitude)
    {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLat()
    {
        return latitude;
    }

    public double getLon()
    {
        return longitude;
    }
}
