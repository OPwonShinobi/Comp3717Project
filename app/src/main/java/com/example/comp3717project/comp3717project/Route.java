package com.example.comp3717project.comp3717project;

/**
 * Created by Alex on 22/11/2017.
 */

public class Route {
    private String src;
    private String destn;
    private String polyline;

    public Route(String src, String destn, String polyline) {
        this.src = src;
        this.destn = destn;
        this.polyline = polyline;
    }

    public String getSrc() {
        return src;
    }

    public String getDestn() {
        return destn;
    }

    public String getPolyline() {
        return polyline;
    }
}
