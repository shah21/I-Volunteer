package com.curiocodes.i_volunteer.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.curiocodes.i_volunteer.AddOn.General;
import com.curiocodes.i_volunteer.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    private EditText mEmail,mPass,mConfPass;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //init
        mEmail = findViewById(R.id.email);
        mPass = findViewById(R.id.pass);
        mConfPass = findViewById(R.id.conf_pass);
        progressBar = findViewById(R.id.my_progressBar);


        findViewById(R.id.btn_register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = mEmail.getText().toString();
                final String pass = mPass.getText().toString();
                final String confPass = mConfPass.getText().toString();

                if (email.isEmpty() || pass.isEmpty() || confPass.isEmpty()) {
                    if (email.isEmpty()){
                        mEmail.setError("Email is missing");
                    }
                    if (pass.isEmpty()){
                        mPass.setError("Password number is missing");
                    }
                    if (confPass.isEmpty()){
                        mConfPass.setError("Confirm password is missing");
                    }
                    Toast.makeText(getApplicationContext(), "Check fields", Toast.LENGTH_SHORT).show();
                } else {
                    if (pass.equals(confPass)) {
                        progressBar.setVisibility(View.VISIBLE);
                        //new SendPostRequest().execute(email,pass);
                        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
                        mAuth.createUserWithEmailAndPassword(email, pass).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                progressBar.setVisibility(View.INVISIBLE);
                                Intent intent = new Intent(RegisterActivity.this, SetProfileActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                General.toast(getApplicationContext(),e.getMessage());
                                progressBar.setVisibility(View.INVISIBLE);
                            }
                        });
                    } else {
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(getApplicationContext(), "Passwords are not identical", Toast.LENGTH_SHORT);
                    }
                }
            }
        });

        findViewById(R.id.go_to_log).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });


    }

}
