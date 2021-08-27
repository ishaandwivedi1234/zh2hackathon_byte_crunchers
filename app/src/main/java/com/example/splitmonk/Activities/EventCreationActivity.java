package com.example.splitmonk.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.splitmonk.R;
import com.example.splitmonk.Utils.progressBarUtils;
import com.example.splitmonk.adapter.EventUserAdapter;
import com.example.splitmonk.models.Event;
import com.example.splitmonk.models.Event_User;
import com.example.splitmonk.models.Group;
import com.example.splitmonk.models.User;
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

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import io.paperdb.Paper;

public class EventCreationActivity extends AppCompatActivity {

    ImageView back_button;
    FirebaseAuth mAuth;
    FirebaseUser currUser;
    Button create_Event;
    String group_id_txt;
    EditText event_name, event_amount;
    RecyclerView users_rec;
    TextView groupname;
    EventUserAdapter adapter;
    ArrayList<String> all_group_users_id = new ArrayList<>();
    ArrayList<User> all_group_users = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_creation);

        InitViews();
        InitData();
        currUser = mAuth.getCurrentUser();
        if(currUser == null){
            Paper.book().write("isLoggedIn", false);
            Toast.makeText(this, "Pleaes Log-in to continue", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(EventCreationActivity.this, LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        }else{
            group_id_txt = getIntent().getExtras().getString("group_id");
            Log.e("this", group_id_txt);

            progressBarUtils.showProgress(EventCreationActivity.this, EventCreationActivity.this);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(EventCreationActivity.this);
            adapter = new EventUserAdapter(EventCreationActivity.this, EventCreationActivity.this);
            users_rec.setLayoutManager(linearLayoutManager);
            users_rec.setAdapter(adapter);
            loadGroupData();
        }

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(EventCreationActivity.this, GroupDashboardActivity.class);
                i.putExtra("group_id", group_id_txt);
                startActivity(i);
                finish();
            }
        });

        create_Event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Event_User> selected_users = adapter.getSelected_users();
                if(selected_users.size() < 2){
                    Toast.makeText(EventCreationActivity.this, "Please Select at least 2 Users!", Toast.LENGTH_LONG).show();
                    return;
                }
                int sum = 0;
                for(int i = 0; i < selected_users.size(); i++){
                    sum += Integer.parseInt(selected_users.get(i).getUser_paid());
                }
                String event_total_amount = event_amount.getText().toString();
                String event_name_Txt = event_name.getText().toString();
                String event_individual;
                if(!String.valueOf(sum).trim().equals(event_total_amount.trim())){
                    Toast.makeText(EventCreationActivity.this, "Total contributed doesn't add up to total amount", Toast.LENGTH_LONG).show();
                    return;
                }
                progressBarUtils.showProgress(EventCreationActivity.this, EventCreationActivity.this);
                if(event_name_Txt.equals("") || event_total_amount.equals("")){
                    Toast.makeText(EventCreationActivity.this, "Please enter all details", Toast.LENGTH_SHORT).show();
                    progressBarUtils.hideProgress();
                }else if(Integer.parseInt(event_total_amount) < 1){
                    Toast.makeText(EventCreationActivity.this, "Total amount should be greater than 0", Toast.LENGTH_SHORT).show();
                    progressBarUtils.hideProgress();
                }else{
                    int total = Integer.parseInt(event_total_amount);
                    int individual = total/selected_users.size();
                    event_individual = String.valueOf(individual);
                    for(int i = 0; i < selected_users.size(); i++){
                        String paid_curr = selected_users.get(i).getUser_paid();
                        String to_pay_curr = String.valueOf(individual - Integer.parseInt(paid_curr));
                        selected_users.get(i).setUser_to_pay(to_pay_curr);
                    }

                    String event_id = group_id_txt + event_name_Txt.substring(0, 4);
                    Event event = new Event(event_name_Txt, event_total_amount, event_individual, group_id_txt, event_id, "0", selected_users);
                    uploadEvent(event);
                }
            }
        });

    }

    private void uploadEvent(Event event) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference("split-monk-events");
        ref.child(event.getEvent_id()).setValue(event).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task) {
                if(task.isComplete()){
                    Toast.makeText(EventCreationActivity.this, "Event Uploaded!", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(EventCreationActivity.this, GroupDashboardActivity.class);
                    i.putExtra("group_id", group_id_txt);
                    progressBarUtils.hideProgress();
                    startActivity(i);
                    finish();
                }else{
                    progressBarUtils.hideProgress();
                    Toast.makeText(EventCreationActivity.this, "Some error occurred, Please try again", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadGroupData() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference("split-monk-groups").child(group_id_txt);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                Group curr = snapshot.getValue(Group.class);
                groupname.setText(curr.getGroup_name());
                all_group_users_id = curr.getUsers();
                loadUserData();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Toast.makeText(EventCreationActivity.this, "Some Error Occurred!", Toast.LENGTH_SHORT).show();
                progressBarUtils.hideProgress();
            }
        });
    }

    private void loadUserData() {
        for(int i = 0; i < all_group_users_id.size(); i++){
            Log.e("this", "1");
            FirebaseDatabase db = FirebaseDatabase.getInstance();
            DatabaseReference ref = db.getReference("split-monk-users").child(all_group_users_id.get(i));
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    all_group_users.add(snapshot.getValue(User.class));
                    adapter.setData(all_group_users);
                    progressBarUtils.hideProgress();
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {
                    Toast.makeText(EventCreationActivity.this, "Some Error Occurred!", Toast.LENGTH_SHORT).show();
                    progressBarUtils.hideProgress();
                }
            });
        }
    }

    private void InitData() {

    }

    private void InitViews() {
        mAuth = FirebaseAuth.getInstance();
        back_button = findViewById(R.id.back_button);
        create_Event = findViewById(R.id.btn_create_event);
        event_amount = findViewById(R.id.et_event_amount);
        event_name = findViewById(R.id.et_event_name);
        users_rec = findViewById(R.id.users_rec);
        groupname = findViewById(R.id.group_name);
    }

}