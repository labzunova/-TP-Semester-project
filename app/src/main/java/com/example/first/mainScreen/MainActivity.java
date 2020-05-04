package com.example.first.mainScreen;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.first.AccountActivity;
import com.example.first.AccountEditActivity;
import com.example.first.MatchesActivity;
import com.example.first.R;
import com.google.firebase.auth.FirebaseAuth;


public class MainActivity extends AppCompatActivity {

    // but of menu
    ImageView editBut, profileBut, matchesBut, exitBut;

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
                startActivity(new Intent(MainActivity.this, AccountActivity.class));
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(ConstValue.INFORMATION_PROCESS_ACTIVITY, "onCreate Activity");

        InitView();

        MainViewModel mViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        mViewModel.getProfile().observe(this, new Observer<MainViewModel.DataProfile>() {
            @Override
            public void onChanged(MainViewModel.DataProfile dataProfile) {
                Log.d(ConstValue.INFORMATION_PROCESS_ACTIVITY, "Create first fragment in activity");
                String infUser = dataProfile.getInfProfile();

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.dogFragment, DogFragment.newInstance(infUser, dataProfile.getMainImageUser()))
                        .commit();
            }
        });

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.d(ConstValue.INFORMATION_PROCESS_ACTIVITY, "onDestroy Activity");
    }


}
