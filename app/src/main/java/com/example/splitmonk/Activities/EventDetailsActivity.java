package com.example.splitmonk.Activities;

import androidx.annotation.NonNull;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.concurrent.RecursiveAction;

import io.paperdb.Paper;

public class EventDetailsActivity extends AppCompatActivity implements EventDetailsAdapter.clicked {

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
            adapter = new EventDetailsAdapter(EventDetailsActivity.this, EventDetailsActivity.this, this);
            event_rec.setLayoutManager(linearLayoutManager);
            event_rec.setAdapter(adapter);
            adapter.setData(curr_event.getEvent_users());
        }

        Toast.makeText(EventDetailsActivity.this, "Please click on cards to pay/withdraw", Toast.LENGTH_LONG).show();

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

    @Override
    public void clickedCard(int position) {
        progressBarUtils.showProgress(EventDetailsActivity.this, EventDetailsActivity.this);
        if(Integer.parseInt(curr_event.getEvent_users().get(position).getUser_to_pay()) < 0){
            String amount = curr_event.getEvent_users().get(position).getUser_to_pay();
            if(Integer.parseInt(curr_event.getEvent_amount_given_to_app()) < (Integer.parseInt(amount) * -1)){
                Toast.makeText(EventDetailsActivity.this, "Please wait! Not Enough members of this event has paid their share.", Toast.LENGTH_LONG).show();
                progressBarUtils.hideProgress();
                return;
            }
            curr_event.getEvent_users().get(position).setUser_to_pay("0");
            curr_event.setEvent_amount_given_to_app(String.valueOf(Integer.parseInt(curr_event.getEvent_amount_given_to_app()) - (Integer.parseInt(amount) * -1)));

            FirebaseDatabase db = FirebaseDatabase.getInstance();
            DatabaseReference ref = db.getReference("split-monk-events");
            ref.child(curr_event.getEvent_id()).setValue(curr_event).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isComplete()){
                        updateGroupAmount(0, amount);
                    }else{
                        progressBarUtils.hideProgress();
                    }
                }
            });
        }else{
            String amount = curr_event.getEvent_users().get(position).getUser_to_pay();
            curr_event.getEvent_users().get(position).setUser_to_pay("0");
            curr_event.setEvent_amount_given_to_app(String.valueOf(Integer.parseInt(curr_event.getEvent_amount_given_to_app()) + Integer.parseInt(amount)));

            FirebaseDatabase db = FirebaseDatabase.getInstance();
            DatabaseReference ref = db.getReference("split-monk-events");
            ref.child(curr_event.getEvent_id()).setValue(curr_event).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isComplete()){
                        updateGroupAmount(1, amount);
                    }else{
                        progressBarUtils.hideProgress();
                    }
                }
            });

        }

    }

    private void updateGroupAmount(int cond, String amount) {
        if(cond == 0){
            FirebaseDatabase db = FirebaseDatabase.getInstance();
            DatabaseReference ref = db.getReference("split_monk_app_account");
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int amount_group = Integer.parseInt(String.valueOf(snapshot.child("amount").getValue(Integer.class)));
                    amount_group -= (Integer.parseInt(amount) * -1);
                    setData(amount_group);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }else{
            FirebaseDatabase db = FirebaseDatabase.getInstance();
            DatabaseReference ref = db.getReference("split_monk_app_account");
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int amount_group = Integer.parseInt(String.valueOf(snapshot.child("amount").getValue(Integer.class)));
                    amount_group += Integer.parseInt(amount);
                    setData(amount_group);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    progressBarUtils.hideProgress();
                }
            });
        }
    }

    private void setData(int amount_group) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference("split_monk_app_account");
        ref.child("amount").setValue(amount_group);
        Toast.makeText(EventDetailsActivity.this, "Succesfully completed!", Toast.LENGTH_SHORT).show();
        progressBarUtils.hideProgress();
        Intent i = new Intent(EventDetailsActivity.this, GroupDashboardActivity.class);
        i.putExtra("group_id", curr_event.getEvent_group_id());
        startActivity(i);
        finish();
    }
}