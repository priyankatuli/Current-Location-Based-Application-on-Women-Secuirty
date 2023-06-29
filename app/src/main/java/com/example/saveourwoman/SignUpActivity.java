package com.example.saveourwoman;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText SignUpEmail, SignUpPassword, nameField, phoneField, ageField, dobField;
    private Button SignUpbtn;
    private TextView SignUptxt;
    private ProgressBar SignUpProgressBar;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private void initiateViewItems()
    {
        SignUpEmail = (EditText) findViewById(R.id.SignUpEmailId);
        SignUpPassword = (EditText) findViewById(R.id.SignUpPasswordId);
        nameField = (EditText) findViewById(R.id.signUpName);
        ageField = (EditText) findViewById(R.id.signUpAge);
        dobField = (EditText) findViewById(R.id.signUpDob);
        phoneField = (EditText) findViewById(R.id.signUpPhone);
        SignUpbtn = (Button) findViewById(R.id.SignUpButtonId);
        SignUptxt = (TextView) findViewById(R.id.SignUptextViewId);
        SignUpProgressBar = (ProgressBar) findViewById(R.id.SignUpprogressId);

        SignUpbtn.setOnClickListener(this);
        SignUptxt.setOnClickListener(this);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up2);

        this.setTitle("Sign Up");
        //actionbar e color
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.color4)));

        //initialize firebaseauth
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        //initiate text fields and buttons
        initiateViewItems();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.SignUpButtonId:
                UserRegister();
                break;

            case R.id.SignUptextViewId:
                Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
        }

    }

    private boolean isValidInput(String name, String dob, String age, String phone, String email, String password)
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

        if (email.isEmpty()) {
            SignUpEmail.setError("Enter an email address");
            SignUpEmail.requestFocus();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            SignUpEmail.setError("Enter a valid email address");
            SignUpEmail.requestFocus();
            return false;
        }

        if (password.isEmpty()) {
            SignUpPassword.setError("Enter a  password");
            SignUpPassword.requestFocus();
            return false;

        }

        if (password.length() < 8) {
            SignUpPassword.setError("minimum length of a password should be 8");
            SignUpPassword.requestFocus();
            return false;
        }

        return true;
    }

    private void UserRegister() {

        String email = SignUpEmail.getText().toString().trim();
        String password = SignUpPassword.getText().toString().trim();
        String name = nameField.getText().toString();
        String age = ageField.getText().toString();
        String dob = dobField.getText().toString();
        String phone = phoneField.getText().toString();

        //Now Check email/password validity

        if (!isValidInput(name, dob, age, phone, email, password))
        {
            return;
        }


        SignUpProgressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                SignUpProgressBar.setVisibility(View.GONE);

                mAuth.getCurrentUser().sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        if (task.isSuccessful()) {

                            Toast.makeText(getApplicationContext(),"Please verify your email",Toast.LENGTH_SHORT).show();
                            saveUserInfo(name, age, dob, phone, email);
                            Intent intent=new Intent(getApplicationContext(),SignInActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);

                        }
                        else if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                            Toast.makeText(getApplicationContext(), "User is already registered", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        //else{
                        //Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_SHORT).show();
                        //}
                    }
                });
            }

        });

    }

    public FirebaseUser getLoggedInUser()
    {

        if (mAuth == null)
        {
            return null;
        }

        return mAuth.getCurrentUser();
    }

    private void saveUserInfo(String name, String age, String dob, String phone, String email)
    {
        FirebaseUser user = getLoggedInUser();

        if (user == null)
        {
            return ;
        }

        String userId = user.getUid();

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
                            Log.d("UnableToAccessFirestore", "user details successfully saved");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("UnableToAccessFirestore", e.toString());
                            Toast.makeText(getApplicationContext(), "Failed to save user details", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        catch (Exception e)
        {
            Log.d("RunTimeException", e.toString());
            Toast.makeText(getApplicationContext(), "Failed to save user details", Toast.LENGTH_SHORT).show();
        }
    }

}