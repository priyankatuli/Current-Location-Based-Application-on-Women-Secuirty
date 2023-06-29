package com.example.saveourwoman;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText SignInEmailID, SignInPasswordID;
    private Button SignInbtnID,ResetID;
    private TextView SignUptxtID;
    private ProgressBar progressbarID;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        this.setTitle("Sign In");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.color4)));

        mAuth = FirebaseAuth.getInstance();

        SignInEmailID = (EditText) findViewById(R.id.EmailId);
        SignInPasswordID = (EditText) findViewById(R.id.PasswordId);
        SignInbtnID = (Button) findViewById(R.id.ButtonId);
        SignUptxtID = (TextView) findViewById(R.id.textViewId);
        progressbarID = (ProgressBar) findViewById(R.id.progressId);
        ResetID=(Button) findViewById(R.id.ResetId);

        SignInbtnID.setOnClickListener(this);
        SignUptxtID.setOnClickListener(this);
        ResetID.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.ButtonId:

                UserLogin();
                break;

            case R.id.textViewId:

                Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(intent);
                break;
            case R.id.ResetId:
                UserReset();
                break;

        }
    }

    private void UserReset() {
        String email = SignInEmailID.getText().toString().trim();
        progressbarID.setVisibility(View.VISIBLE);
        mAuth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                progressbarID.setVisibility(View.GONE);
                Toast.makeText(SignInActivity.this, "Email Sent", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SignInActivity.this,e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void UserLogin() {

        //Edittext r maddhome email/password niye store kore ra
        // trim means kono space thakle ignore korbe

        String email = SignInEmailID.getText().toString().trim();
        String password = SignInPasswordID.getText().toString().trim();

        //Now Check email/password validity

        if (email.isEmpty()) {
            SignInEmailID.setError("Enter an email address");
            SignInEmailID.requestFocus();
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            SignInEmailID.setError("Enter a valid email address");
            SignInEmailID.requestFocus();
        }

        if (password.isEmpty()) {
            SignInPasswordID.setError("Enter a  password");
            SignInPasswordID.requestFocus();

        }
        if (password.length() < 8) {
            SignInPasswordID.setError("minimum length of a password should be 8");
            SignInPasswordID.requestFocus();
        }
        progressbarID.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressbarID.setVisibility(View.GONE);

                if(task.isSuccessful()) {
                    if (mAuth.getCurrentUser().isEmailVerified()) {

                        Intent intent = new Intent(getApplicationContext(), SecondActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);

                    } else {
                        Toast.makeText(getApplicationContext(), "Please verify your email", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(),"Error :" +task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}