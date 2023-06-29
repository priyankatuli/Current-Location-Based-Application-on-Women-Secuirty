package com.example.saveourwoman;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class EmergencyCallingActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_PHONE_CALL = 47 ;
    Button sendBtn;
    String message;
    FirebaseFirestore db;
    List<ContactViewModel> savedContacts;
    List<String> numbers = new ArrayList<>();

    TableLayout contactTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_calling);
        this.setTitle("Emergency Call");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.color4)));

        contactTable = findViewById(R.id.contactTableForEmergencyCalling);

        db = FirebaseFirestore.getInstance();
        getSavedContactFromFirestore();
    }

    private boolean checkCallingPermission()
    {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
           // if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                   // android.Manifest.permission.CALL_PHONE)) {
               // // for debugging purposes
               // Log.d("Bachao", "Entered in getPermission");
            //} else {
                // for debugging purposes
                //Log.d("Bachao", "Entered in sendSMSMessage()");

                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.CALL_PHONE},
                        MY_PERMISSIONS_REQUEST_PHONE_CALL);
            }

            //return false;

       // }
        return true;
    }

    public void makeCallToSingleNumber(int index)
    {
        // saved contacts are empty, do nothing
        if (savedContacts.size() == 0)
        {
            return ;
        }

        boolean flag = checkCallingPermission();

        if (flag == false)
        {
            return ;
        }

        // Make phone call to the number using Action_Call
        String number = savedContacts.get(index).phone;

        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + number));
        Toast.makeText(getApplicationContext(), "Calling...", Toast.LENGTH_SHORT).show();

        if (ActivityCompat.checkSelfPermission(EmergencyCallingActivity.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)
        {
            Toast.makeText(getApplicationContext(), "Entered in get permission", Toast.LENGTH_LONG).show();
           return ;
        }

        startActivity(callIntent);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_PHONE_CALL: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // for debugging purposes
                    //Log.d("Bachao", "Entered in onRequestPermissionResult()");
                    Toast.makeText(getApplicationContext(),
                            "Permission Granted Call again.", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(getApplicationContext(),
                            "Making call failed, please try again.", Toast.LENGTH_LONG).show();
                    return;
                }
            }
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
                    header2.setGravity(Gravity.CENTER);
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
                    column2.setGravity(Gravity.CENTER);
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

                    // Call button for each contacts
                    ImageView makeCall = new ImageView(getApplicationContext());
                    makeCall.setImageResource(R.drawable.telephonecall_30x30);
                    makeCall.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            makeCallToSingleNumber(in);
                        }
                    });
                    tr.addView(makeCall);

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
}