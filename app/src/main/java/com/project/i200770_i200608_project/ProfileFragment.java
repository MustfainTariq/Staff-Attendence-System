package com.project.i200770_i200608_project;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class ProfileFragment extends Fragment {

    Button logout;
    TextView name;
    TextView department;
    FirebaseStorage storage;
    TextView nuid;
    TextView phone;
    TextView designation;
    TextView about;
    TextView professional;
    TextView experience;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore fstore = FirebaseFirestore.getInstance();
    DocumentReference documentReference;

    ImageView DP;
    Uri imagePath;
    String imageuri;
    String myUri = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile,container,false);
        logout = view.findViewById(R.id.logout);
        name = view.findViewById(R.id.name);
        department = view.findViewById(R.id.department);
        nuid = view.findViewById(R.id.email);
        phone = view.findViewById(R.id.phone);
        designation = view.findViewById(R.id.designation);
        about = view.findViewById(R.id.about);
        professional = view.findViewById(R.id.education);
        experience = view.findViewById(R.id.qualification);
        DP = view.findViewById(R.id.profilephoto);
        storage = FirebaseStorage.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();

        fstore.setFirestoreSettings(settings);


        documentReference = fstore.collection("users").document(mAuth.getUid());
        documentReference.addSnapshotListener((value, error) -> {
            if(value.exists()){
                name.setText(value.getString("Name"));
                department.setText(value.getString("Department"));
                nuid.setText(value.getString("Email"));
                phone.setText(value.getString("Phone"));
                designation.setText(value.getString("Designation"));
                about.setText(value.getString("About"));
                professional.setText(value.getString("Education"));
                experience.setText(value.getString("Experience"));
                myUri = value.getString("Image");
                if(myUri != null) {
                    Picasso.with(getContext()).load(myUri).into(DP);
                }
            }
        });


    DP.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent photoIntent = new Intent(Intent.ACTION_PICK);
            photoIntent.setType("image/*");
            startActivityForResult(photoIntent,1);
        }

    });






        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent1 = new Intent(view.getContext(), MainActivity.class);
                startActivity(intent1);
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode ==  1 && resultCode == RESULT_OK && data != null) {
            imagePath = data.getData();
            DP.setImageURI(imagePath);
            StorageReference storageReference = storage.getReference().child("Upload").child(mAuth.getUid());
            storageReference.putFile(imagePath).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()){
                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                imageuri = uri.toString();
                                Task<Void> documentReference = fstore.collection("users").
                                        document(mAuth.getUid()).update("Image",imageuri);
                            }
                        });
                    }
                }
            });


        }
    }
}