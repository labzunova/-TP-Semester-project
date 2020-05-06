package com.example.first.mainScreen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.example.first.AccountActivity;
import com.example.first.AccountEditActivity;
import com.example.first.MatchesActivity;
import com.example.first.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;


public class MainActivity extends AppCompatActivity {

    // but of menu
    ImageView editBut, profileBut, matchesBut, exitBut;

    private void InitView() {

        BottomNavigationView bottomNavigationView;
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.cards);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.profile:
                        startActivity(new Intent(MainActivity.this, AccountActivity.class));
                        return true;
                    case R.id.matches:
                        startActivity(new Intent(MainActivity.this, MatchesActivity.class));
                        return true;
                    case R.id.cards:
                        return true;
                }
                return false;
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
