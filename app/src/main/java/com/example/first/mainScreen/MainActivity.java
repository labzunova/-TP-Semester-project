package com.example.first.mainScreen;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.first.Profile;
import com.example.first.R;


public class MainActivity extends AppCompatActivity {

    private MainViewModel mViewModel;

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
                //startActivity(new Intent(MainActivity.this, AccountEditActivity.class));

                mViewModel.swipe(ConstValue.SIDE_LEFT);
            }
        });

        profileBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(MainActivity.this, AccountActivity.class));

                mViewModel.swipe(ConstValue.SIDE_RIGHT);
            }
        });

        matchesBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(MainActivity.this, MatchesActivity.class));

                mViewModel.swipe(ConstValue.DEFAULT);
            }
        });

        exitBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //FirebaseAuth.getInstance().signOut();
                //startActivity(new Intent(MainActivity.this, AccountActivity.class));
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(ConstValue.INFORMATION_PROCESS_ACTIVITY, "onCreate Activity");

        InitView();

        mViewModel = new ViewModelProvider(this).get(MainViewModel.class);

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
