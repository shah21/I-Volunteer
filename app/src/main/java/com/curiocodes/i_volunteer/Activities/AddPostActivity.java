package com.curiocodes.i_volunteer.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.curiocodes.i_volunteer.Models.Models;
import com.curiocodes.i_volunteer.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Timestamp;
import java.util.Date;

public class AddPostActivity extends AppCompatActivity {

    private TextView title,place,content,phone;
    private Button btnAdd;
    private DatabaseReference db;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);
        setSupportActionBar((Toolbar)findViewById(R.id.toolbar));
        getSupportActionBar().setTitle("New post");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //init
        title = findViewById(R.id.title);
        place = findViewById(R.id.place);
        content = findViewById(R.id.content);
        phone = findViewById(R.id.phone);
        mAuth = FirebaseAuth.getInstance();

        db = FirebaseDatabase.getInstance().getReference();

        findViewById(R.id.post).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String ttl = title.getText().toString();
                final String plc  = place.getText().toString();
                final String ctn = content.getText().toString();
                final String contact = phone.getText().toString();

                if (ttl.isEmpty()||ctn.isEmpty()||plc.isEmpty()){
                    if (ttl.isEmpty()){
                        title.setError("Title is empty");
                    }
                    if (ctn.isEmpty()){
                        content.setError("Content is empty");
                    }
                    if (plc.isEmpty()){
                        place.setError("Place is empty");
                    }
                    if (contact.isEmpty()){
                        phone.setError("Contact is empty");
                    }
                }else{
                    db.child("Users").child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    String name = dataSnapshot.child("name").getValue().toString();
                                    String uri = dataSnapshot.child("user_photo").getValue().toString();
                                    String key = db.push().getKey();
                                    Models.Posts posts = new Models.Posts(key,name, uri, ttl, ctn, new Timestamp(System.currentTimeMillis()), plc, mAuth.getCurrentUser().getUid(),contact,false);
                                    addPost(key,posts);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                    });

                }

            }
        });
    }

    private void addPost(String key,Models.Posts post){
        db.child("Posts").child(key).setValue(post).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Intent intent = new Intent(AddPostActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
