package com.sm.sdk.yokke.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.sm.sdk.yokke.R;
import com.sm.sdk.yokke.fragment.AdminFragment;
import com.sm.sdk.yokke.fragment.InitializeFragment;
import com.sm.sdk.yokke.fragment.MainFragment;
import com.sm.sdk.yokke.fragment.TransactionFragment;
import com.sm.sdk.yokke.initialize.InitializeActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity{
    BottomNavigationView bottomNavigationView;
    private static final int TIME_INTERVAL = 2000;
    private long backPressed;
    public static final String TAG_EXIT = "EXIT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.splashScreenTheme);
        setContentView(R.layout.activity_main);

        //bottom navigation
//        memulai dgn main fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MainFragment()).commit();

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            switch (item.getItemId()){
                case R.id.nav_main:
                    selectedFragment = new MainFragment();
                    break;
                case R.id.nav_transaction:
                    selectedFragment = new TransactionFragment();
                    break;
                case R.id.nav_admin:
                    selectedFragment = new AdminFragment();
                    break;
                case R.id.nav_init:
                    selectedFragment = new InitializeFragment();
                    break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
            return true;
        });
    }

    @Override
    public void onBackPressed(){
        if (backPressed + TIME_INTERVAL > System.currentTimeMillis()){
            super.onBackPressed();
            return;
        }  else {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MainFragment()).commit();
            bottomNavigationView.setSelectedItemId(R.id.nav_main);
            Toast.makeText(getBaseContext(), "Press Back again to exit", Toast.LENGTH_SHORT).show(); }
        backPressed = System.currentTimeMillis();
    }

}