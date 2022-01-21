package com.sm.sdk.yokkeedc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.sm.sdk.yokkeedc.initialize.InitializeActivity;
import com.sm.sdk.yokkeedc.transaction.TransactionActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity{
    TextView tv_date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE, dd-MM-yyyy");
        tv_date = findViewById(R.id.tv_date);
        String dateTime = simpleDateFormat.format(calendar.getTime());

        tv_date.setText(dateTime);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_main);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (!MtiApplication.app.isConnectPaySDK()) {
                    MtiApplication.app.bindPaySDKService();

                }
                switch (item.getItemId()) {
                    case R.id.nav_main:
                        return true;
                    case R.id.nav_transaction:
                        startActivity(new Intent(getApplicationContext(), TransactionActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.nav_init:
                        startActivity(new Intent(getApplicationContext(), InitializeActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });
    }

}