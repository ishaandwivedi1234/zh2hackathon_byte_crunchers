package com.example.splitmonk.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.splitmonk.R;
import com.example.splitmonk.Utils.progressBarUtils;
import com.example.splitmonk.adapter.EventAdapter;
import com.example.splitmonk.adapter.EventDetailsAdapter;
import com.example.splitmonk.models.Event;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.concurrent.RecursiveAction;

import io.paperdb.Paper;

public class EventDetailsActivity extends AppCompatActivity {

    ImageView back_button;
    FirebaseAuth mAuth;
    FirebaseUser currUser;
    Event curr_event;
    TextView eventname, eventamount, eventpeople;
    RecyclerView event_rec;
    EventDetailsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        InitViews();
        InitData();
        Paper.init(this);
        curr_event = Paper.book().read("event");
        currUser = mAuth.getCurrentUser();

        if(currUser == null){
            Paper.book().write("isLoggedIn", false);
            Toast.makeText(this, "Pleaes Log-in to continue", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(EventDetailsActivity.this, LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        }else{
            eventname.setText(curr_event.getEvent_name());
            eventpeople.setText(String.valueOf(curr_event.getEvent_users().size()));
            eventamount.setText(String.valueOf(curr_event.getEvent_total_amount()));

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            adapter = new EventDetailsAdapter(EventDetailsActivity.this, EventDetailsActivity.this);
            event_rec.setLayoutManager(linearLayoutManager);
            event_rec.setAdapter(adapter);
            adapter.setData(curr_event.getEvent_users());
        }

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(EventDetailsActivity.this, GroupDashboardActivity.class);
                i.putExtra("group_id", curr_event.getEvent_group_id());
                startActivity(i);
                finish();
            }
        });

    }

    private void InitData() {

    }

    private void InitViews() {
        mAuth = FirebaseAuth.getInstance();
        back_button = findViewById(R.id.back_button);
        eventname = findViewById(R.id.group_name);
        eventamount = findViewById(R.id.event_total_amount);
        eventpeople = findViewById(R.id.event_total_contri);
        event_rec = findViewById(R.id.event_rec);
    }

}