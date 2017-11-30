package com.example.comp3717project.comp3717project;

public class Park {
    private String parkName;
    private String strName;
    private String strNum;
    private String category;
    private String strPolyline;

    public Park(String parkName, String strName, String strNum, String category) {
        this.parkName = parkName;
        this.strName = strName;
        this.strNum = strNum;
        this.category = category;
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

    public String getStrPolyline() {
        return strPolyline;
    }

    public void setStrPolyline(String strPolyline) {
        this.strPolyline = strPolyline;
    }
}
