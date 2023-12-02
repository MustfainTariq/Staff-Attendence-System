package com.project.i200770_i200608_project;

import android.annotation.SuppressLint;
import android.graphics.Color;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class ReportFragment extends Fragment {

    public CalendarView calendarView;
    String Dateselected;
    ImageButton addEvent;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore fstore = FirebaseFirestore.getInstance();
    EditText eventtext;
    TextView Attendance;
    TextView name;
    TextView checkintime;
    TextView checkouttime;

    DocumentReference documentReference;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_report, container, false);

        calendarView = (CalendarView) view.findViewById(R.id.calendarView);
        addEvent = (ImageButton) view.findViewById(R.id.addbutton);
        eventtext = (EditText) view.findViewById(R.id.eventdestext);
        name = (TextView) view.findViewById(R.id.name);
        checkintime = (TextView) view.findViewById(R.id.checkintime);
        checkouttime = (TextView) view.findViewById(R.id.checkouttime);
        Attendance = (TextView) view.findViewById(R.id.attendence);


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
                Dateselected = Integer.toString(dayOfMonth) + Integer.toString(month+1) + Integer.toString(year);
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
                            String intime = value.getString("checkin");
                            String outtime = value.getString("checkout");
                            if(!intime.equals("No Record")){
                                Attendance.setText("Present");
                                Attendance.setTextColor(Color.parseColor("#FF4CAF50"));
                                @SuppressLint({"NewApi", "LocalSuppress"}) LocalTime localTime = LocalTime.parse(intime, DateTimeFormatter.ofPattern("HH:mm:ss.SSS"));
                                @SuppressLint({"NewApi", "LocalSuppress"}) DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("h:mm a");
                                @SuppressLint({"NewApi", "LocalSuppress"}) String formattedTime = localTime.format(outputFormatter);
                                checkintime.setText(formattedTime);
                            }
                            else{
                                Attendance.setText("Absent");
                                Attendance.setTextColor(Color.parseColor("#F44336"));
                                checkintime.setText(intime);
                            }
                            if(!outtime.equals("No Record")){
                                @SuppressLint({"NewApi", "LocalSuppress"}) LocalTime localTime1 = LocalTime.parse(outtime, DateTimeFormatter.ofPattern("HH:mm:ss.SSS"));
                                @SuppressLint({"NewApi", "LocalSuppress"}) DateTimeFormatter outputFormatter1 = DateTimeFormatter.ofPattern("h:mm a");
                                @SuppressLint({"NewApi", "LocalSuppress"}) String formattedTime1 = localTime1.format(outputFormatter1);
                                checkouttime.setText(formattedTime1);
                            }
                            else{
                                checkouttime.setText(outtime);
                            }

                        }
                        else{
                            eventtext.setText("No Event");
                            checkintime.setText("No Record");
                            checkouttime.setText("No Record");
                            Attendance.setText("Absent");
                            Attendance.setTextColor(Color.parseColor("#F44336"));

                        }
                    }
                });

    }



    public void buttonSaveEvent(View view) {
        DocumentReference documentReference = fstore.collection("users").document(mAuth.getUid()).collection("Calendar").
                document(Dateselected);

        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                if(value.exists()){
                    documentReference.update("Event", eventtext.getText().toString());
                }
                else{

                    Map<String,Object> user = new HashMap<>();
                    user.put("checkin","No Record");
                    user.put("checkout","No Record");
                    user.put("Event",eventtext.getText().toString());
                    documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.d("TAG","onSuccess: user profile is created for "+mAuth.getUid());
                        }
                    });
                    Toast.makeText(getContext(), "Event Saved", Toast.LENGTH_SHORT).show();

                }
            }
        });


    }
}