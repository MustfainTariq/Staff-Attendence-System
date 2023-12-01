package com.project.i200770_i200608_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class register_user extends AppCompatActivity {

    Button btnRegister;
    Button btnRegisterFingerPrint;
    Button btnRegisterFace;
    EditText id;
    EditText department;
    EditText password;
    EditText confirmPassword;

    FirebaseAuth mAuth;
    FirebaseFirestore fstore = FirebaseFirestore.getInstance();

    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(getApplicationContext(), HomeScreen.class);
            startActivity(intent);
            finish();
        }
    }

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
        mAuth = FirebaseAuth.getInstance();


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String email, pass, cpass;
                email = String.valueOf(id.getText());
                pass = String.valueOf(password.getText());
                cpass = String.valueOf(confirmPassword.getText());
                if (TextUtils.isEmpty(email)){
                    Toast.makeText(register_user.this,"Enter NU ID!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(pass)){
                    Toast.makeText(register_user.this,"Enter Password!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(cpass)){
                    Toast.makeText(register_user.this,"Enter Password Again!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if( !pass.equals(cpass)){
                    Toast.makeText(register_user.this,"Passwords do not match!", Toast.LENGTH_SHORT).show();
                    return;
                }
                mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

//                            DocumentReference documentReference = fstore.collection("users").document(mAuth.getUid());
//                            Map<String,Object> user = new HashMap<>();
//                            user.put("Name","abc");
//                            user.put("Email",email);
//                            user.put("Phone","abc");
//                            user.put("Pass",pass);
//                            user.put("Fcm","abc");
//                            user.put("Image","abc");
//                            user.put("Department","abc");
//                            user.put("About","abc");
//                            user.put("Education","abc");
//                            user.put("Experience","abc");
//                            user.put("Uid",mAuth.getUid());
//                            user.put("Finger","abc");
//                            user.put("Face","abc");
//
//
//                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                @Override
//                                public void onSuccess(Void unused) {
//                                    Log.d("TAG","onSuccess: user profile is created for "+mAuth.getUid());
//                                }
//                            });
//
//
                            Intent intent = new Intent(register_user.this, HomeScreen.class);
                            startActivity(intent);
                        }
                        else{
                            Toast.makeText(register_user.this,"Invalid NU ID or Password!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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