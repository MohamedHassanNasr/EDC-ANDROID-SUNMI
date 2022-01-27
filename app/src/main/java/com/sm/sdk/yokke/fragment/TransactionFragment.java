package com.sm.sdk.yokke.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.sm.sdk.yokke.R;
import com.sm.sdk.yokke.activities.SaleActivity;
import com.sm.sdk.yokke.activities.VoidActivity;

public class TransactionFragment extends Fragment {
    CardView btnSale, btnVoid;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LayoutInflater lf = getActivity().getLayoutInflater();
        View view = lf.inflate(R.layout.fragment_transaction, container, false);

        btnSale = view.findViewById(R.id.btn_sale);
        btnSale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SaleActivity.class);
                startActivity(intent);
            }
        });

        btnVoid = view.findViewById(R.id.btn_void);
        btnVoid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), VoidActivity.class);
                startActivity(intent);
            }
        });

        return view;    }
}