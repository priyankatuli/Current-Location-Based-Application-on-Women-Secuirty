package com.example.saveourwoman;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;

public class SelfDefenseActivity extends AppCompatActivity {

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_self_defense_trips);

        this.setTitle("Self Defense Tips And Tricks");

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.color4)));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // We don't need to call new intent. If we create new intent, open it, actually it will take us forward with new intent not backward.
        this.finish();

        return super.onOptionsItemSelected(item);
    }
}