package com.leadnsolutions.saloonappointmentscheduling.appointments;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.leadnsolutions.saloonappointmentscheduling.R;

public class AppointmentsActivity extends AppCompatActivity {
    private String hisUid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointments);

        if (getIntent()!=null){
            hisUid = getIntent().getStringExtra("hisUid");
            Toast.makeText(this, "Notification received from "+hisUid, Toast.LENGTH_SHORT).show();
        }
    }
}