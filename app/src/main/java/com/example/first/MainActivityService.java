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

    public static final String NAME_BRANCH = "ProfilesSergei";
    public static final String BRANCH_SEEN = "seen";
    private static final String BRANCH_LIKES = "likes";
    private static final String BRANCH_MATCHES = "matches";

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
    private ArrayList<String> idDogs;
    private String idProfileHowSeeUser;

    private ArrayList<String> likes;
    private ArrayList<String> matches;

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
        matches = new ArrayList<>();


        if (user != null) {
            Log.d(INFORMATION_PROCESS_SERVICE, "User +");
            DatabaseReference childRef = myRef.child(NAME_BRANCH).child(user.getUid());
            childRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if(dataSnapshot.getValue(Profile.class).getSeen() != null) {

                        idDogs = dataSnapshot.getValue(Profile.class).getSeen();

                        Log.d(INFORMATION_PROCESS_SERVICE, Integer.toString(idDogs.size()) + " In ArrayList Id");

                        Log.d(INFORMATION_PROCESS_SERVICE, "Start readDataWithServer with Listener");
                    }

                    // initialisation likes with server
                    if(dataSnapshot.getValue(Profile.class).getLikes() != null)
                        likes = dataSnapshot.getValue(Profile.class).getLikes();

                    // initialisation matches with server
                    if(dataSnapshot.getValue(Profile.class).getMatches() != null)
                        matches = dataSnapshot.getValue(Profile.class).getMatches();

                    readDataWithServer();
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

        if (inf != null) {
            //
            Log.d(MainActivity.INF, inf);

            switch (inf) {
                case "left": {
                    //remove with likes

                    setNewProfile();
                    Log.d(INFORMATION_PROCESS_SERVICE, "Stop Service Left");
                    stopSelf(startId);

                    break;
                }

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
                        ArrayList<String> yourMatches = new ArrayList<>();
                        if (userNowSee.getMatches() != null)
                            yourMatches = userNowSee.getLikes();
                        yourMatches.add(user.getUid());
                        myRef.child(NAME_BRANCH).child(idProfileHowSeeUser).child(BRANCH_MATCHES).
                                setValue(yourMatches);

                        // set Matches my profile
                        matches.add(idProfileHowSeeUser);
                        myRef.child(NAME_BRANCH).child(user.getUid()).child(BRANCH_MATCHES).
                                setValue(matches);
                    }

                    setNewProfile();
                    Log.d(INFORMATION_PROCESS_SERVICE, "Stop Service Right");
                    stopSelf(startId);

                    break;
                }

                case "default": {
                    setNewProfile();
                    Log.d(INFORMATION_PROCESS_SERVICE, "Stop Service Default");
                    stopSelf(startId);

                    break;
                }
                default: {
                    idProfileHowSeeUser = inf;
                    myRef.child(NAME_BRANCH).child(idProfileHowSeeUser).
                            addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    userNowSee = dataSnapshot.getValue(Profile.class);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                    break;
                }

            }

        }


        else {

        }

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
        if (idDogs.size() != 0) {
            Log.d(INFORMATION_PROCESS_SERVICE, idDogs.get(0));
            myRef.child(NAME_BRANCH).child(idDogs.get(0))
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            userProfile = dataSnapshot.getValue(Profile.class);

                            Log.d(INFORMATION_PROCESS_SERVICE, "Next Profile We Have in memory");
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

            storageRef.child(idDogs.get(0)).child("AvatarImage").
                    getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    bmpImage = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                    Log.d(INFORMATION_PROCESS_SERVICE, "Next Image in Bitmap");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), "No Such file or Path found!!", Toast.LENGTH_LONG).show();
                }
            });

        }
        else {
            userProfile = null;

            Log.d(INFORMATION_PROCESS_SERVICE, "Next Profile Not");
        }
    }

    private void getNewProfile() {

        // remove profile
        if (idDogs.size() != 0) {
            Log.d(INFORMATION_PROCESS_SERVICE, "Remove profile");
            idProfileHowSeeUser = idDogs.get(0);
            Log.d(MainActivity.INF, idProfileHowSeeUser);
            idDogs.remove(0);
            myRef.child(NAME_BRANCH).child(user.getUid()).child(BRANCH_SEEN).setValue(idDogs);
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

        getNewProfile();
    }
}
