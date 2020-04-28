package com.example.first;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class MainActivityService extends Service {
    public static final String INFORMATION_PROCESS_SERVICE = "infService";

    public static final String NAME_BRANCH = "Profiles";
    public static final String BRANCH_SEEN = "seen";
    private static final String BRANCH_LIKES = "likes";
    private static final String BRANCH_MATCHES = "matches";
    private static final String BRANCH_ID_PROFILES = "IdProfiles";

    private final long ONE_MEGABYTE = 1024 * 1024;

    // For data
    private DatabaseReference myRef;
    private FirebaseUser user;

    // For Image
    private StorageReference storageRef;
    Bitmap bmpImage;

    // For connection server with activity
    private MyBinder mBinder = new MyBinder();
    private ProfileListener listener = null;

    // Copy of data from the server
    private Profile userProfile;
    private Profile userNowSee;
    private Profile myProfile;
    private ArrayList<String> idDogs;
    private String idProfileHowSeeUser;

    private ArrayList<String> likes;
    private ArrayList<String> seen;
    private ArrayList<Profile.Matches> matches;

    int i;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(INFORMATION_PROCESS_SERVICE, "ServiceOnCreate");

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();

        storageRef = FirebaseStorage.getInstance().getReference().child(NAME_BRANCH);

        idDogs = new ArrayList<>();
        likes = new ArrayList<>();
        seen = new ArrayList<>();
        matches = new ArrayList<>();


        if (user != null) {
            Log.d(INFORMATION_PROCESS_SERVICE, "User +");
            myRef.child(BRANCH_ID_PROFILES).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Log.d(INFORMATION_PROCESS_SERVICE, dataSnapshot.getKey());

                    idDogs.clear();
                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren ()) {
                        idDogs.add(postSnapshot.getValue(String.class));
                    }

                    Log.d(INFORMATION_PROCESS_SERVICE, Integer.toString(idDogs.size()) + " In ArrayList Id");

                    Log.d(INFORMATION_PROCESS_SERVICE, "Start readDataWithServer with Listener");
                    readDataWithServer();
                }


                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            Log.d(INFORMATION_PROCESS_SERVICE, "Start readDataWithServer with Listener");

            DatabaseReference childRef = myRef.child(NAME_BRANCH).child(user.getUid());
            childRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if(dataSnapshot.getValue(Profile.class) != null) {

                        myProfile = dataSnapshot.getValue(Profile.class);
                    }

                    // initialisation likes with server
                    if(dataSnapshot.getValue(Profile.class).getLikes() != null)
                        likes = dataSnapshot.getValue(Profile.class).getLikes();

                    // initialisation matches with server
                    if(dataSnapshot.getValue(Profile.class).getMatches() != null)
                        matches = dataSnapshot.getValue(Profile.class).getMatches();

                    // initialisation seen with server
                    if(dataSnapshot.getValue(Profile.class).getSeen() != null)
                        seen = dataSnapshot.getValue(Profile.class).getSeen();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String inf;
        inf = intent.getAction();

        Log.d(INFORMATION_PROCESS_SERVICE, "New Service");

        if ((inf != null) && (userNowSee != null)) {
            //
            Log.d(MainActivity.INF, inf);

            switch (inf) {

                case "right": {
                    //remove with likes, put in likes or matches

                    Log.d(MainActivity.INF, Boolean.toString(likes.isEmpty()));
                    if (likes.indexOf(idProfileHowSeeUser) == -1) {

                        ArrayList<String> yourLikes = new ArrayList<>();
                        if (userNowSee.getLikes() != null)
                            yourLikes = userNowSee.getLikes();
                        yourLikes.add(user.getUid());
                        myRef.child(NAME_BRANCH).child(idProfileHowSeeUser).child(BRANCH_LIKES).
                                setValue(yourLikes);
                    }
                    else {
                        likes.remove(likes.indexOf(idProfileHowSeeUser));
                        myRef.child(NAME_BRANCH).child(user.getUid()).child(BRANCH_LIKES).setValue(likes);

                        // set Matches your profile
                        ArrayList<Profile.Matches> yourMatches = new ArrayList<Profile.Matches>();
                        if (userNowSee.getMatches() != null)
                            yourMatches = userNowSee.getMatches();
                        yourMatches.add(new Profile.Matches(user.getUid(), myProfile.getName(), "false"));
                        myRef.child(NAME_BRANCH).child(idProfileHowSeeUser).child(BRANCH_MATCHES).
                                setValue(yourMatches);

                        // set Matches my profile
                        matches.add(new Profile.Matches(idProfileHowSeeUser, userNowSee.getName(), "false"));
                        myRef.child(NAME_BRANCH).child(user.getUid()).child(BRANCH_MATCHES).
                                setValue(matches);
                    }

                    break;
                }

                default: {
//                    idProfileHowSeeUser = inf;
//                    myRef.child(NAME_BRANCH).child(idProfileHowSeeUser).
//                            addListenerForSingleValueEvent(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                    userNowSee = dataSnapshot.getValue(Profile.class);
//                                }
//
//                                @Override
//                                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                }
//                            });
                    break;
                }

            }

        }


        else {

        }

        setNewProfile();
        Log.d(INFORMATION_PROCESS_SERVICE, "Stop Service");
        stopSelf(startId);

        super.onStartCommand(intent, flags, startId);
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d(INFORMATION_PROCESS_SERVICE, "onDestroy Service");
    }

    private void readDataWithServer() {
        Log.d(INFORMATION_PROCESS_SERVICE, "Start readDataWithServer");

        userNowSee = userProfile;

        i = 0;
        while ((i < idDogs.size()) && (seen.contains(idDogs.get(i))))
            i++;

        if ((idDogs.size() == 0) || (i == idDogs.size())) {
            userProfile = null;

            Log.d(INFORMATION_PROCESS_SERVICE, "Next Profile Not");
        }
        else {
            Log.d(INFORMATION_PROCESS_SERVICE, idDogs.get(i));

            // get dada
                myRef.child(NAME_BRANCH).child(idDogs.get(i))
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Log.d(INFORMATION_PROCESS_SERVICE, "Next Profile We Have in memory");

                                userProfile = dataSnapshot.getValue(Profile.class);

                                Log.d(INFORMATION_PROCESS_SERVICE, "Next Profile We Have in memory");
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

            // get image
            storageRef.child(idDogs.get(i)).child("AvatarImage").
                    getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    bmpImage = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                    Log.d(INFORMATION_PROCESS_SERVICE, "Next Image in Bitmap");
                }
            });
        }
    }

    private void getNewProfile() {

        // add profile in seen
        if (idDogs.size() != 0) {
            Log.d(INFORMATION_PROCESS_SERVICE, "Add profile in seen");
            idProfileHowSeeUser = idDogs.get(i);
            Log.d(MainActivity.INF, idProfileHowSeeUser);
            seen.add(idProfileHowSeeUser);
            myRef.child(NAME_BRANCH).child(user.getUid()).child(BRANCH_SEEN).setValue(seen);
        }

        readDataWithServer();
    }




    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void listenEvents(ProfileListener profileListener) {
        listener = profileListener;
    }

    class MyBinder extends Binder {
        MainActivityService getService() {
            return MainActivityService.this;
        }
    }

    interface ProfileListener {
        void newProfile(Profile profile, Bitmap bmpImage);
    }

    public void setNewProfile () {
        Log.d(INFORMATION_PROCESS_SERVICE, "Service Start New Profile");
        if (userProfile != null) {

        }
        else {
            Log.d(INFORMATION_PROCESS_SERVICE, "Not Profile");
        }

        Log.d(INFORMATION_PROCESS_SERVICE, "Transport Profile");
        listener.newProfile(userProfile, bmpImage);
        bmpImage = null;

        getNewProfile();
    }
}
