package com.curiocodes.i_volunteer.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.curiocodes.i_volunteer.Activities.LoginActivity;
import com.curiocodes.i_volunteer.AddOn.General;
import com.curiocodes.i_volunteer.Fragments.AccountFragment;
import com.curiocodes.i_volunteer.Fragments.HomeFragment;
import com.curiocodes.i_volunteer.Fragments.PostsFragment;
import com.curiocodes.i_volunteer.Fragments.RequestFragments;
import com.curiocodes.i_volunteer.R;
import com.curiocodes.i_volunteer.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private BottomNavigationView bottomNavigationView;
    private DatabaseReference reference;
    private boolean isVol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar((Toolbar)findViewById(R.id.toolbar));
        getSupportActionBar().setTitle("Home");

        //init
        mAuth = FirebaseAuth.getInstance();
        changeFragment(new HomeFragment());
        bottomNavigationView = findViewById(R.id.bottomNavigation);

        final Menu menu = bottomNavigationView.getMenu();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        if (mAuth.getCurrentUser()!=null) {
            reference.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    isVol = (boolean) dataSnapshot.child("isVol").getValue();
                    if (isVol) {
                        menu.findItem(R.id.posts).setTitle("Requests");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.home:
                        changeFragment(new HomeFragment(isVol));
                        getSupportActionBar().setTitle("Home");
                        return true;
                    case R.id.posts:
                        if (!isVol) {
                            changeFragment(new PostsFragment());
                            getSupportActionBar().setTitle("Posts");
                            return true;
                        }else {
                            changeFragment(new RequestFragments());
                            getSupportActionBar().setTitle("Requests");
                            return true;
                        }
                    case R.id.account:
                        changeFragment(new AccountFragment());
                        getSupportActionBar().setTitle("Account");
                        return true;
                    default:
                        return true;
                }
            }
        });

    }



    private void changeFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frame,fragment);
        transaction.commit();
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser()==null){
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }else {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
            databaseReference.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.exists()){
                        Intent intent = new Intent(MainActivity.this,SetProfileActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.home_menu,menu);
        return true;
    }
}
