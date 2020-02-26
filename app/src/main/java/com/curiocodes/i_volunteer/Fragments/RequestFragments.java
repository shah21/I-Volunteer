package com.curiocodes.i_volunteer.Fragments;


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

import com.curiocodes.i_volunteer.Activities.DetailsViewActivity;
import com.curiocodes.i_volunteer.Adapters.PostsAdapter;
import com.curiocodes.i_volunteer.Models.Models;
import com.curiocodes.i_volunteer.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class RequestFragments extends Fragment {

    private RecyclerView recyclerView;
    private DatabaseReference db;
    private FirebaseAuth mAuth;
    private List<Models.Posts> list;
    private PostsAdapter adapter;
    private ProgressBar progressBar;

    public RequestFragments() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_request_fragments, container, false);
        recyclerView = view.findViewById(R.id.listOfPosts);
        db = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        list = new ArrayList<>();
        progressBar = view.findViewById(R.id.progress);

        adapter = new PostsAdapter("request",list, getContext(), new PostsAdapter.OnItemClick() {
            @Override
            public void onClick(int pos) {
                Intent intent = new Intent(getContext(), DetailsViewActivity.class);
                intent.putExtra("postId",list.get(pos).getKey());
                startActivity(intent);
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        getData();

        return view;
    }

    private void getData() {
        progressBar.setVisibility(View.VISIBLE);
        db.child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (final DataSnapshot data:dataSnapshot.getChildren()){
                        Query query = data.getRef().child("Requests").orderByChild("requestId").equalTo(mAuth.getCurrentUser().getUid());
                        query.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    list.clear();
                                    for (DataSnapshot data1 : dataSnapshot.getChildren()) {
                                        Models.Posts posts = data.getValue(Models.Posts.class);
                                        list.add(posts);
                                    }
                                    progressBar.setVisibility(View.INVISIBLE);
                                    adapter.notifyDataSetChanged();
                                }else {
                                    progressBar.setVisibility(View.INVISIBLE);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                progressBar.setVisibility(View.INVISIBLE);
                            }
                        });
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
