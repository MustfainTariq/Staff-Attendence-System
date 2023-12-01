package com.project.i200770_i200608_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import android.os.Bundle;
import android.util.Base64;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;

public class BiometricScan extends AppCompatActivity {

    Executor executor = Executors.newFixedThreadPool(2);
    //FirebaseApp.initializeApp(this);
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
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
                // Handle authentication success
                byte[] fingerprintData = new byte[0]; // Replace this with your actual fingerprint data
                try {
                    fingerprintData = result.getCryptoObject().getSignature().sign();
                } catch (SignatureException e) {
                    throw new RuntimeException(e);
                }

                // Hash the fingerprint data
                String hashedFingerprint = null;
                try {
                    hashedFingerprint = hashFingerprintData(fingerprintData);
                } catch (NoSuchAlgorithmException e) {
                    throw new RuntimeException(e);
                }

                // Store the hashed fingerprint in Firestore
                storeFingerprintInFirestore(hashedFingerprint);
            }

            @Override
            public void onAuthenticationFailed() {
                // Handle authentication failure
            }
        });

        biometricPrompt.authenticate(promptInfo);


    }
    private void storeFingerprintInFirestore(String hashedFingerprint) {
        Map<String, Object> fingerprintData = new HashMap<>();
        fingerprintData.put("hashedFingerprint", hashedFingerprint);

        FirebaseFirestore.getInstance()
                .collection("fingerprintCollection") // Replace with your actual collection name
                .add(fingerprintData)
                .addOnSuccessListener(documentReference -> {
                    // Handle successful addition to Firestore
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                });
    }

    private String hashFingerprintData(byte[] fingerprintData) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(fingerprintData);
        return Base64.encodeToString(hashBytes, Base64.DEFAULT);
    }

}