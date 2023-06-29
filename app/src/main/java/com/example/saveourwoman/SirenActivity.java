package com.example.saveourwoman;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.TextView;

public class SirenActivity extends AppCompatActivity {

    private TextView TextID;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_siren2);


        getSupportActionBar().hide();

        TextID = (TextView) findViewById(R.id.TxtID);

        startsiren();

    }


    public void startsiren(){
        mediaPlayer=MediaPlayer.create(getApplicationContext(),R.raw.songg);
        mediaPlayer.start();
        mediaPlayer.setLooping(true);
        mediaPlayer.setVolume(1f,1f);

    }
    //public void setVolumn(float left,float right){
    //mediaPlayer.setVolume(1f,1f);
    //}
    @Override
    public void onBackPressed() {

        mediaPlayer.stop();
        super.onBackPressed();
    }

}