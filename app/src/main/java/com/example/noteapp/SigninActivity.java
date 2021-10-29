package com.example.noteapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import Model.User;

public class SigninActivity extends AppCompatActivity implements View.OnClickListener {

    Button signin;
    EditText email_et, password_et;
    TextView dont;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        mAuth = FirebaseAuth.getInstance();
        signin = findViewById(R.id.signin);
        signin.setOnClickListener(this);
        email_et = findViewById(R.id.email_et);
        password_et = findViewById(R.id.password_et);
        dont = findViewById(R.id.dont);
        dont.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.signin:
                signup();
                break;
            case R.id.dont:
                startActivity(new Intent(this, SignupActivity.class));
                break;
        }
    }

    private void signup() {
        String email = email_et.getText().toString().trim();
        String password = password_et.getText().toString().trim();

        if (email.isEmpty()) {
            email_et.setError("This field is required");
            email_et.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            email_et.setError("Please provide a valid email");
            email_et.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            password_et.setError("This field is required");
            password_et.requestFocus();
            return;
        }
        if (password.length() < 6) {
            password_et.setError("Min password length should be 6 characters");
            password_et.requestFocus();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    startActivity(new Intent(SigninActivity.this, StartActivity.class));
                } else {
                    Toast.makeText(SigninActivity.this, "Failed to sign in!", Toast.LENGTH_LONG).show();

                }
            }

            ;
        });
    }}
