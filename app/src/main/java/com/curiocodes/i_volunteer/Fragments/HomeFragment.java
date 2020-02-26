package com.curiocodes.i_volunteer.Fragments;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.curiocodes.i_volunteer.Activities.AddPostActivity;
import com.curiocodes.i_volunteer.Activities.DetailsViewActivity;
import com.curiocodes.i_volunteer.Activities.MainActivity;
import com.curiocodes.i_volunteer.Adapters.PostsAdapter;
import com.curiocodes.i_volunteer.AddOn.General;
import com.curiocodes.i_volunteer.Models.Models;
import com.curiocodes.i_volunteer.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private DatabaseReference reference;
    private RecyclerView recyclerView;
    private FloatingActionButton addBtn;
    private FirebaseAuth mAuth;
    private PostsAdapter adapter;
    private List<Models.Posts> postsList;
    private ProgressBar progressBar;
    private boolean isVol;

    public HomeFragment() {
        // Required empty public constructor
    }

    public HomeFragment(boolean isVol){
        this.isVol = isVol;
    }


    @SuppressLint("RestrictedApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_home, container, false);

        //init
        reference = FirebaseDatabase.getInstance().getReference("Posts");
        postsList = new ArrayList<>();
        adapter = new PostsAdapter("home",postsList, getContext(), new PostsAdapter.OnItemClick() {
            @Override
            public void onClick(int pos) {
                Intent intent = new Intent(getContext(), DetailsViewActivity.class);
                intent.putExtra("postId",postsList.get(pos).getKey());
                startActivity(intent);
            }
        });
        mAuth = FirebaseAuth.getInstance();
        recyclerView =  view.findViewById(R.id.posts);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        progressBar = view.findViewById(R.id.my_progressBar);

        addBtn = view.findViewById(R.id.add);

        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        if(mAuth.getCurrentUser()!=null) {
            db.child("Users").child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        isVol = (boolean) dataSnapshot.child("isVol").getValue();
                        if (isVol) {
                            addBtn.setVisibility(View.INVISIBLE);
                        } else {
                            addBtn.setVisibility(View.VISIBLE);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        view.findViewById(R.id.add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AddPostActivity.class);
                startActivity(intent);
            }
        });

        //get posts
        getData();
        // Inflate the layout for this fragment
        return view;
    }

    private void getData() {
        progressBar.setVisibility(View.VISIBLE);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    postsList.clear();
                    for (DataSnapshot data:dataSnapshot.getChildren()){
                        Models.Posts posts = data.getValue(Models.Posts.class);
                        postsList.add(posts);
                    }
                    progressBar.setVisibility(View.INVISIBLE);
                    adapter.notifyDataSetChanged();
                }else {
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
