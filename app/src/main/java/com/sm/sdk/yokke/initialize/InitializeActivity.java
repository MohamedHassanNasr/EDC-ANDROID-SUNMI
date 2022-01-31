package com.sm.sdk.yokke.initialize;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.sm.sdk.yokke.activities.MainActivity;
import com.sm.sdk.yokke.R;
import com.sm.sdk.yokke.activities.TransactionActivity;

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