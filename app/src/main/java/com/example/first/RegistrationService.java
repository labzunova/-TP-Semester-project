package com.example.first;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RegistrationService extends Service {
    private Profile myProfile;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseUser user;

    private ArrayList<String> mySeen;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mySeen = new ArrayList<>();

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference(MainActivityService.NAME_BRANCH);
        user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {

            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    myProfile = dataSnapshot.child(user.getUid()).getValue(Profile.class);
                    myProfile.setSeen(mySeen);
                    mySeen.add(user.getUid());
                    myRef.child(user.getUid()).setValue(myProfile);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        stopSelf(startId);

        Log.d(MainActivity.INF, "stop Service");
        return super.onStartCommand(intent, flags, startId);
    }
}
