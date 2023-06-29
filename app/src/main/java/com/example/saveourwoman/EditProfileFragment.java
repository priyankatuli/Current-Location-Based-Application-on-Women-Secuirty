package com.example.saveourwoman;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditProfileFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private EditText nameField, phoneField, ageField, dobField;
    private Button updateProfileBtn;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private void initiateViewItems(View view)
    {
        nameField = (EditText) view.findViewById(R.id.editName);
        ageField = (EditText) view.findViewById(R.id.editAge);
        dobField = (EditText) view.findViewById(R.id.editDob);
        phoneField = (EditText) view.findViewById(R.id.editPhone);
        updateProfileBtn = (Button) view.findViewById(R.id.editInfoBtn);
        updateProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserDetails();
            }
        });
    }

    public EditProfileFragment() {
        // Required empty public constructor
    }

    public static EditProfileFragment newInstance(String param1, String param2) {
        EditProfileFragment fragment = new EditProfileFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        db = FirebaseFirestore.getInstance();

        initiateViewItems(view);

        // Get existing saved user details
        getUserDetails();
        return view;
    }

    //Populate existing info in the edit text fields
    private void populateProfileDetails(User user)
    {
        if (user == null)
        {
            return ;
        }
        nameField.setText(user.name);
        ageField.setText(user.age);
        dobField.setText(user.dateOfBirth);
        phoneField.setText(user.phone);
    }

    // Check validity of the user inputs
    private boolean isValidInput(String name, String dob, String age, String phone)
    {
        if (name.isEmpty()) {
            nameField.setError("Enter name");
            nameField.requestFocus();
            return false;
        }

        if (dob.isEmpty()) {
            dobField.setError("Enter date of birth");
            dobField.requestFocus();
            return false;
        }

        if (age.isEmpty()) {
            ageField.setError("Enter age");
            ageField.requestFocus();
            return false;
        }

        if (phone.isEmpty()) {
            phoneField.setError("Enter phone number");
            phoneField.requestFocus();
            return false;
        }

        return true;
    }
    private void updateUserDetails()
    {
        String name = nameField.getText().toString();
        String age = ageField.getText().toString();
        String dob = dobField.getText().toString();
        String phone = phoneField.getText().toString();

        //Now Check inputs validity

        if (!isValidInput(name, dob, age, phone))
        {
            return;
        }

        FirebaseUser user = getLoggedInUser();

        if (user == null)
        {
            return ;
        }

        String userId = user.getUid();
        String email = user.getEmail();

        try {

            User userDetails = new User(name, email, phone, age, dob);

            Map<String, Object> data = new HashMap<>();

            data.put("user", userDetails);

            // Be careful with path naming convention next time.
            // This silly mistake costs you two sleepless night to find the root cause. Don't forget that.
            db.collection("userInfoCollections/details/uid").document(userId)
                    .set(data)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.d("UnableToAccessFirestore", "user details successfully updated");
                            Toast.makeText(getContext(), "user details successfully updated", Toast.LENGTH_SHORT).show();
                            setFragment(R.id.fragmentContainer, new profileFragment());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("UnableToAccessFirestore", e.toString());
                            Toast.makeText(getContext(), "Failed to update user details", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        catch (Exception e)
        {
            Log.d("RunTimeException", e.toString());
            Toast.makeText(getContext(), "Failed to update user details", Toast.LENGTH_SHORT).show();
        }

    }

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
}