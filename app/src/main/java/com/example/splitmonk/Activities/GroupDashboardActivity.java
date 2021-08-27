package com.example.splitmonk.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
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
import com.example.splitmonk.models.Event;
import com.example.splitmonk.models.Event_User;
import com.example.splitmonk.models.Group;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import io.paperdb.Paper;

public class GroupDashboardActivity extends AppCompatActivity {

    ImageView back_button, btn_group_info;
    FirebaseAuth mAuth;
    FirebaseUser currUser;
    FloatingActionButton add_event;
    RecyclerView event_rec;
    EventAdapter adapter;
    String group_id_txt;
    TextView groupname;
    Group curr;
    ArrayList<Event> all_events = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_dashboard);

        InitViews();
        InitData();
        currUser = mAuth.getCurrentUser();
        if(currUser == null){
            Paper.book().write("isLoggedIn", false);
            Toast.makeText(this, "Pleaes Log-in to continue", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(GroupDashboardActivity.this, LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        }else{
            group_id_txt = getIntent().getExtras().getString("group_id");
            Log.e("this", group_id_txt);

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            adapter = new EventAdapter(GroupDashboardActivity.this, GroupDashboardActivity.this);
            event_rec.setLayoutManager(linearLayoutManager);
            event_rec.setAdapter(adapter);
            progressBarUtils.showProgress(GroupDashboardActivity.this, GroupDashboardActivity.this);
            loadGroupData();
        }

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(GroupDashboardActivity.this, DashboardActivity.class);
                startActivity(i);
            }
        });

        add_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(GroupDashboardActivity.this, EventCreationActivity.class);
                i.putExtra("group_id", group_id_txt);
                startActivity(i);
            }
        });

        btn_group_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("group_id", curr.getGroup_id());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(GroupDashboardActivity.this, "Group ID has been copied to your clipboard", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void loadGroupData() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference("split-monk-groups").child(group_id_txt);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                curr = snapshot.getValue(Group.class);
                groupname.setText(curr.getGroup_name());
                loadEventData(curr);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Toast.makeText(GroupDashboardActivity.this, "Some Error Occurred!", Toast.LENGTH_SHORT).show();
                progressBarUtils.hideProgress();
            }
        });
    }

    private void loadEventData(Group curr) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference("split-monk-events");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                ArrayList<Event> events = new ArrayList<>();
                for(DataSnapshot snap: snapshot.getChildren()){
                    events.add(snap.getValue(Event.class));
                }

                for(int i = 0; i < events.size(); i++){
                    if(events.get(i).getEvent_group_id().equals(group_id_txt)){
                        all_events.add(events.get(i));
                    }
                }

                adapter.setData(all_events);
                progressBarUtils.hideProgress();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void InitData() {

    }

    private void InitViews() {
        mAuth = FirebaseAuth.getInstance();
        back_button = findViewById(R.id.back_button);
        add_event = findViewById(R.id.add_event);
        groupname = findViewById(R.id.group_name);
        event_rec = findViewById(R.id.events_rec);
        btn_group_info = findViewById(R.id.btn_group_info);
    }

}