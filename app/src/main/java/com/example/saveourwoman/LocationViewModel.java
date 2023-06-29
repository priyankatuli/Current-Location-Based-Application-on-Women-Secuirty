package com.example.saveourwoman;

import static android.content.Context.LOCATION_SERVICE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class LocationViewModel {
    private Context context;
    FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    public double latitude = -1.0, longitude = -1.0;
    private Geocoder geocoder;
    private List<Address> addresses;
    private LocationManager locationManager;

    private ILocationService _locationService;
    private ILastLocationService _lastLocationService;
    private FirebaseFirestore db;
    public LocationViewModel()
    {
        // Empty constructor
    }

    public LocationViewModel(Context context)
    {
        this.context = context;
        db = FirebaseFirestore.getInstance();
    }

    // Initialize locationService interface
    public void SetLocationService(ILocationService _locationService)
    {
        this._locationService = _locationService;
    }

    // Initialize LastLocationService interface

    public void SetLastLocationService(ILastLocationService _lastLocationService)
    {
        this._lastLocationService = _lastLocationService;
    }

    public void InitializeModel()
    {
        try {
            //geocoder class er object create kora
            geocoder = new Geocoder(context, Locale.ENGLISH);

            //initialize fusedlocationprovider cilent
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        }
        catch(Exception ex)
        {
            Log.d("Bachao", "Exception at InitializeModel: " + ex);
        }
    }

    public void GetLocation(Activity activity) {
        try {
            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_NETWORK_STATE}, 101);
            }

            locationRequest = LocationRequest.create();

            locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Toast.makeText(context, "GPS is on", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "GPS is required to be turned on", Toast.LENGTH_SHORT).show();
            }

            Log.d("Bachao", "Entered into shareData()");

            locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(@NonNull LocationResult locationResult) {
                    if (locationResult == null) {

                        // Send lat -1.0 and long -1.0 to indicate that location is not set due to internet issue or other technical issue

                        _locationService.NotifyAboutLocationSet(-1.0, -1.0);
                        return;
                    }

                    Log.d("Bachao", "Entered into onLocationResult()");
                    for (Location location : locationResult.getLocations()) {
                        if (location != null) {
                            try {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                            Log.d("Bachao", "Into onLocationResult" + latitude + longitude);
                        }
                    }

                    // call the provided callback method to notify of successful retrieval of latitude and longitude
                    _locationService.NotifyAboutLocationSet(latitude, longitude);

                    PrepareLocationToSave(latitude, longitude, null);
                }
            };
            Log.d("Bachao", "Before fused location");
            //request to update location using requestLocationUpdates method
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
            Log.d("Bachao", "After fused location");
        }
        catch (Exception ex)
        {
            Log.d("Bachao", "Exception at GetLocation: " + ex);

            // Send lat -1.0 and long -1.0 to indicate that location is not set due to internet issue or other technical issue

            _locationService.NotifyAboutLocationSet(-1.0, -1.0);
        }
    }

    public String GetAddressFromLatLong(double lat, double lng)
    {
        // for debugging purposes
        Log.d("Bachao", "lat & lng : " + lat + lng);

        // If exception occurred while getting location, send default Location unknown message
        String address = "Unknown";

        try {
            addresses = geocoder.getFromLocation(lat, lng, 1);

            //if location name is retrieved successfully, then add location name
            if (addresses.size() > 0)
            {
                address = addresses.get(0).getAddressLine(0);
            }
        }
        catch (Exception ex)
        {
            Log.d("Bachao", "Exception at GetAddressFromLatLong: " + ex);
        }
        return address;
    }

    // Following methods are for getting and saving last locations

    //Starts here

    public void PrepareLocationToSave(double lat, double lng, String address)
    {
        if (lat == -1.0 && lng == -1.0)
        {
            // Don't save. User location not found.

            return ;
        }

        // Otherwise save location. For this first get existing saved locations

        GetSavedLocation(lat, lng, true, address);
    }

    public FirebaseUser getLoggedInUser()
    {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        if (mAuth == null)
        {
            return null;
        }

        return mAuth.getCurrentUser();
    }

    public void GetSavedLocation(double lat, double lng, boolean saveLocation, String address)
    {
        FirebaseUser user = getLoggedInUser();

        if (user == null)
        {
            return ;
        }

        String userId = user.getUid();

        try {
            Log.d("Bachao", "Entered into GetSavedLocation");

            db.collection("savedLocations/location/list").document(userId)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            try {
                                List<Place> places = new ArrayList<>();

                                if (documentSnapshot.exists())
                                {
                                    places = documentSnapshot.toObject(PlaceListMapper.class).places;
                                }

                                if (saveLocation)
                                {
                                    SaveLocation(lat, lng, places, address);
                                }

                                if (_lastLocationService != null)
                                {
                                    _lastLocationService.NotifyOnSuccessfulRetrievalOfLastLocations(places);
                                }

                                Log.d("Bachao", "Successfully locations retrieved.");
                            }
                            catch (Exception ex)
                            {
                                // For debugging purposes
                                Log.d("Bachao", "Exception getting location. " + ex);
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("Bachao", "Exception while getting location " + e.toString());

                            if (saveLocation)
                            {
                                SaveLocation(lat, lng, null, address);
                            }
                        }
                    });
        }
        catch (Exception e)
        {
            Log.d("RunTimeException", e.toString());
        }
    }

    //This method will return only latest 10 locations
    private List<Place> GetFinalListOfPlace(List<Place> places, Place newPlace)
    {
        if (places.size() >= 10)
        {
            // If already 10 locations are saved, then remove first location and then add new location to the end
            places.remove(0);
        }

        // Add new location to the end;
        places.add(newPlace);

        return places;
    }

    public String GetCurrentLocalTime()
    {
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

        return dateFormatter.format(currentTime);
    }

    public void SaveLocation(double lat, double lng, List<Place> places, String address)
    {

        FirebaseUser user = getLoggedInUser();

        if (user == null)
        {
            return ;
        }

        String userId = user.getUid();

        if (places == null)
        {
            places = new ArrayList<>();
        }

        // Add new location to the end of the list. If length overflow of length 10, then remove first place.
        if (address == null)
        {
            places = GetFinalListOfPlace(places, new Place(lat, lng, GetAddressFromLatLong(lat, lng), GetCurrentLocalTime()));
        }
        else
        {
            places = GetFinalListOfPlace(places, new Place(lat, lng, address, GetCurrentLocalTime()));
        }

        try {
            Map<String, Object> data = new HashMap<>();

            data.put("places", places);

            Log.d("Bachao", "Entered into GetSavedLocation");

            db.collection("savedLocations/location/list").document(userId)
                    .set(data)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.d("Bachao", "Location Successfully saved.");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("Bachao", "Exception while saving location " + e.toString());
                        }
                    });
        }
        catch (Exception e)
        {
            Log.d("Bachao", e.toString());
        }
    }

}
