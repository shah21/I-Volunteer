package com.curiocodes.i_volunteer.Activities;

import      androidx.annotation.NonNull;
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

public class LoginActivity extends AppCompatActivity {
    private EditText mEmail,mPass;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //init
        mEmail = findViewById(R.id.email);
        mPass = findViewById(R.id.pass);
        progressBar = findViewById(R.id.my_progressBar);



        findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString();
                final String pass = mPass.getText().toString();


                if (email.isEmpty() || pass.isEmpty()){
                    if (email.isEmpty()){
                        mEmail.setError("Email is missing");
                    }
                    if (email.isEmpty()){
                        mPass.setError("Password is missing");
                    }
                    Toast.makeText(getApplicationContext(),"Check fields",Toast.LENGTH_SHORT).show();
                    return;
                }else {
                    progressBar.setVisibility(View.VISIBLE);
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    mAuth.signInWithEmailAndPassword(email,pass).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            progressBar.setVisibility(View.INVISIBLE);
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.INVISIBLE);
                            General.toast(getApplicationContext(),e.getMessage());
                        }
                    });;
                }
            }
        });

        findViewById(R.id.go_to_reg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
}
