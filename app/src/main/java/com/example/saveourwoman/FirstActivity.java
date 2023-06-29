package com.example.saveourwoman;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;

public class FirstActivity extends AppCompatActivity {

    private TextView txtID;
    LottieAnimationView lottie;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        getSupportActionBar().hide();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        txtID=(TextView)findViewById(R.id.txt);
        lottie=findViewById(R.id.animation);


        //to make it splash screen
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {

                Intent intent=new Intent(FirstActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        },5000)  ;
    }
}