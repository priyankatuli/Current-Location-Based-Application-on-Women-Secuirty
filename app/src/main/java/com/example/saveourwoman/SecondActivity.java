package com.example.saveourwoman;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class SecondActivity extends AppCompatActivity {

    private CardView FirstId,SecondId,ThirdId,FourthId,FifthId, LogOutBtnView, DefenseTricksView;
    private ImageView backID;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle drawerToggle;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //Toast.makeText(SecondActivity.this, "On option Clicked" + item.getItemId(), Toast.LENGTH_SHORT).show();
        if (drawerToggle.onOptionsItemSelected(item))
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //Set fragment based on the option clicked on navigation drawer menu
    private void setFragment(int id, Fragment fragment)
    {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(id, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void setNavigationMenu()
    {
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        drawerToggle = new ActionBarDrawerToggle(this,
                drawerLayout,
                R.string.open,
                R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setFragment(R.id.fragmentContainer, new home());
        navigationView.setCheckedItem(R.id.home);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                //Toast.makeText(SecondActivity.this, "Entered into onNavigationItemSelected", Toast.LENGTH_SHORT).show();

                switch (item.getItemId())
                {
                    case R.id.home:
                    {
                        //Toast.makeText(SecondActivity.this, "Home Clicked", Toast.LENGTH_SHORT).show();
                        setFragment(R.id.fragmentContainer, new home());
                        break;
                    }
                    case R.id.profile:
                    {
                        //Toast.makeText(SecondActivity.this, "profile Clicked", Toast.LENGTH_SHORT).show();
                        setFragment(R.id.fragmentContainer, new profileFragment());
                        break;
                    }
                    case R.id.contacts:
                    {
                        //Toast.makeText(SecondActivity.this, "contacts Clicked", Toast.LENGTH_SHORT).show();
                        setFragment(R.id.fragmentContainer, new emergency_contacts_fragment());
                        break;
                    }
                    case R.id.about:
                    {
                        //Toast.makeText(SecondActivity.this, "about Clicked", Toast.LENGTH_SHORT).show();
                        setFragment(R.id.fragmentContainer, new about());
                        break;
                    }
                    case R.id.logout:
                    {
                        //Toast.makeText(SecondActivity.this, "logout Clicked", Toast.LENGTH_SHORT).show();

                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(SecondActivity.this, SignInActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        break;
                    }
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        this.setTitle("Main Menu");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.color4)));
        setNavigationMenu();

        //Note: Previous secondActivity views and functionalities developed by Tuli have been moved to home_fragment.xml and home.java

    }

    //@Override
    //public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //if(item.getItemId()==android.R.id.home){
            //Intent intent=new Intent(getApplicationContext(),MainActivity.class);
           // startActivity(intent);
           // finish();
        //}
       // return super.onOptionsItemSelected(item);
    //}

    @Override
    public void onBackPressed() {
        //Toast.makeText(SecondActivity.this, "Back Clicked", Toast.LENGTH_SHORT).show();
        if (getSupportFragmentManager().getBackStackEntryCount() > 0)
        {
            getSupportFragmentManager().popBackStack();
        }
        else {
            super.onBackPressed();
        }
    }
}