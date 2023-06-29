package com.example.saveourwoman;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.sql.Struct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NearbyPoliceStationsActivity extends AppCompatActivity implements ILocationService {

    private CardView addPoliceStations;
    private LocationViewModel locationVM;
    private FirebaseFirestore db;
    private List<PoliceStation> stations;
    private ImageView policeCall1, policeMssg1;
    private ImageView policeCall2, policeMssg2;
    private ImageView policeCall3, policeMssg3;

    private CardView firstPs, secondPs, thirdPs;
    private TextView firstPsInfo, secondPsInfo, thirdPsInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_police_stations);
        this.setTitle(R.string.text8);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.color4)));

        InitializeModel();

        GetCurrentLocation();
    }

    public void InitializeModel()
    {
        addPoliceStations = findViewById(R.id.AddPoliceStationNumber);
        policeCall1 = findViewById(R.id.policeCall1);
        policeMssg1 = findViewById(R.id.policeMssg1);

        policeCall2 = findViewById(R.id.policeCall2);
        policeMssg2 = findViewById(R.id.policeMssg2);

        policeCall3 = findViewById(R.id.policeCall3);
        policeMssg3 = findViewById(R.id.policeMssg3);

        firstPs = findViewById(R.id.FirstPoliceStationId);
        secondPs = findViewById(R.id.SecondPoliceStationId);
        thirdPs = findViewById(R.id.ThirdPoliceStationId);

        firstPsInfo = findViewById(R.id.PSName1);
        secondPsInfo = findViewById(R.id.PSName2);
        thirdPsInfo = findViewById(R.id.PSName3);

        locationVM = new LocationViewModel(this);

        db = FirebaseFirestore.getInstance();

        //Temporary button for uploading police stations info
        addPoliceStations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddNewStations();
            }
        });
    }

    void GetCurrentLocation()
    {
        try {
            locationVM.SetLocationService(this::NotifyAboutLocationSet);
            // Initialize geoCoder and fusedlocationprovider cilent
            locationVM.InitializeModel();

            // Call get location method of the LocationViewModel class
            locationVM.GetLocation(this);
        }
        catch (Exception ex)
        {
            Log.d("Bachao", "Exception at GetCurrentLocation : " + ex);
        }
    }

    //For adding police stations info only
    private void InsertPoliceStationList ()
    {
        stations = new ArrayList<>();
        stations.add(new PoliceStation("Tangail Sadar Thana", 24.246485 , 89.913969, "017XXXXXXXX", "octan.tan@police.gov.bd", "Tangail"));
        stations.add(new PoliceStation("Delduar Thana", 24.1636355 , 89.9668828, "013XXXXXXXX", "octan.del@police.gov.bd", "Tangail"));
        stations.add(new PoliceStation("Nagarpur Thana", 24.061822 , 89.879745, "016XXXXXXXX", "octan.nag@police.gov.bd", "Tangail"));
        stations.add(new PoliceStation("Mirzapur Thana", 24.1020195 , 90.0983805, "015XXXXXXXX", "octan.mir@police.gov.bd", "Tangail"));
        stations.add(new PoliceStation("Basail Thana", 24.22982 , 90.05266, "018XXXXXXXX", "octan.bas@police.gov.bd", "Tangail"));
        stations.add(new PoliceStation("Sakhipur Thana", 24.318355 , 90.170202, "019XXXXXXXX", "octan.sak@police.gov.bd", "Tangail"));
        stations.add(new PoliceStation("Kalihati Thana", 24.384523  , 89.989802, "017XXXXXXXX", "octan.kal@police.gov.bd", "Tangail"));
        stations.add(new PoliceStation("Ghatail Thana", 24.483516  , 89.972961, "015XXXXXXXX", "octan.gha@police.gov.bd", "Tangail"));
        stations.add(new PoliceStation("Madhupur Thana", 24.605139  , 90.02593, "016XXXXXXXX", "octan.mad@police.gov.bd", "Tangail"));
        stations.add(new PoliceStation("Dhanbari Thana", 24.669274  , 89.957371, "018XXXXXXXX", "octan.dha@police.gov.bd", "Tangail"));
        stations.add(new PoliceStation("Gopalpur Thana", 24.5620636  , 89.9287605, "019XXXXXXXX", "octan.gop@police.gov.bd", "Tangail"));
        stations.add(new PoliceStation("Bhuapur Thana", 24.4626  , 89.873056, "013XXXXXXXX", "octan.bhu@police.gov.bd", "Tangail"));
        stations.add(new PoliceStation("Bangha Bandhu Setu East Thana", 24.387494  , 89.823698, "017XXXXXXXX", "octan.jum@police.gov.bd", "Tangail"));
    }

    //For adding police stations info only
    public void AddNewStations()
    {
        try {
            Map<String, Object> data = new HashMap<>();

            InsertPoliceStationList();

            data.put("stations", stations);

//            db.collection("policeStationCollections/stations/list").document(userId)
//                    .delete()
//                    .addOnSuccessListener(new OnSuccessListener<Void>() {
//
//
//                        @Override
//                        public void onSuccess(Void unused) {
//                            Toast
//                                    .makeText(getApplicationContext(), "Police Station Successfully Deleted.", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//            );

            db.collection("policeStationCollections/stations/list").document("common")
                    .set(data)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.d("Bachao", "Police Station Successfully Added");
                            Toast.makeText(getApplicationContext(), "Police Station Successfully Added", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("Bachao", e.toString());
                            Toast.makeText(getApplicationContext(), "Failed to add police station", Toast.LENGTH_SHORT).show();
                        }
                    }
            );
        }
        catch (Exception e)
        {
            Log.d("Bachao", "Exception at AddNewStations()" + e);
        }
    }

    public void GetPoliceStations(double lat, double lng, boolean saveStation)
    {
        try {
            db.collection("policeStationCollections/stations/list").document("common")
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists())
                            {
                                stations = new ArrayList<>();
                                stations = documentSnapshot.toObject(PoliceStationsListMapper.class).stations;

                                if (stations.size() > 0)
                                {
                                    ShowNearestPoliceStations(lat, lng);
                                }

                                if (saveStation)
                                {
                                    AddNewStations();
                                }
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("Bachao", e.toString());
                            Toast.makeText(getApplicationContext(), "Failed to load police stations", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        catch (Exception e)
        {
            Log.d("Bachao", e.toString());
            Toast.makeText(getApplicationContext(), "Failed to load police stations", Toast.LENGTH_SHORT).show();
        }
    }

    public void CalculateDistanceOfPoliceStations(double lat, double lng)
    {
        PoliceStation temp;

        for (int i = 0; i < stations.size(); i++)
        {
            temp = stations.get(i);

            if (temp == null)
            {
                continue;
            }

            temp.distanceFromUser = GetDistance(lat, lng, temp.latitude, temp.longitude);

            stations.set(i, temp);
        }
    }

    public String SetPoliceInfo(PoliceStation ps)
    {
        if (ps == null)
        {
            return null;
        }

        String info = ps.name + "\nDistrict: " + ps.district
                + "\nDistance: " + String.format("%.2f", ps.distanceFromUser) + " km"
                + "\nMobile: " + ps.phoneNo
                + "\nEmail: " + ps.email;

        return info;
    }

    public void MakePhoneCall(String phoneNo, int index)
    {
        Toast.makeText(getApplicationContext(), "Calling to police" + index, Toast.LENGTH_SHORT).show();
        //ToDo: Add functionality to make phone call
    }

    public void SendMessage(String phoneNo, int index)
    {
        Toast.makeText(getApplicationContext(), "Sending msg to police" + index, Toast.LENGTH_SHORT).show();
        //ToDo: Add functionality to send message
    }

    public void SetActionListenerForCall(ImageView callBtn, String phoneNo, int index)
    {
        if (callBtn == null)
        {
            return;
        }

        callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MakePhoneCall(phoneNo, index);
            }
        });
    }

    public void SetActionListenerForMessage(ImageView MsgBtn, String phoneNo, int index)
    {
        if (MsgBtn == null)
        {
            return;
        }

        MsgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendMessage(phoneNo, index);
            }
        });
    }

    public void MakeVisibleNearestPSs()
    {
        // if first stations is available, make visible it
        if (stations.size() > 0)
        {
            firstPs.setVisibility(View.VISIBLE);
            firstPsInfo.setText(SetPoliceInfo(stations.get(0)));
            SetActionListenerForCall(policeCall1, stations.get(0).phoneNo, 1);
            SetActionListenerForMessage(policeMssg1, stations.get(0).phoneNo, 1);
        }

        // if second stations is available, make visible it
        if (stations.size() > 1)
        {
            secondPs.setVisibility(View.VISIBLE);
            secondPsInfo.setText(SetPoliceInfo(stations.get(1)));
            SetActionListenerForCall(policeCall2, stations.get(1).phoneNo, 2);
            SetActionListenerForMessage(policeMssg2, stations.get(1).phoneNo, 2);
        }

        // if third stations is available, make visible it
        if (stations.size() > 2)
        {
            thirdPs.setVisibility(View.VISIBLE);
            thirdPsInfo.setText(SetPoliceInfo(stations.get(2)));
            SetActionListenerForCall(policeCall3, stations.get(2).phoneNo, 3);
            SetActionListenerForMessage(policeMssg3, stations.get(2).phoneNo, 3);
        }
    }

    public void ShowNearestPoliceStations(double lat, double lng)
    {
        if (stations == null)
        {
            return ;
        }

        //calculate distance from user's position for every police stations
        CalculateDistanceOfPoliceStations(lat, lng);

        stations.sort(Comparator.comparingDouble(o -> o.distanceFromUser));

        // Show most three nearest stations

        MakeVisibleNearestPSs();
    }

    //Calculate distance using Haversine formula to get more accurate distance
    public double GetDistance(double x1, double y1, double x2, double y2)
    {
        double earthRadius = 6371; // in km
        double latDifference = Math.toRadians(x1 - x2);
        double lngDifference = Math.toRadians(y1 - y2);

        double a = Math.sin(latDifference / 2) * Math.sin(latDifference / 2) + Math.cos(Math.toRadians(x1)) * Math.cos(Math.toRadians(x2)) * Math.sin(lngDifference / 2) * Math.sin(lngDifference / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return earthRadius * c;
    }

    @Override
    public void NotifyAboutLocationSet(double latitude, double longitude) {
        if (latitude == -1 && longitude == -1)
        {
            Log.d("Bachao", "Current Location is unknown");
            Toast.makeText(getApplicationContext(), "Current Location is unknown", Toast.LENGTH_SHORT).show();

            return ;
        }

        GetPoliceStations(latitude, longitude, false);
    }
}