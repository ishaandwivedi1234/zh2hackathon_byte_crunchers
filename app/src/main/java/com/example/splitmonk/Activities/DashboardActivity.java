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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.splitmonk.adapter.GroupAdapter;
import com.example.splitmonk.R;
import com.example.splitmonk.Utils.progressBarUtils;
import com.example.splitmonk.models.Group;
import com.example.splitmonk.models.User;
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

public class DashboardActivity extends AppCompatActivity {

    ImageView no_group_img;
    Button profile;
    TextView no_group_txt;
    GroupAdapter adapter;
    RecyclerView group_rec;
    FirebaseAuth mAuth;
    FirebaseUser currUser;
    FloatingActionButton add_a_group;
    User current_user;
    ArrayList<Group> current_user_groups = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        Paper.init(this);
        InitViews();
        boolean logged = Paper.book().read("isLoggedIn", false);
        if(!logged){
            Intent i = new Intent(DashboardActivity.this, LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        }else{
            InitData();
            currUser = mAuth.getCurrentUser();
            if(currUser == null){
                Paper.book().write("isLoggedIn", false);
                Toast.makeText(this, "Pleaes Log-in to continue", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(DashboardActivity.this, LoginActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            }else{
                progressBarUtils.showProgress(DashboardActivity.this, DashboardActivity.this);
                hideGroup();
                adapter = new GroupAdapter(this, DashboardActivity.this);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
                group_rec.setLayoutManager(layoutManager);
                group_rec.setAdapter(adapter);

                loadUserData();
            }
        }

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this, ProfileActivity.class));
            }
        });

        add_a_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this, CreateOrJoinActivity.class));
            }
        });

    }

    private void loadUserData() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference("split-monk-users").child(currUser.getUid());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                current_user = snapshot.getValue(User.class);
                loadGroupData();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void loadGroupData() {
        ArrayList<String> groups = current_user.getGroups();
        ArrayList<Task<DataSnapshot>> tasks = new ArrayList<>();
        for(int i = 0; i < groups.size(); i++){
            Log.e("this", "1");
            FirebaseDatabase db = FirebaseDatabase.getInstance();
            DatabaseReference ref = db.getReference("split-monk-groups").child(groups.get(i));
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    current_user_groups.add(snapshot.getValue(Group.class));
                    adapter.setData(current_user_groups);
                    showGroup();
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                }
            });
        }

        progressBarUtils.hideProgress();
    }

    private void hideGroup(){
        group_rec.setVisibility(View.GONE);
        no_group_txt.setVisibility(View.VISIBLE);
        no_group_img.setVisibility(View.VISIBLE);
    }

    private void showGroup(){
        group_rec.setVisibility(View.VISIBLE);
        no_group_txt.setVisibility(View.GONE);
        no_group_img.setVisibility(View.GONE);
    }

    private void InitData() {

    }

    private void InitViews() {
        profile = findViewById(R.id.profile_page);
        mAuth = FirebaseAuth.getInstance();
        add_a_group = findViewById(R.id.add_a_group);
        no_group_img = findViewById(R.id.no_group_img);
        no_group_txt = findViewById(R.id.no_group_txt);
        group_rec = findViewById(R.id. group_rec);
    }
}