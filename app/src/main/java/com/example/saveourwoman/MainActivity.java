package com.example.saveourwoman;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button LoginID;
    private Button RegisterID;
    private Button SkipID;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.setTitle("");
        getSupportActionBar().hide();
        //getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.color4)));

        LoginID = (Button) findViewById(R.id.LoginID);
        RegisterID = (Button) findViewById(R.id.RegisterID);
        //SkipID = (Button) findViewById(R.id.SkipID);

        //-----------Auto Log in section starts-----------
        // This part will redirect user to the Main Menu i.e second activity layout.
        // If one logged In before, SHE does not require to log in again

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser loggedInUser = mAuth.getCurrentUser();
        if (loggedInUser != null && loggedInUser.isEmailVerified())
        {
            // if user exists and email verified redirect to main menu

            Intent intent = new Intent(getApplicationContext(), SecondActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            Toast.makeText(getApplicationContext(), "Signed In as " + loggedInUser.getEmail(), Toast.LENGTH_SHORT).show();
        }
        else if (loggedInUser != null && loggedInUser.isEmailVerified() == false)
        {
            // If not verified, send verification mail again

            loggedInUser.sendEmailVerification()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(getApplicationContext(), "Verification mail sent to " + loggedInUser.getEmail() , Toast.LENGTH_SHORT).show();
                            Toast.makeText(getApplicationContext(), "Please verify and login again." , Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Failed to send email verification." , Toast.LENGTH_SHORT).show();
                        }
                    });

            LoginID.setOnClickListener(this);
            RegisterID.setOnClickListener(this);
        }
        else
        {
            // Otherwise stay sign in / sign up page

            LoginID.setOnClickListener(this);
            RegisterID.setOnClickListener(this);
            //SkipID.setOnClickListener(this);
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.LoginID:
                if (mAuth.getCurrentUser() != null && mAuth.getCurrentUser().isEmailVerified())
                {
                    Toast.makeText(MainActivity.this,"Login Successful",Toast.LENGTH_SHORT);
                    Intent intent = new Intent(getApplicationContext(), SecondActivity.class);
                    startActivity(intent);
                }
                else
                {
                    Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                    startActivity(intent);
                }
                break;

            case R.id.RegisterID:
                Intent intent1 = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(intent1);
                break;
        }
    }
}