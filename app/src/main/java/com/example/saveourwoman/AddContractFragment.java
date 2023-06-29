package com.example.saveourwoman;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.type.DateTime;

import java.security.spec.ECField;
import java.text.DateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class AddContractFragment extends Fragment implements View.OnClickListener{
    //region Variable declaration
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private List<ContactViewModel> contacts;
    private Map<String, Object> data;

    private EditText contactName, contactNumber;
    private FirebaseFirestore db;
    private List<ContactViewModel> savedContacts;

    //endregion

    // region method initialization
    public AddContractFragment() {
        // Required empty public constructor
    }
    public static AddContractFragment newInstance(String param1, String param2) {
        AddContractFragment fragment = new AddContractFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private void initializeActions(View view)
    {
        Button addContactBtn = view.findViewById(R.id.addContactBtn);
        contactName = view.findViewById(R.id.contactName);
        contactNumber = view.findViewById(R.id.contactNumber);

        addContactBtn.setOnClickListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_contract, container, false);

        initializeActions(view);
        return view;
    }

    //endregion

    // Onclick listener event

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.addContactBtn)
        {
            getPreviousSavedContact();
        }
    }

    // Checks if name and number are valid
    private boolean validateInputs(String name, String number)
    {
        if (name == null || name.trim() == "" )
        {
            Toast.makeText(getContext(), "Please enter valid name", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (number == null)
        {
            Toast.makeText(getContext(), "Please enter valid number", Toast.LENGTH_SHORT).show();
            return false;
        }

        if ((number.length() == 11 && number.startsWith("01")) || (number.length() == 14 && number.startsWith("+8801")) )
        {
            return true;
        }

        Toast.makeText(getContext(), "Please enter valid number. Number should start with 01 or +8801", Toast.LENGTH_SHORT).show();
        //Toast.makeText(getContext(), "Please enter valid number." + number.substring(0, 2) + number.length(), Toast.LENGTH_SHORT).show();

        return false;
    }

    private void getPreviousSavedContact()
    {
        FirebaseUser user = getLoggedInUser();

        if (user == null)
        {
            return ;
        }

        String userId = user.getUid();
        try
        {
            // Be careful with path naming convention next time.
            // This silly mistake costs you two sleepless night to find the root cause. Don't forget that.
            Log.d("DEBUG", "Before of calling get contact" + userId);
            db.collection("contactCollections/emergency/list").document(userId)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            try {
                                // Bug was here. If document does not exist we didn't call to addContactToFireStore method before
                                // For a new user, obviously document isn't created yet. That's why contact wasn't getting saved.
                                if (documentSnapshot.exists())
                                {
                                    // For debugging purposes
                                    Log.d("DEBUG", "Before converting contacts. ");
                                    savedContacts = documentSnapshot.toObject(ContactListViewModel.class).contacts;
                                }

                                // Now it will call add contacts regardless of document already exists or not.
                                addContactToFireStore();
                            }
                            catch (Exception ex)
                            {
                                // For debugging purposes
                                Log.d("DEBUG", "Exception getting contacts. " + ex.toString());
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // For debugging purposes
                            Log.d("DEBUG", "Exception failure listener. " + e.toString());
                        }
                    });
        }
        catch (Exception ex)
        {
            Log.d("DEBUG", "Exception exiting get contacts. " + ex.toString());
            Toast.makeText(getContext(), "Failed to access Firestore. get contact!", Toast.LENGTH_SHORT).show();
        }

    }

    private void addContactToFireStore()
    {
        Log.d("DEBUG", "Entered into addContactToFireStore");
        String name = contactName.getText().toString();
        String number = contactNumber.getText().toString();

        if(!validateInputs(name, number))
        {
            return;
        }

        try{

            FirebaseUser user = getLoggedInUser();

            if (user == null)
            {
                return ;
            }

            String userId = user.getUid();

            if (savedContacts == null)
            {
                savedContacts = new ArrayList<ContactViewModel>();
            }

            try {
                data = new HashMap<>();

                savedContacts.add(new ContactViewModel(name, number, getCurrentUtcTime()));
                data.put("contacts", savedContacts);
                Log.d("DEBUG", "Ei porjonto Kaj kore");

                // Be careful with path naming convention next time.
                // This silly mistake costs you two sleepless night to find the root cause. Don't forget that.
                db.collection("contactCollections/emergency/list").document(userId)
                        .set(data)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d("DEBUG", "Contact Successfully Added");
                                Toast.makeText(getContext(), "Contact Successfully Added", Toast.LENGTH_SHORT).show();
                                contactName.setText("");
                                contactNumber.setText("");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("UnableToAccessFirestore", e.toString());
                                Toast.makeText(getContext(), "Failed to add contact", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
            catch (Exception e)
            {
                Log.d("Bachao", e.toString());  // Got you silly mistake! Paliye jabe kothay!
            }

            //Log.d("DEBUG", "Ekahne Kaj kore na. Something erros are happening between these two debug messages");

        }
        catch (Exception ex)
        {
            // Notify user of unsuccessful attempt of saving emergency contact
            Log.d("DEBUG", "Entered into exception addContactToFireStore");
            Toast.makeText(getContext(), "Failed to add contact", Toast.LENGTH_SHORT).show();
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

    private String getCurrentUtcTime()
    {
        DateFormat df = DateFormat.getDateInstance();
        df.setTimeZone(TimeZone.getTimeZone("gmt"));
        String gmtTime = df.format(new Date()) + " UTC + 00";

        return gmtTime;
    }
}