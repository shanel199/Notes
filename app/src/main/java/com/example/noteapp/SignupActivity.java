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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.FirebaseDatabase;

import Model.User;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener {

    Button signup;
    EditText email_et, password_et, cnfpassword_et;
    TextView already;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();
        signup = findViewById(R.id.signup);
        signup.setOnClickListener(this);
        email_et = findViewById(R.id.email_et);
        password_et = findViewById(R.id.password_et);
        cnfpassword_et = findViewById(R.id.cnfpassword_et);
        already = findViewById(R.id.already);
        already.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.signup:
                signup();
                break;
            case R.id.already:
                startActivity(new Intent(this, SigninActivity.class));
                break;
    }
}

    private void signup() {
        String email = email_et.getText().toString().trim();
        String password = password_et.getText().toString().trim();
        String cnfpassword = cnfpassword_et.getText().toString().trim();

        if(email.isEmpty()){
            email_et.setError("This field is required");
            email_et.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            email_et.setError("Please provide a valid email");
            email_et.requestFocus();
            return;
        }
        if(password.isEmpty()){
            password_et.setError("This field is required");
            password_et.requestFocus();
            return;
        }
        if(password.length() < 6){
            password_et.setError("Min password length should be 6 characters");
            password_et.requestFocus();
            return;
        }
        if(password != cnfpassword){
            password_et.setError("The passwords does not match");
            password_et.requestFocus();
            return;
        }
        {
            mAuth.fetchSignInMethodsForEmail(email_et.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                        @Override
                        public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                            boolean check = !task.getResult().getSignInMethods().isEmpty();
                            if(check)
                            {
                                Toast.makeText(getApplicationContext(), "Email already exists", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    User user = new User(email, password);
                    FirebaseDatabase.getInstance().getReference("Users")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {

                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                                if (user.isEmailVerified()) {
                                    startActivity(new Intent(SignupActivity.this, StartActivity.class));
                                }
                                else {
                                    user.sendEmailVerification();
                                    Toast.makeText(SignupActivity.this, "Check your email to verify your account", Toast.LENGTH_LONG).show();
                                }
                                if(!task.isSuccessful())
                                {
                                    Toast.makeText(SignupActivity.this, "Failed to sign up", Toast.LENGTH_LONG).show();
                                }
                            }
                        }

                    });
    }}

        });
    }}
