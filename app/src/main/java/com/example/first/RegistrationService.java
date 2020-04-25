package com.example.first;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

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
                    for (DataSnapshot snapshotNode: dataSnapshot.getChildren()) {
                        // add all id in my profile
                        mySeen.add(snapshotNode.getKey());

                        // add my id in all profile

                    }

                    myProfile = dataSnapshot.child(user.getUid()).getValue(Profile.class);
                    myProfile.setSeen(mySeen);
                    myRef.child(user.getUid()).setValue(myProfile);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
    }
}
