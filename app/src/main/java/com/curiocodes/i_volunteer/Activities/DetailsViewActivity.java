package com.curiocodes.i_volunteer.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.curiocodes.i_volunteer.AddOn.General;
import com.curiocodes.i_volunteer.Models.Models;
import com.curiocodes.i_volunteer.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

public class DetailsViewActivity extends AppCompatActivity {

    private String key;
    private TextView place,created,content,contact_number,author;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private Button btnInterested;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_view);
        setSupportActionBar((Toolbar)findViewById(R.id.toolbar));
        getSupportActionBar().setTitle("Know more");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        key = getIntent().getStringExtra("postId");
        //init
        databaseReference = FirebaseDatabase.getInstance().getReference();
        place = findViewById(R.id.place);
        created = findViewById(R.id.date);
        content = findViewById(R.id.content);
        contact_number = findViewById(R.id.contact);
        author = findViewById(R.id.author);
        mAuth = FirebaseAuth.getInstance();
        btnInterested = findViewById(R.id.btnIntr);

        checkIntr();

        getData();

        btnInterested.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addIntrst();
            }
        });


    }

    private void checkIntr() {
        Query query = databaseReference.child("Posts").child(key).child("Requests").orderByChild("requestId").equalTo(mAuth.getCurrentUser().getUid());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() != 0){
                    btnInterested.setText("Already Interested");
                }else {
                    btnInterested.setText(R.string.intrested);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addIntrst() {
        final boolean[] avail = {false};
        final Models.Request request = new Models.Request(mAuth.getCurrentUser().getUid(),new Timestamp(System.currentTimeMillis()),false);

        Query query = databaseReference.child("Posts").child(key).child("Requests").orderByChild("requestId").equalTo(mAuth.getCurrentUser().getUid());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() == 0){
                    databaseReference.child("Posts").child(key).child("Requests").push().setValue(request).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Map<String,Object> map = new HashMap<>();
                            map.put("postId",key);
                            map.put("requested",new Timestamp(System.currentTimeMillis()));
                            map.put("status",false);
                            databaseReference.child("Users").child(mAuth.getCurrentUser().getUid()).child("Requests").
                                    push().setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    checkIntr();
                                }
                            });
                        }
                    });
                }else {
                    dataSnapshot.getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Query query1 = databaseReference.child("Users").child(mAuth.getCurrentUser().getUid()).child("Requests")
                                        .orderByChild("postId").equalTo(key);

                                query1.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        dataSnapshot.getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    checkIntr();
                                                }
                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

    private void getData() {
        databaseReference.child("Posts").child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    Models.Posts posts = dataSnapshot.getValue(Models.Posts.class);
                    if (posts.getUid().equals(mAuth.getCurrentUser())){
                        btnInterested.setEnabled(false);
                    }
                    databaseReference.child("Users").child(posts.getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                 author.setText("Posted by  "+dataSnapshot.child("name").getValue().toString());
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    place.setText(posts.getPlace());
                    String date = new java.text.SimpleDateFormat("MM/dd/yyyy").format(posts.getDate());
                    created.setText(date);
                    content.setText(posts.getContent());
                    contact_number.setText(posts.getContact());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
