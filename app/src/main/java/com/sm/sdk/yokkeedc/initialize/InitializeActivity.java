package com.sm.sdk.yokkeedc.initialize;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.sm.sdk.yokkeedc.MainActivity;
import com.sm.sdk.yokkeedc.MtiApplication;
import com.sm.sdk.yokkeedc.R;
import com.sm.sdk.yokkeedc.database.DatabaseHelper;
import com.sm.sdk.yokkeedc.models.EDCParam;
import com.sm.sdk.yokkeedc.models.EMVTag;
import com.sm.sdk.yokkeedc.transaction.TransactionActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InitializeActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initialize);

        Button btnInitProc =findViewById(R.id.btnInit);

        btnInitProc.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(getBaseContext(),MainCobaActivity.class);
//                startActivity(intent);
                Toast.makeText(getApplicationContext(), "Processing..", Toast.LENGTH_SHORT).show();

                if (InitProcess.initProc(InitializeActivity.this) == 1) {
                    Intent intent = new Intent(getBaseContext(),InitFinishActivity.class);
                    startActivity(intent);
                }
                else {
                    Intent intent = new Intent(getBaseContext(), MainActivity.class);
                    startActivity(intent);
                }
            }
        });


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_init);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_main:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.nav_transaction:
                        startActivity(new Intent(getApplicationContext(), TransactionActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.nav_init:
                        return true;
                }
                return false;
            }
        });
    }
}