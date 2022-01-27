package com.sm.sdk.yokke.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.sm.sdk.yokke.R;
import com.sm.sdk.yokke.activities.QrisActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainFragment extends Fragment {
    TextView tvDate;
    CardView btnQr;
    Calendar calendar = Calendar.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LayoutInflater lf = getActivity().getLayoutInflater();
        View view = lf.inflate(R.layout.fragment_main, container, false);

        tvDate = view.findViewById(R.id.tv_date);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE, dd-MM-yyyy");
        String datetime = simpleDateFormat.format(calendar.getTime());
        tvDate.setText(datetime);
        // Inflate the layout for this fragment

        btnQr = view.findViewById(R.id.btn_qr);
        btnQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), QrisActivity.class);
                startActivity(i);
            }
        });

        return view;
       }
}