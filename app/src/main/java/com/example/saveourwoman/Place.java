package com.example.saveourwoman;

public class Place {

    public double lat;
    public double lng;
    public String address;
    public String createdOn;

    public Place()
    {
        //Empty Constructor
    }

    public Place(double lat, double lng, String address, String createdOn)
    {
        this.lat = lat;
        this.lng = lng;
        this.address = address;
        this.createdOn = createdOn;
    }
}
