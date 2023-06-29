package com.example.saveourwoman;

public class PoliceStation {
    public double latitude;
    public double longitude;
    public String name;
    public String phoneNo;
    public String email;
    public String district;
    double distanceFromUser = 0.0;

    public PoliceStation()
    {
        //Empty constructor
    }

    public PoliceStation(String name, double lat, double lng, String phoneNo, String email, String district)
    {
        latitude = lat;
        longitude = lng;
        this.name = name;
        this.phoneNo = phoneNo;
        this.email = email;
        this.district = district;
    }
}
