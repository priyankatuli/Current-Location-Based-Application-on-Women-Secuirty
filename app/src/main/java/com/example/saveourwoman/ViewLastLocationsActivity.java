package com.example.saveourwoman;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class ViewLastLocationsActivity extends AppCompatActivity implements ILastLocationService {

    private LocationViewModel locationVM;
    private TableLayout lastLocationTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_last_locations);
        this.setTitle("Last Opened Locations");

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.color4)));
        locationVM = new LocationViewModel(this);
        lastLocationTable = findViewById(R.id.tableForSavedLocations);
        GetLastLocations();
    }

    @Override
    public void NotifyOnSuccessfulRetrievalOfLastLocations(List<Place> places) {

        if (places.size() > 0)
        {
            // If saved locations are available then show them
            ShowLastLocations(places);
        }
        else
        {
            // Otherwise show no saved locations available message
            showNoSavedLocationAvailableMessage();
        }
    }

    public void GetLastLocations() {
        try {
            locationVM.SetLastLocationService(this);
            // Initialize geoCoder and fusedlocationprovider cilent
            locationVM.InitializeModel();

            // Call get savedLocation method of the LocationViewModel class
            locationVM.GetSavedLocation(-1.0, -1.0, false, null);
        }
        catch (Exception ex)
        {
            Log.d("Bachao", "Exception at GetLastLocations: " + ex);
        }
    }

    private View addLine()
    {
        final View vline = new View(getApplicationContext());
        vline.setLayoutParams(new
                TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 1));
        vline.setBackgroundColor(Color.BLUE);

        return vline;
    }

    public void ShowLastLocations(List<Place> places)
    {
        try{

            boolean addHeader = true;
            int rowTextSize = 16;

            // Starting from -1 in order to add an extra row (Column names) in the table

            for(int i = -1; i < places.size(); i++){

                TableRow tr = new TableRow(getApplicationContext());

                tr.setLayoutParams(new TableRow.LayoutParams(
                        TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.WRAP_CONTENT));
                tr.setMinimumHeight(120);

                // this will be executed once and will add header
                if(addHeader){

                    TextView header2 = new TextView(getApplicationContext());
                    header2.setPadding(10, 5, 0, 5);
                    header2.setTextSize(rowTextSize);
                    header2.setText("SL");
                    header2.setTextColor(Color.BLUE);
                    header2.setGravity(Gravity.CENTER);
                    tr.addView(header2);

                    TextView header3 = new TextView(getApplicationContext());
                    header3.setPadding(40, 5, 0, 5);
                    header3.setText("Address");
                    header3.setTextColor(Color.BLUE);
                    header3.setTextSize(rowTextSize);
                    tr.addView(header3);

//                    TextView header4 = new TextView(getApplicationContext());
//                    header4.setPadding(35, 5, 0, 5);
//                    header4.setText("Place");
//                    header4.setTextColor(Color.BLUE);
//                    header4.setTextSize(rowTextSize);
//                    tr.addView(header4);

//                    TextView header5 = new TextView(getApplicationContext());
//                    header5.setPadding(35, 5, 0, 5);
//                    header5.setText("Time");
//                    header5.setTextColor(Color.BLUE);
//                    header5.setTextSize(rowTextSize);
//                    tr.addView(header5);

                    tr.setGravity(Gravity.CENTER);

                    lastLocationTable.addView(tr);

                    // add line below heading

                    lastLocationTable.addView(addLine());

                    addHeader = false;
                }
                else
                {
                    Place place = places.get(i);
                    final int in = i;

                    TextView column2 = new TextView(getApplicationContext());
                    column2.setPadding(10, 5, 0, 5);
                    column2.setTextSize(rowTextSize);
                    column2.setGravity(Gravity.CENTER);
                    String sl = String.format("%d.", i + 1);
                    column2.setText(sl);
                    column2.setTextColor(Color.BLACK);
                    tr.addView(column2);

                    TextView column3 = new TextView(getApplicationContext());
                    column3.setPadding(35, 5, 0, 5);
                    String locationDetails = String.format("Latitude: %f\n", place.lat)
                                + String.format("Longitude: %f\n", place.lng)
                                + "Address: " + place.address + "\n"
                                + "Time: " + place.createdOn + "\n";
                    column3.setText(locationDetails);
                    column3.setTextColor(Color.BLACK);
                    column3.setTextSize(rowTextSize);
                    tr.addView(column3);

//                    TextView column4 = new TextView(getApplicationContext());
//                    column4.setPadding(35, 5, 0, 5);
//                    String str3 = place.address;
//                    column4.setText(str3);
//                    column4.setTextColor(Color.BLACK);
//                    column4.setTextSize(rowTextSize);
//                    tr.addView(column4);

//                    TextView column5 = new TextView(getApplicationContext());
//                    column5.setPadding(35, 5, 0, 5);
//                    String str4 = place.createdOn;
//                    column5.setText(str4);
//                    column5.setTextColor(Color.BLACK);
//                    column5.setTextSize(rowTextSize);
//                    tr.addView(column5);

                    tr.setGravity(Gravity.CENTER);

                    lastLocationTable.addView(tr);

                    // add line below each row

                    lastLocationTable.addView(addLine());
                }
            }
        }
        catch(Exception e)
        {
            Log.d("FailedToShow", "Error parsing data " + e.toString());
            Toast.makeText(getApplicationContext(), "JsonArray fail", Toast.LENGTH_SHORT).show();
        }
    }

    private void showNoSavedLocationAvailableMessage()
    {
        TableRow tr = new TableRow(getApplicationContext());

        tr.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT));
        TextView column1 = new TextView(getApplicationContext());
        column1.setText("There are no saved locations available");
        column1.setPadding(100, 200, 0, 200);
        column1.setTextColor(Color.BLUE);
        column1.setTextSize(20);
        tr.addView(column1);
        lastLocationTable.addView(tr);
    }
}