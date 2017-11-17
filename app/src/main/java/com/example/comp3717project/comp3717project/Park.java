package com.example.comp3717project.comp3717project;

import java.util.List;

public class Park {
    private String parkName;
    private String strName;
    private String strNum;
    private String category;
    //private List<List<Double>> coordList; // in [longitude, latitude];

    private Park() {
    }

    public Park(String parkName, String strName, String strNum, String category, List<List<Double>> coordList) {
        this.parkName = parkName;
        this.strName = strName;
        this.strNum = strNum;
        this.category = category;
        //this.coordList = coordList;
        //this.jsonGeo = jsonGeo;
    }

    public String getParkName() {
        return parkName;
    }
    public String getStrName() {
        return strName;
    }
    public String getStrNum() {
        return strNum;
    }
    public String getCategory() {
        return category;
    }
    public List<List<Double>> getCoordList() {
        //return coordList;
        return null;
    }
}
