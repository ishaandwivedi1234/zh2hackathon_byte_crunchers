package com.example.splitmonk.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.splitmonk.R;
import com.example.splitmonk.Utils.progressBarUtils;
import com.example.splitmonk.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;

public class SignupActivity extends AppCompatActivity {

    EditText name, email, password, password_2, upi;
    Button login, signup;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        Paper.init(this);

        InitViews();
        InitData();

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name_txt = name.getText().toString().trim();
                String email_txt = email.getText().toString().trim();
                String password_txt = password.getText().toString().trim();
                String password2_txt = password_2.getText().toString().trim();
                String upi_txt = upi.getText().toString().trim();

                if(name_txt.equals("") || email_txt.equals("") || password_txt.equals("") || password2_txt.equals("") || upi_txt.equals("")){
                    Toast.makeText(SignupActivity.this, "Please Enter all details", Toast.LENGTH_SHORT).show();
                }else if(!password_txt.equals(password2_txt)){
                    Toast.makeText(SignupActivity.this, "Passwords don't match!!", Toast.LENGTH_SHORT).show();
                }else if(password_txt.length() < 8){
                    Toast.makeText(SignupActivity.this, "Password length should be at least 8", Toast.LENGTH_SHORT).show();
                }else{
                    progressBarUtils.showProgress(SignupActivity.this, SignupActivity.this);
                    mAuth = FirebaseAuth.getInstance();
                    mAuth.createUserWithEmailAndPassword(email_txt, password_txt)
                            .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        User user_curr = new User(email_txt, name_txt, user.getUid(), "1", upi_txt);
                                        updateDB(user, user_curr);
                                    } else {
                                        progressBarUtils.hideProgress();
                                        Toast.makeText(SignupActivity.this, "Sign Up failed: "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                }
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });

    }

    private void updateDB(FirebaseUser user, User user_curr) {
        try {
            FirebaseDatabase db = FirebaseDatabase.getInstance();
            DatabaseReference ref = db.getReference("split-monk-users");
            ref.child(user.getUid()).setValue(user_curr);
            updateUI();
        }catch (Exception e){
            Log.e("signup", e.getMessage());
            progressBarUtils.hideProgress();
        }
    }

    private void updateUI() {
        Toast.makeText(this, "User signed-up successfully!!", Toast.LENGTH_SHORT).show();
        progressBarUtils.hideProgress();
        Intent i = new Intent(SignupActivity.this, LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    private void InitData() {

    }

    private void InitViews() {
        name = findViewById(R.id.et_name);
        email = findViewById(R.id.et_email);
        password = findViewById(R.id.et_password);
        password_2 = findViewById(R.id.et_password_2);
        upi = findViewById(R.id.et_upi);

        login = findViewById(R.id.btn_login);
        signup = findViewById(R.id.btn_signup);
    }
}