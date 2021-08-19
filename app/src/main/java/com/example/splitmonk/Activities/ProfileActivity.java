package com.example.splitmonk.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.splitmonk.R;
import com.example.splitmonk.Utils.progressBarUtils;
import com.example.splitmonk.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.intellij.lang.annotations.PrintFormat;
import org.jetbrains.annotations.NotNull;

import io.paperdb.Paper;

public class ProfileActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseUser currUser;
    ImageView back_button;
    TextView name, email, logout, upi;
    User user;
    RelativeLayout payment_card, profile_card;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        InitViews();

        Paper.init(this);
        currUser = mAuth.getCurrentUser();
        if(currUser == null){
            Paper.book().write("isLoggedIn", false);
            Toast.makeText(this, "Pleaes Log-in to continue", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(ProfileActivity.this, LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        }else{
            loadData();
        }

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, DashboardActivity.class));
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Paper.book().write("isLoggedIn", false);
                Toast.makeText(ProfileActivity.this, "You have been logged out", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(ProfileActivity.this, LoginActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            }
        });

        payment_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase db = FirebaseDatabase.getInstance();
                DatabaseReference ref = db.getReference("split-monk-users");

                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                builder.setTitle("Please Enter you new UPI ID");
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.et_upi, null);
                builder.setView(dialogView);
                AlertDialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(false);

                EditText input_upi = dialogView.findViewById(R.id.et_upi2);

                builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        progressBarUtils.showProgress(ProfileActivity.this, ProfileActivity.this);
                        String upi = input_upi.getText().toString();
                        user.setUpi(upi);
                        ref.child(user.getUid()).setValue(user);
                        updateUI();
                        dialog.cancel();
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        });

        profile_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase db = FirebaseDatabase.getInstance();
                DatabaseReference ref = db.getReference("split-monk-users");

                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                builder.setTitle("Please Enter you new Name");
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.et_upi, null);
                builder.setView(dialogView);
                AlertDialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(false);

                EditText input_name = dialogView.findViewById(R.id.et_upi2);
                input_name.setHint("Name");
                input_name.setCompoundDrawables(AppCompatResources.getDrawable(ProfileActivity.this, R.drawable.profile_logo), null, null, null);

                builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        progressBarUtils.showProgress(ProfileActivity.this, ProfileActivity.this);
                        String name = input_name.getText().toString();
                        user.setName(name);
                        ref.child(user.getUid()).setValue(user);
                        updateUI();
                        dialog.cancel();
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        });

    }

    private void updateUI() {
        progressBarUtils.hideProgress();
        Intent i = new Intent(ProfileActivity.this, ProfileActivity.class);
        startActivity(i);
        finish();
    }

    private void loadData() {
        progressBarUtils.showProgress(ProfileActivity.this, ProfileActivity.this);
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference("split-monk-users").child(currUser.getUid());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                user = snapshot.getValue(User.class);
                InitData();
                progressBarUtils.hideProgress();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void InitViews() {
        mAuth = FirebaseAuth.getInstance();
        back_button = findViewById(R.id.back_button);
        name = findViewById(R.id.user_name);
        email = findViewById(R.id.user_email);
        logout = findViewById(R.id.logout);
        upi = findViewById(R.id.upi_id_txt);
        payment_card = findViewById(R.id.payment_card);
        profile_card = findViewById(R.id.profile_card);
    }

    private void InitData() {
        name.setText(user.getName());
        email.setText(user.getEmail());
        upi.setText(user.getUpi());
    }
}