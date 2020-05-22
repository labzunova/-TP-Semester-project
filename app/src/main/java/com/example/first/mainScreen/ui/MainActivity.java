package com.example.first.mainScreen.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.first.AccountActivity;
import com.example.first.AccountEditActivity;
import com.example.first.AuthorizationActivity;
import com.example.first.MatchesActivity;
import com.example.first.R;
import com.google.firebase.auth.FirebaseAuth;


public class MainActivity extends AppCompatActivity {

    // but of menu
    ImageView editBut, profileBut, matchesBut, exitBut;
    MainViewModel mViewModel;

    private void InitView() {
        editBut = findViewById(R.id.edit_but);
        profileBut = findViewById(R.id.profile_but);
        matchesBut = findViewById(R.id.matches_but);
        exitBut = findViewById(R.id.exit_but);

        editBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AccountEditActivity.class));
            }
        });

        profileBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AccountActivity.class));
            }
        });

        matchesBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, MatchesActivity.class));
            }
        });

        exitBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, AuthorizationActivity.class));
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        InitView();

        mViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        mViewModel.getProfile().observe(this, new Observer<MainViewModel.UIInfo>() {
            @Override
            public void onChanged(MainViewModel.UIInfo dataProfile) {

                DogFragment fragment = DogFragment.newInstance(dataProfile.infoProfile, dataProfile.mainImageUser);
                fragment.setOnSwipeListener(new DogFragment.Listener() {
                    @Override
                    public void swipeLeft() {
                        mViewModel.dislike();
                    }

                    @Override
                    public void swipeRight() {
                        mViewModel.like();
                    }
                });

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.dogFragment, fragment)
                        .commit();
            }
        });

    }
}
