package com.project.i200770_i200608_project;
import java.time.LocalDate;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.biometric.BiometricPrompt;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class HomeFragment extends Fragment implements View.OnClickListener {

    Button checkinface;

    Button checkinfinger;
    Button checknetwork;
    TextView NetworkStatus;

    Switch switch1;

 @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_home,container,false);
    checkinface = (Button) view.findViewById(R.id.checkinface);
    checkinfinger = (Button) view.findViewById(R.id.checkinfinger);
    checknetwork = (Button) view.findViewById(R.id.checkstatus);
    NetworkStatus = (TextView) view.findViewById(R.id.networkstatus);
    switch1 = (Switch) view.findViewById(R.id.switch1);
    checkinface.setOnClickListener(this);
    checkinfinger.setOnClickListener(this);
    checknetwork.setOnClickListener(this);



        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.checkinfinger:
                if(NetworkStatus.getText().toString().equals("Connected")) {
                    Boolean switchState = switch1.isChecked();
                    if(switchState)
                    {
                        Bundle bundle = new Bundle();
                        bundle.putBoolean("switchState", true);
                        Intent intent = new Intent(v.getContext(), BiometricScan.class);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                    else{
                        Bundle bundle = new Bundle();
                        bundle.putBoolean("switchState", false);
                        Intent intent = new Intent(v.getContext(), BiometricScan.class);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }

                }else {
                    Toast.makeText(getContext(), "Network Not Verified!", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.checkinface:
                Intent intent1 = new Intent(v.getContext(), FaceScan.class);
                startActivity(intent1);
                break;
             case R.id.checkstatus:
                 if(networkStatus())
                 {
                        NetworkStatus.setText("Connected");
                        NetworkStatus.setTextColor(Color.parseColor("#FF4CAF50"));
                    }
                    else
                    {
                        NetworkStatus.setText("Not Connected!");
                 }
                 break;
        }
    }

    public Boolean networkStatus() {
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
         Toast.makeText(getContext(), "Network Verified!", Toast.LENGTH_SHORT).show();
         return true;
     }
     else {
         return false;
     }
 }


}