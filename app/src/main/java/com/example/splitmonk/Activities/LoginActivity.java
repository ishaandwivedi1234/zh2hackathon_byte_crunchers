package com.example.splitmonk.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.splitmonk.R;
import com.example.splitmonk.Utils.progressBarUtils;
import com.example.splitmonk.models.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 123;
    EditText et_email, et_password;
    TextView forgot_pass;
    Button login, signup;
    ImageView google, fb, twitter;
    FirebaseAuth mAuth;
    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Paper.init(this);
        InitViews();
        InitData();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email_txt = et_email.getText().toString().trim();
                String password_txt = et_password.getText().toString().trim();

                if(email_txt.equals("") || password_txt.equals("")){
                    Toast.makeText(LoginActivity.this, "Please enter all details", Toast.LENGTH_SHORT).show();
                }else {
                    progressBarUtils.showProgress(LoginActivity.this, LoginActivity.this);
                    signIn(email_txt, password_txt);
                }
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(i);
            }
        });

        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();

                mGoogleSignInClient = GoogleSignIn.getClient(LoginActivity.this, gso);
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Log.e("google login", "Google sign in failed", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            progressBarUtils.showProgress(LoginActivity.this, LoginActivity.this);
                            UpdateDB(user);
                        } else {
                            Log.e("Login", "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed." + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void signIn(String email_txt, String password_txt) {
        mAuth.signInWithEmailAndPassword(email_txt, password_txt)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            UpdateDB(user);
                        } else {
                            progressBarUtils.hideProgress();
                            Log.e("Login", "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed." + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void UpdateDB(FirebaseUser user) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference("split-monk-users");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<User> userList = new ArrayList<>();
                for(DataSnapshot curr: snapshot.getChildren()){
                    User currUser = curr.getValue(User.class);
                    userList.add(currUser);
                }

                for(int i = 0; i < userList.size(); i++){
                    Log.e("login", "old user");
                    if(userList.get(i).getUid().equals(user.getUid())){
                        if(userList.get(i).getUpdated_profile().equals("0")){
                            User curr_user = userList.get(i);
                            Paper.book().write("isLoggedIn", true);
                            showDialogAndUpdate(curr_user);
                        }else {
                            Paper.book().write("isLoggedIn", true);
                            updateUI(user);
                        }
                    }
                }

                User curr = new User(user.getEmail(), user.getDisplayName(), user.getUid(), "0", "");
                Paper.book().write("isLoggedIn", true);
                showDialogAndUpdate(curr);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void showDialogAndUpdate(User curr_user) {
        progressBarUtils.hideProgress();
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference("split-monk-users");

        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("Please Enter you UPI ID");
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.et_upi, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);

        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText input_upi = dialogView.findViewById(R.id.et_upi2);
                String upi = input_upi.getText().toString();
                curr_user.setUpi(upi);
                curr_user.setUpdated_profile("1");
                ref.child(curr_user.getUid()).setValue(curr_user);
                updateUI(mAuth.getCurrentUser());
                dialog.cancel();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(LoginActivity.this, "Please Enter details", Toast.LENGTH_SHORT).show();
            }
        });
        builder.show();
    }

    private void updateUI(FirebaseUser user) {
        Intent i = new Intent(LoginActivity.this, DashboardActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    private void InitData() {

    }

    private void InitViews() {
        et_email = findViewById(R.id.et_email);
        et_password = findViewById(R.id.et_password);
        forgot_pass = findViewById(R.id.txt_forgot_password);
        login = findViewById(R.id.btn_login);
        signup = findViewById(R.id.btn_signup);
        google = findViewById(R.id.google_login);
        fb = findViewById(R.id.fb_login);
        twitter = findViewById(R.id.twitter_login);

        mAuth = FirebaseAuth.getInstance();
    }
}