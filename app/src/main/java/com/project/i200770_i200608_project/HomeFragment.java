package com.project.i200770_i200608_project;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class HomeFragment extends Fragment implements View.OnClickListener {

    Button checkinface;
    Button checkinfinger;
 @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_home,container,false);
    checkinface = (Button) view.findViewById(R.id.checkinface);
    checkinfinger = (Button) view.findViewById(R.id.checkinfinger);
    checkinface.setOnClickListener(this);
    checkinfinger.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.checkinfinger:
                Intent intent = new Intent(v.getContext(), BiometricScan.class);
                startActivity(intent);
                break;
            case R.id.checkinface:
                Intent intent1 = new Intent(v.getContext(), FaceScan.class);
                startActivity(intent1);
                break;
        }
    }
}