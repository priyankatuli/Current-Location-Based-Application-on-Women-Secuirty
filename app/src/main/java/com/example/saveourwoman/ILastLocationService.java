package com.example.saveourwoman;

import java.util.List;

public interface ILastLocationService {
    void NotifyOnSuccessfulRetrievalOfLastLocations(List<Place> places);
}
