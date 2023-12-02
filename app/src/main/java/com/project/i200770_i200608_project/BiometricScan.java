package com.project.i200770_i200608_project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class BiometricScan extends AppCompatActivity {
    FirebaseFirestore fstore = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    Executor executor = Executors.newFixedThreadPool(2);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_biometric_scan);



        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric login")
                .setSubtitle("Use your fingerprint to login")
                .setNegativeButtonText("Cancel")
                .build();

        BiometricPrompt biometricPrompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                System.out.println("Auth Successfull");
                @SuppressLint({"NewApi", "LocalSuppress"}) int year = LocalDate.now().getYear();
                @SuppressLint({"NewApi", "LocalSuppress"}) int month = LocalDate.now().getMonthValue();
                @SuppressLint({"NewApi", "LocalSuppress"}) int day = LocalDate.now().getDayOfMonth();
                String Dateselected = Integer.toString(day) + Integer.toString(month) + Integer.toString(year);
                System.out.println(Dateselected);
                DocumentReference documentReference = (DocumentReference) fstore.collection("users").document(mAuth.getUid()).collection("Calendar").
                        document(Dateselected);
                documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {

                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                        Bundle bundle = getIntent().getExtras();

                        Boolean switchState = bundle.getBoolean("switchState");
                        if(!switchState)
                        {
                            DocumentReference documentReference = fstore.collection("users").document(mAuth.getUid()).collection("Calendar").
                                    document(Dateselected);

                            documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                @SuppressLint("NewApi")
                                @Override
                                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                                    if(value.exists()){
                                        if(!value.getString("checkin").equals("No Record")){
                                            Toast.makeText(BiometricScan.this, "Already Checked In!", Toast.LENGTH_SHORT).show();
                                        }
                                        else {
                                            documentReference.update("checkin", LocalTime.now().toString());
                                            Toast.makeText(BiometricScan.this, "Check In Successful", Toast.LENGTH_SHORT).show();
                                        }
                                        Intent intent = new Intent(BiometricScan.this, HomeScreen.class);
                                        startActivity(intent);
                                    }
                                    else{

                                        Map<String,Object> user = new HashMap<>();
                                        user.put("checkin",LocalTime.now().toString());
                                        user.put("checkout","No Record");
                                        user.put("Event","No Event");
                                        documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Log.d("TAG","onSuccess: user profile is created for "+mAuth.getUid());
                                            }
                                        });
                                        Toast.makeText(BiometricScan.this, "Check In Successful", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(BiometricScan.this, HomeScreen.class);
                                        startActivity(intent);
                                    }
                                }
                            });
                        }
                        else if (switchState){
                            DocumentReference documentReference = fstore.collection("users").document(mAuth.getUid()).collection("Calendar").
                                    document(Dateselected);

                            documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                @SuppressLint("NewApi")
                                @Override
                                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                                    if(value.exists()){
                                        if(!value.getString("checkout").equals("No Record")){
                                            Toast.makeText(BiometricScan.this, "Already Checked Out!", Toast.LENGTH_SHORT).show();
                                        }
                                        else {
                                            documentReference.update("checkout", LocalTime.now().toString());
                                            Toast.makeText(BiometricScan.this, "Check Out Successful", Toast.LENGTH_SHORT).show();
                                        }
                                        Intent intent = new Intent(BiometricScan.this, HomeScreen.class);
                                        startActivity(intent);
                                    }
                                    else{

                                        Map<String,Object> user = new HashMap<>();
                                        user.put("checkin","No Record");
                                        user.put("checkout",LocalTime.now().toString());
                                        user.put("Event","No Event");
                                        documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Log.d("TAG","onSuccess: user profile is created for "+mAuth.getUid());
                                            }
                                        });
                                        Toast.makeText(BiometricScan.this, "Check Out Successful", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(BiometricScan.this, HomeScreen.class);
                                        startActivity(intent);
                                    }
                                }
                            });

                        }
                    }
                });
            }

            @Override
            public void onAuthenticationFailed() {
                System.out.println("Auth UnSuccessfull");
            }
        });

        biometricPrompt.authenticate(promptInfo);
    }

}