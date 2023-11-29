package com.project.i200770_i200608_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class register_user extends AppCompatActivity {

    Button btnRegister;
    Button btnRegisterFingerPrint;
    Button btnRegisterFace;
    EditText id;
    EditText department;
    EditText password;
    EditText confirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);
        btnRegister = findViewById(R.id.checkin);
        btnRegisterFingerPrint = findViewById(R.id.reg_fingerprint);
        btnRegisterFace = findViewById(R.id.reg_face);
        id = findViewById(R.id.nuid);
        department = findViewById(R.id.department);
        password = findViewById(R.id.pass);
        confirmPassword = findViewById(R.id.reneterpass);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(register_user.this, HomeScreen.class);
                startActivity(intent);
            }
        });
        btnRegisterFace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(register_user.this, register_face.class);
                startActivity(intent);
            }
        });
        btnRegisterFingerPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(register_user.this, register_fingerprint.class);
                startActivity(intent);
            }
        });

    }
}