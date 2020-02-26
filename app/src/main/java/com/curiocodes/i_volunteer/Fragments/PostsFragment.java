package com.curiocodes.i_volunteer.Fragments;


import android.content.Intent;
import android.graphics.ColorSpace;
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
import com.curiocodes.i_volunteer.Adapters.MyPostAdapter;
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

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class PostsFragment extends Fragment {

    private RecyclerView recyclerView;
    private DatabaseReference db;
    private FirebaseAuth mAuth;
    private List<Models.Posts> list;
    private ProgressBar progressBar;
    private MyPostAdapter myPostAdapter;

    public PostsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_posts, container, false);
        //init
        db = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        list = new ArrayList<>();
        recyclerView = view.findViewById(R.id.listOfPosts);
        progressBar = view.findViewById(R.id.progress);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        myPostAdapter = new MyPostAdapter(list, getContext(), new MyPostAdapter.OnItemClick() {
            @Override
            public void onClick(int pos) {
                Intent intent = new Intent(getContext(), DetailsViewActivity.class);
                intent.putExtra("postId",list.get(pos).getKey());
                startActivity(intent);
            }

            @Override
            public void onDelete(final int pos) {
                Query query= db.child("Posts").orderByChild("key").equalTo(list.get(pos).getKey());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            dataSnapshot.getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        General.toast(getContext(),"Post deleted");
                                        myPostAdapter.notifyDataSetChanged();
                                        getData();
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
        });

        recyclerView.setAdapter(myPostAdapter);


        getData();
        return view;
    }

    private void getData() {
        progressBar.setVisibility(View.VISIBLE);
        Query query= db.child("Posts").orderByChild("uid").equalTo(mAuth.getCurrentUser().getUid());

        query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            list.clear();
                            for (DataSnapshot data : dataSnapshot.getChildren()) {
                                Models.Posts posts = data.getValue(Models.Posts.class);
                                list.add(posts);
                            }
                            progressBar.setVisibility(View.INVISIBLE);
                            myPostAdapter.notifyDataSetChanged();
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
