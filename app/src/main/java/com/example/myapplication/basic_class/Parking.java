package com.example.myapplication.basic_class;


public class Parking {
    private String park_id;

    private boolean isusig=true;

    public String getPark_id() {
        return park_id;
    }

    public void setPark_id(String park_id) {
        this.park_id = park_id;
    }

    public boolean getIsusig() {
        return isusig;
    }

    public void setIsusig(boolean isusig) {
        this.isusig = isusig;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    private double latitude;

    private double longitude;


}
