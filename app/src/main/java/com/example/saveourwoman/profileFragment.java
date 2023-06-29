package com.example.saveourwoman;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class profileFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    private TextView name, phone, age, email, dob;
    Button updateBtn;
    FirebaseFirestore db;

    public profileFragment() {
        // Required empty public constructor
    }
    public static profileFragment newInstance(String param1, String param2) {
        profileFragment fragment = new profileFragment();
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
        getUserDetails();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        name = view.findViewById(R.id.profileName);
        phone = view.findViewById(R.id.profilePhone);
        age = view.findViewById(R.id.profileAge);
        dob = view.findViewById(R.id.profileDob);
        email = view.findViewById(R.id.profileEmail);
        updateBtn = view.findViewById(R.id.updateProfileBtn);
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(R.id.fragmentContainer, new EditProfileFragment());
            }
        });
        return view;
    }

    // Will replace current fragment with editProfileFragment
    private void setFragment(int id, Fragment fragment)
    {
        getParentFragmentManager()
                .beginTransaction()
                .replace(id, fragment)
                .addToBackStack(null)
                .commit();
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

    private void getUserDetails()
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

            db.collection("userInfoCollections/details/uid").document(userId)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists())
                            {
                                try {
                                    UserMapper userMapper = documentSnapshot.toObject(UserMapper.class);
                                    if (userMapper != null)
                                    {
                                        User userInfo = userMapper.user;
                                        populateProfileDetails(userInfo);
                                    }

                                }
                                catch (Exception e)
                                {
                                    Log.d("DEBUG", e.toString());
                                }

                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("UnableToAccessFirestore", e.toString());
                            Toast.makeText(getContext(), "Failed to fetch user details", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        catch (Exception e)
        {
            Log.d("RunTimeException", e.toString());
            Toast.makeText(getContext(), "Failed to fetch user details", Toast.LENGTH_SHORT).show();
        }
    }

    private void populateProfileDetails(User user)
    {
        try {
            if (user == null)
            {
                return ;
            }

            name.setText(user.name);
            age.setText(user.age);
            dob.setText(user.dateOfBirth);
            email.setText(user.email);
            phone.setText(user.phone);
        }
        catch (Exception e)
        {
            Log.d("DEBUG", e.toString());
        }

    }
}