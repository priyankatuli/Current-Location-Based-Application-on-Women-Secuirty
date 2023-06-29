package com.example.saveourwoman;

import java.util.List;

public class PoliceStationsListMapper {
    public List<PoliceStation> stations;

    public PoliceStationsListMapper()
    {
        // Empty Constructor
    }

    public PoliceStationsListMapper(List<PoliceStation> stations)
    {
        this.stations = stations;
    }
}
