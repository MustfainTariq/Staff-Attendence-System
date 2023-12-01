package com.project.i200770_i200608_project;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

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

    GetPublicIP getPublicIP = new GetPublicIP();
     try {
         getPublicIP.execute().get();
     } catch (ExecutionException e) {
         throw new RuntimeException(e);
     } catch (InterruptedException e) {
         throw new RuntimeException(e);
     }
     String ip = getPublicIP.getIP();
     if(ip.equals("39.43.128.22")) {
         Toast.makeText(getContext(), "Network Verified!", Toast.LENGTH_LONG).show();
     }
     else {
         Toast.makeText(getContext(), "Network Not Verified!", Toast.LENGTH_LONG).show();
     }

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