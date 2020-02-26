package com.curiocodes.i_volunteer.Fragments;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.curiocodes.i_volunteer.Activities.LoginActivity;
import com.curiocodes.i_volunteer.Activities.MainActivity;
import com.curiocodes.i_volunteer.Models.Models;
import com.curiocodes.i_volunteer.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment {

    private TextView name,email,phone,place;
    private DatabaseReference db;
    private FirebaseAuth auth;
    private CircleImageView circleImageView;
    private ProgressBar progressBar;

    public AccountFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_account, container, false);
        // Inflate the layout for this fragment

        db = FirebaseDatabase.getInstance().getReference();
        name = view.findViewById(R.id.name);
        email = view.findViewById(R.id.email);
        phone = view.findViewById(R.id.phone);
        place = view.findViewById(R.id.place);
        circleImageView = view.findViewById(R.id.profile_image);

        progressBar = view.findViewById(R.id.progress);

        auth = FirebaseAuth.getInstance();

        getData();

        view.findViewById(R.id.logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    public void getData(){
        progressBar.setVisibility(View.VISIBLE);
        db.child("Users").child(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    name.setText(dataSnapshot.child("name").getValue(String.class));
                    phone.setText(dataSnapshot.child("phone").getValue(String.class));
                    email.setText(auth.getCurrentUser().getEmail());
                    place.setText(dataSnapshot.child("place").getValue(String.class));
                    Picasso.get().load(dataSnapshot.child("user_photo").getValue(String.class)).fit().placeholder(R.drawable.profile_placeholder).into(circleImageView);
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
