package com.project.i200770_i200608_project;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashMap;
import java.util.Map;

public class ReportFragment extends Fragment {

    public CalendarView calendarView;
    String Dateselected;
    ImageButton addEvent;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore fstore = FirebaseFirestore.getInstance();
    EditText eventtext;
    TextView name;

    DocumentReference documentReference;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_report, container, false);

        calendarView = (CalendarView) view.findViewById(R.id.calendarView);
        addEvent = (ImageButton) view.findViewById(R.id.addbutton);
        eventtext = (EditText) view.findViewById(R.id.eventdestext);
        name = (TextView) view.findViewById(R.id.name);

        documentReference = fstore.collection("users").document(mAuth.getUid());
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {

            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                if(value.exists()){
                    name.setText(value.getString("Name"));
                }
                else{
                    name.setText("No Name");
                }
            }
        });
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int dayOfMonth) {
                Dateselected = Integer.toString(dayOfMonth) + Integer.toString(month) + Integer.toString(year);
                calanderClicked();
            }
        });

        addEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonSaveEvent(view);
            }
        });



        // Inflate the layout for this fragment
        return view;
    }

    private void calanderClicked() {
        DocumentReference documentReference = (DocumentReference) fstore.collection("users").document(mAuth.getUid()).collection("Calendar").
                document(Dateselected);
                    documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {

                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                        if(value.exists()){
                            eventtext.setText(value.getString("Event"));
                        }
                        else{
                            eventtext.setText("No Event");
                        }
                    }
                });

    }



    public void buttonSaveEvent(View view) {
        DocumentReference documentReference = fstore.collection("users").document(mAuth.getUid()).collection("Calendar").
                document(Dateselected);
        Map<String,Object> user = new HashMap<>();
        user.put("checkin","abc");
        user.put("checkout","abc");
        user.put("Event",eventtext.getText().toString());


        documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("TAG","onSuccess: user profile is created for "+mAuth.getUid());
            }
        });

    }
}