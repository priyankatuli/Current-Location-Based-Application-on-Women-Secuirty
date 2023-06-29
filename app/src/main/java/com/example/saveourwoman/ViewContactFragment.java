package com.example.saveourwoman;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewContactFragment extends Fragment implements View.OnClickListener{

    //region variable declaration
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private FirebaseFirestore db;
    private List<ContactViewModel> savedContacts;
    TableLayout contactTable;

    //endregion

    //region method initialization
    public ViewContactFragment() {
        // Required empty public constructor
    }
    public static ViewContactFragment newInstance(String param1, String param2) {
        ViewContactFragment fragment = new ViewContactFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        db = FirebaseFirestore.getInstance();
        getSavedContactFromFirestore();
    }

    //endregion

    // initiate view
    private void initiateCardsListener(View view)
    {
        contactTable = view.findViewById(R.id.contactTable);
        contactTable.removeAllViewsInLayout();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_view_contact, container, false);
        initiateCardsListener(view);

        //viewContact();
        return view;
    }

    // Onclick listener event

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.addContactBtn)
        {
            //viewContact();
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
                            Log.d("UnableToAccessFirestore", e.toString());
                            Toast.makeText(getContext(), "Failed to load contacts", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        catch (Exception e)
        {
            Log.d("RunTimeException", e.toString());
            Toast.makeText(getContext(), "Failed to load contacts", Toast.LENGTH_SHORT).show();
        }
    }

    private View addLine()
    {
        final View vline = new View(getContext());
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

                TableRow tr = new TableRow(getContext());

                tr.setLayoutParams(new TableRow.LayoutParams(
                        TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.WRAP_CONTENT));
                tr.setMinimumHeight(100);

                // this will be executed once and will add header
                if(addHeader){

                    TextView header1 = new TextView(getContext());
                    header1.setText("SL");
                    header1.setPadding(10, 5, 0, 5);
                    header1.setTextColor(Color.BLUE);
                    header1.setTextSize(rowTextSize);
                    tr.addView(header1);

                    TextView header2 = new TextView(getContext());
                    header2.setPadding(20, 5, 0, 5);
                    header2.setTextSize(rowTextSize);
                    header2.setText("Name");
                    header2.setTextColor(Color.BLUE);
                    tr.addView(header2);

                    TextView header3 = new TextView(getContext());
                    header3.setPadding(40, 5, 0, 5);
                    header3.setText("Phone");
                    header3.setTextColor(Color.BLUE);
                    header3.setTextSize(rowTextSize);
                    tr.addView(header3);

                    TextView header4 = new TextView(getContext());
                    header4.setPadding(35, 5, 0, 5);
                    header4.setText("Delete");
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
                    TextView column1 = new TextView(getContext());
                    String str = String.valueOf(i + 1);
                    column1.setText(str);
                    column1.setPadding(20, 5, 0, 5);
                    column1.setTextColor(Color.BLACK);
                    column1.setTextSize(rowTextSize);
                    tr.addView(column1);

                    TextView column2 = new TextView(getContext());
                    column2.setPadding(10, 5, 0, 5);
                    column2.setTextSize(rowTextSize);
                    String str1 = contact.name;
                    column2.setText(str1);
                    column2.setTextColor(Color.BLACK);
                    tr.addView(column2);

                    TextView column3 = new TextView(getContext());
                    column3.setPadding(35, 5, 0, 5);
                    String str2 = contact.phone;
                    column3.setText(str2);
                    column3.setTextColor(Color.BLACK);
                    column3.setTextSize(rowTextSize);
                    tr.addView(column3);

                    // Delete button for each contacts
                    ImageView deleteLogo = new ImageView(getContext());
                    deleteLogo.setImageResource(R.drawable.baseline_delete_24);

                    deleteLogo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            deleteContact(in);
                        }
                    });
                    tr.addView(deleteLogo);

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
            Toast.makeText(getContext(), "JsonArray fail", Toast.LENGTH_SHORT).show();
        }

    }

    private void showNoContactsAvailableMessage()
    {
        TableRow tr = new TableRow(getContext());

        tr.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT));
        TextView column1 = new TextView(getContext());
        column1.setText("There are no contacts available\nto show. Please add contacts first.");
        column1.setPadding(100, 200, 0, 200);
        column1.setTextColor(Color.BLUE);
        column1.setTextSize(20);
        tr.addView(column1);
        contactTable.addView(tr);
    }

    private void refreshPage(int id, Fragment fragment)
    {
        getParentFragmentManager()
                .beginTransaction()
                .replace(id, fragment)
                .commit();
    }

    private void deleteContact(int index)
    {
        List<ContactViewModel> newContactList = new ArrayList<>();

        // Logic: store all the contacts again except the deleted one
        // It's the worst solution but the easiest one.
        // This worst solution chosen to avoid unwanted complexity

        for (int i = 0; i < savedContacts.size(); i++)
        {
            if (i != index)
            {
                newContactList.add(savedContacts.get(i));
            }
        }

        FirebaseUser user = getLoggedInUser();

        if (user == null)
        {
            return ;
        }

        String userId = user.getUid();

        try {
            Map<String, Object> data = new HashMap<>();

            data.put("contacts", newContactList);

            // Be careful with path naming convention next time.
            // This silly mistake costs you two sleepless night to find the root cause. Don't forget that.

            db.collection("contactCollections/emergency/list").document(userId)
                    .set(data)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.d("DEBUG", "Contact Successfully Deleted");
                            Toast.makeText(getContext(), "Contact Successfully Deleted", Toast.LENGTH_SHORT).show();
                            refreshPage(R.id.fragmentContainer, new ViewContactFragment());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("UnableToAccessFirestore", e.toString());
                            Toast.makeText(getContext(), "Failed to delete contact", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        catch (Exception e)
        {
            // For debugging purposes
            Log.d("RunTimeException", e.toString());
        }
    }

}