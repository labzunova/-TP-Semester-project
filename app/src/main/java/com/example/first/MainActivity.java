package com.example.first;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;


public class MainActivity extends AppCompatActivity implements MainActivityService.ProfileListener {
    public static final String INF = "information";
    public static final String INFORMATION_PROCESS_ACTIVITY = "infActivity";


    private MainActivityService mainService;

    private Intent intent;
    private ServiceConnection sConn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        intent = new Intent(this, MainActivityService.class);

        Log.d(INFORMATION_PROCESS_ACTIVITY, "Create Service in Activity");
        sConn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mainService = ((MainActivityService.MyBinder)service).getService();
                mainService.listenEvents(MainActivity.this);

                Log.d(INFORMATION_PROCESS_ACTIVITY, "sConn");
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
        bindService(intent, sConn, BIND_AUTO_CREATE);
        Log.d(INFORMATION_PROCESS_ACTIVITY, "Service <-> Activity");


        // Create first fragment
        StartDogFragment firstFragment = StartDogFragment.newInstance();
        if (savedInstanceState == null) {

            Log.d(INFORMATION_PROCESS_ACTIVITY, "Create first fragment in activity");
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.dogFragment, firstFragment)
                    .commit();


        }

        findViewById(R.id.nextFragment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainService.setNewProfile();
            }
        });

    }



    private Profile userSave;

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.d(INFORMATION_PROCESS_ACTIVITY, "onDestroy Activity");
        unbindService(sConn);
    }


    @Override
    public void newProfile(Profile profile, Bitmap bmpImage) {
        if (profile != null) {
            Log.d(INFORMATION_PROCESS_ACTIVITY, "Transport data to new fragment");

            userSave = profile;
            DogFragment nextFragment = DogFragment.newInstance(bmpImage, profile);

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.dogFragment, nextFragment)
                    .commit();
        }
        else {


            Log.d(INFORMATION_PROCESS_ACTIVITY, "Default fragment end");

            ProfilesOverFragment nextFragment = ProfilesOverFragment.newInstance();

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.dogFragment, nextFragment)
                    .commit();
        }

        Log.d(INFORMATION_PROCESS_ACTIVITY, "Start fragment");
    }

}
