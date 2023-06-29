package com.example.saveourwoman;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.saveourwoman.databinding.ActivityMapsBinding;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    //AIzaSyCzRUJALbMMntjiYb3dAW8eC1YO0uTbV8A
    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private static final int REQUEST_CODE = 101;
    FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private double latitude, longitude;
    private Geocoder geocoder;
    private List<Address> addresses,addressList;
    private SearchView searchView;
    public LocationViewModel locationVM;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        searchView=(SearchView) findViewById(R.id.SearchViewID);


        //geocoder class er object create kora
        geocoder = new Geocoder(this, Locale.ENGLISH);

        //initialize fusedlocationprovider cilent
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationVM = new LocationViewModel(this);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location=searchView.getQuery().toString();
                addressList=null;
                if(location!=null ||!location.equals("")){
                    try {
                        addressList=geocoder.getFromLocationName(location,1);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    Address address=addresses.get(0);
                    LatLng latLng=new LatLng(address.getLatitude(),address.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(latLng).title(location));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,20));
                }

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        //map initialize when map is ready to be used then callback is triggered
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

       // mMap.getUiSettings().setCompassEnabled(true);
       getCurrentLocation();

    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_NETWORK_STATE}, 101);
        }

        ///location request ekti class ei class er maddome location request ti send korbo
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(20000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setFastestInterval(20000);

        ///create locationrequest setting//check device location is enabled or not??

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(getApplicationContext())
                .checkLocationSettings(builder.build());

        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    Toast.makeText(MapsActivity.this, "Gps is on", Toast.LENGTH_SHORT).show();
                }
                //e means error
                catch (ApiException e) {
                    //throw new RuntimeException(e);

                    switch (e.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                            try {
                                ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                                resolvableApiException.startResolutionForResult(MapsActivity.this, REQUEST_CODE);
                            } catch (IntentSender.SendIntentException ex) {
                                throw new RuntimeException(ex);
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            break;
                    }
                }

            }

        });

        ///location callback ekti class ei class er maddome location ti pabo
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);

                //Toast.makeText(getApplicationContext(), "Location result is: " + locationResult, Toast.LENGTH_SHORT).show();

                if (locationResult == null) {
                    Toast.makeText(MapsActivity.this, "Current location is null", Toast.LENGTH_SHORT).show();
                }

                for (Location location : locationResult.getLocations()) {
                    if (location != null) {

                        //Toast.makeText(MapsActivity.this, "Current location is: " + location.getLatitude() + " " + location.getLongitude(), Toast.LENGTH_SHORT).show();
                        // latitude=location.getLatitude();
                        //logitude=location.getLongitude();
                        try {
                            addresses = geocoder.getFromLocation(latitude, longitude, 1);
                            String address = addresses.get(0).getAddressLine(0) + "\n" + addresses.get(0).getLocality() + "\n" + addresses.get(0).getPostalCode() + "\n" + addresses.get(0).getCountryName();
                            Toast.makeText(getApplicationContext(), "Current address is: " + address, Toast.LENGTH_SHORT).show();
                            locationVM.PrepareLocationToSave(latitude, longitude, addresses.get(0).getAddressLine(0));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }

            }
        };
        //requestlocationupdate ei method er maddome location ti update koranor jonno request ti kora
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);

        //fetch the current location using getlastlocation

        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();

                    LatLng latLng = new LatLng(latitude, longitude);
                    mMap.addMarker(new MarkerOptions().position(latLng).title("I'm here"));
                    //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,20));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20));
                }
            }
        });

    }

    ///is used for result from the requesting permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (REQUEST_CODE) {
            case REQUEST_CODE:

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getCurrentLocation();
                }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==REQUEST_CODE){
            switch (resultCode){
                case Activity.RESULT_OK:
                    Toast.makeText(this,"Gps is turned on",Toast.LENGTH_SHORT).show();
                    break;
                case Activity.RESULT_CANCELED:
                    Toast.makeText(this, "Gps is required to be turned on", Toast.LENGTH_SHORT).show();
            }
        }
    }

}