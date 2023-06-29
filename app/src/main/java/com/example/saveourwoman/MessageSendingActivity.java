package com.example.saveourwoman;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class MessageSendingActivity extends AppCompatActivity implements ILocationService {

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0 ;
    Button sendBtn;
    String message;
    FirebaseFirestore db;
    List<ContactViewModel> savedContacts;
    List<String> numbers = new ArrayList<>();

    TableLayout contactTable;

    private LocationViewModel locationVM;

    private double lat, lng;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_sending);
        this.setTitle("Emergency Message");

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.color4)));

        sendBtn = (Button) findViewById(R.id.sendMessageAllId);

        sendBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                sendMessageToAll();
            }
        });

        contactTable = findViewById(R.id.contactTableForSendingMessage);

        locationVM = new LocationViewModel(this);
        db = FirebaseFirestore.getInstance();
        getSavedContactFromFirestore();
    }

    public void sendMessageToAll()
    {
        numbers = new ArrayList<>();

        if (savedContacts == null || savedContacts.size() == 0)
        {
            Toast.makeText(getApplicationContext(), "No contacts found to send message.",
                    Toast.LENGTH_LONG).show();
            return;
        }

        //separate numbers from all saved contacts
        for(ContactViewModel contact: savedContacts) {
            if (contact != null)
            {
                numbers.add(contact.phone);
            }
        }

        // for debugging purposes
        Log.d("Bachao", "Entered in sendMessageToAll()");

        // call for sending message to all the saved numbers
        sendSMSMessage();
    }

    public void sendMessageToSingleNumber(int index)
    {
        numbers = new ArrayList<>();
        // Only one selected number
        numbers.add(savedContacts.get(index).phone);

        sendSMSMessage();
    }

    protected void sendSMSMessage() {
        try {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.SEND_SMS)
                    != PackageManager.PERMISSION_GRANTED) {
               //if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.CALL_PHONE)){
                    //for debugging purposes
                  //  Log.d("Bachao", "Entered in getPermission");
               // } else {
                    // for debugging purposes
                    Log.d("Bachao", "Entered in sendSMSMessage()");
                    ActivityCompat.requestPermissions(this,
                            new String[]{android.Manifest.permission.SEND_SMS},
                            MY_PERMISSIONS_REQUEST_SEND_SMS);
                }
            else
            {
                GetAddress();
            }
        } catch (Exception e)
        {
            // for debugging purposes
            Log.d("Bachao", e.toString());
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // for debugging purposes
                    Log.d("Bachao", "Entered in onRequestPermissionResult()");
                    GetAddress();

                } else {
                    Toast.makeText(getApplicationContext(),
                            "SMS failed, please try again.", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }

    }

    private void GetAddress()
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
            Log.d("Bachao", "Exception at GetAddress : " + ex);
        }
    }

    private String GetMessage()
    {
        String address = null, Url = null;

        if (lat != -1.0 && lng != -1.0) // -1.0 value indicates that lat long are not set yet. Don't call to retrieve address
        {
            address = locationVM.GetAddressFromLatLong(lat, lng);
            Url = "https://www.google.com/maps/dir/?api=1&destination=" + String.format("%f,%f", lat, lng) + "\n" + address;
        }

        String message = "I'm in danger. ";

        if (address != null)
        {
            message += "Location: " + Url;
        }
        else
        {
            // If we don't get the address because of internet or google issue.
            message += "Please help. Out of internet.";
        }

        return message;
    }

    private void deliverMessages()
    {
        // for debugging purposes
        Log.d("Bachao", "Entered in deliverMessages()");
        try {
            message = GetMessage();
            SmsManager smsManager = SmsManager.getDefault();

            for (String number: numbers) {
                smsManager.sendTextMessage(number, null, message, null, null);
            }

            Toast.makeText(getApplicationContext(), "SMS sent.",
                    Toast.LENGTH_LONG).show();
        }
        catch (Exception e)
        {
            Log.d("Bachao", "Exception at deliverMessages() : Exception " + e);
        }
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

    // Retrieve saved contacts to show in the table
    private void getSavedContactFromFirestore()
    {
        FirebaseUser user = getLoggedInUser();

        if (user == null)
        {
            return ;
        }

        String userId = user.getUid();

        try {
            // Be careful with path naming convention next time.
            // This silly mistake costs you two sleepless night to find the root cause. Don't forget that.
            db.collection("contactCollections/emergency/list").document(userId)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists())
                            {
                                savedContacts = documentSnapshot.toObject(ContactListViewModel.class).contacts;

                                if (savedContacts.size() > 0) // If contacts are available show them in table
                                {
                                    viewContact();
                                }
                                else // otherwise show no contacts available message
                                {
                                    showNoContactsAvailableMessage();
                                }
                            }
                            else // otherwise show no contacts available message
                            {
                                showNoContactsAvailableMessage();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("Bachao", e.toString());
                            Toast.makeText(getApplicationContext(), "Failed to load contacts", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        catch (Exception e)
        {
            Log.d("RunTimeException", e.toString());
            Toast.makeText(getApplicationContext(), "Failed to load contacts", Toast.LENGTH_SHORT).show();
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

    private void viewContact()
    {
        try{

            boolean addHeader = true;
            int rowTextSize = 16;

            // Starting from -1 in order to add an extra row (Column names) in the table

            for(int i = -1; i < savedContacts.size(); i++){

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
                    header2.setText("Name");
                    header2.setTextColor(Color.BLUE);
                    tr.addView(header2);

                    TextView header3 = new TextView(getApplicationContext());
                    header3.setPadding(40, 5, 0, 5);
                    header3.setText("Phone");
                    header3.setTextColor(Color.BLUE);
                    header3.setTextSize(rowTextSize);
                    tr.addView(header3);

                    TextView header4 = new TextView(getApplicationContext());
                    header4.setPadding(35, 5, 0, 5);
                    header4.setText("Action");
                    header4.setTextColor(Color.BLUE);
                    header4.setTextSize(rowTextSize);
                    tr.addView(header4);

                    tr.setGravity(Gravity.CENTER);

                    contactTable.addView(tr);

                    // add line below heading

                    contactTable.addView(addLine());

                    addHeader = false;
                }
                else
                {
                    ContactViewModel contact = savedContacts.get(i);
                    final int in = i;

                    TextView column2 = new TextView(getApplicationContext());
                    column2.setPadding(10, 5, 0, 5);
                    column2.setTextSize(rowTextSize);
                    String str1 = contact.name;
                    column2.setText(str1);
                    column2.setTextColor(Color.BLACK);
                    tr.addView(column2);

                    TextView column3 = new TextView(getApplicationContext());
                    column3.setPadding(35, 5, 0, 5);
                    String str2 = contact.phone;
                    column3.setText(str2);
                    column3.setTextColor(Color.BLACK);
                    column3.setTextSize(rowTextSize);
                    tr.addView(column3);

                    // Delete button for each contacts
                    Button sendSms = new Button(getApplicationContext());
                    sendSms.setBackground(getDrawable(R.drawable.button_color));
                    //sendSms.setLayoutParams(new LinearLayout.LayoutParams(80, 15));
                    sendSms.setText("Send SMS");
                    sendSms.setTextColor(Color.WHITE);
                    sendSms.setPadding(10, 5, 0, 5);

                    sendSms.setTextSize(13);
                    sendSms.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            sendMessageToSingleNumber(in);
                        }
                    });
                    tr.addView(sendSms);

                    tr.setGravity(Gravity.CENTER);

                    contactTable.addView(tr);

                    // add line below each row

                    contactTable.addView(addLine());
                }
            }
        }
        catch(Exception e)
        {
            Log.d("FailedToShow", "Error parsing data " + e.toString());
            Toast.makeText(getApplicationContext(), "JsonArray fail", Toast.LENGTH_SHORT).show();
        }
    }
    private void showNoContactsAvailableMessage()
    {
        TableRow tr = new TableRow(getApplicationContext());

        tr.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT));
        TextView column1 = new TextView(getApplicationContext());
        column1.setText("There are no contacts available\nto show. Please add contacts first.");
        column1.setPadding(100, 200, 0, 200);
        column1.setTextColor(Color.BLUE);
        column1.setTextSize(20);
        tr.addView(column1);
        contactTable.addView(tr);
    }

    @Override
    public void NotifyAboutLocationSet(double latitude, double longitude) {
        lat = latitude;
        lng = longitude;

        // Latitude and longitude are successfully set, call deliver Message.

        deliverMessages();
    }
}