package com.example.saveourwoman;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.List;
import java.util.Locale;

public class ShareLocationActivity extends AppCompatActivity implements View.OnClickListener  {

    private static final int REQUEST_CODE=101;
    private Button ShareButton, lastLocations;

    FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    public double latitude, longitude;
    private Geocoder geocoder;
    private List<Address> addresses;
    private LocationManager locationManager;

    public LocationViewModel locationVM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_location);

        this.setTitle("Share Your Real Time Location");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.color4)));

        ShareButton = findViewById(R.id.ShareButton);
        lastLocations = findViewById(R.id.LastLocationButtton);

        ShareButton.setOnClickListener(this);
        lastLocations.setOnClickListener(this);
        InitializeModel();

        locationVM = new LocationViewModel(this);
    }

    public void InitializeModel()
    {
        //geocoder class er object create kora
        geocoder = new Geocoder(this, Locale.ENGLISH);

        //initialize fusedlocationprovider cilent
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.ShareButton)
        {
            ShareData();
        }
        else if (view.getId() == R.id.LastLocationButtton)
        {
            Intent intent = new Intent(getApplicationContext(), ViewLastLocationsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    public void ShareData() {

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_NETWORK_STATE}, 101);
        }

        //
        locationRequest = LocationRequest.create();

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "GPS is on", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "GPS is required to be turned on", Toast.LENGTH_SHORT).show();

        }

        Log.d("DEBUG", "Entered into shareData()");

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }

                Log.d("DEBUG", "Entered into onLocationResult()");

                for (Location location : locationResult.getLocations()) {
                    if (location != null) {

                        try {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            addresses = geocoder.getFromLocation(latitude, longitude, 1);
                            String address = String.format("%f,%f", latitude, longitude);
                            String add = addresses.get(0).getAddressLine(0);
                            //String Url = "https://maps.app.goo.gl/?link=https://www.google.com/maps/search/?API=1&query=" + address;
                            String Url = "https://www.google.com/maps/dir/?api=1&destination=" + address + "\n" + add;
                            Intent shareIntent = new Intent(Intent.ACTION_SEND);
                            shareIntent.setType("text/plain");
                            //shareIntent.putExtra(Intent.EXTRA_TEXT,data);
                            shareIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"priyankaaatuli123@gmail.com", "ce18047@mbstu.ac.bd"});
                            shareIntent.putExtra(Intent.EXTRA_TEXT, "Please help! I'm in Danger Here is my current location:" + Url);
                            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My Location");
                            //shareIntent.putExtra(Intent.EXTRA_STREAM, Url);
                            startActivity(Intent.createChooser(shareIntent, "Share Via"));

                            PackageManager pm = getPackageManager();
                            locationVM.PrepareLocationToSave(latitude, longitude, add);
                            if (shareIntent.resolveActivity(pm) != null) {
                                //startActivity(shareIntent);
                                // Toast.makeText(getApplicationContext(), "ALready installed", Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(getApplicationContext(), "Software isn't installed", Toast.LENGTH_SHORT);
                            }
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }

                }

            }
        };
        Log.d("DEBUG", "Before fused location");
        //request to update location using requestLocationUpdates method
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
        Log.d("DEBUG", "After fused location");
        //fetch the current location using getlastlocation
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();

                }
                Log.d("DEBUG", "Into fused getLastLocation" + latitude + longitude);
            }
        });
    }

    @Override   ///is used for result from the requesting permission
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (REQUEST_CODE) {
            case REQUEST_CODE:

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ShareData();
                }
        }
    }
}