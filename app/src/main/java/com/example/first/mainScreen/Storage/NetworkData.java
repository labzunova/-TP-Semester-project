package com.example.first.mainScreen.Storage;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.first.Profile;
import com.example.first.mainScreen.ConstValue;
import com.example.first.mainScreen.InfRepo;
import com.example.first.mainScreen.MainViewModel;
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
import java.util.Random;

public class NetworkData {
    private InfRepo repo;

    private DatabaseReference myRef;
    private FirebaseUser user;
    private StorageReference storageRef;

    private final long ONE_MEGABYTE = 3 * 1024 * 1024;

    private MutableLiveData<InfRepo.UserInformation> newUserProfile;

    public NetworkData(@NonNull final InfRepo repo){
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        storageRef = FirebaseStorage.getInstance().getReference().child(ConstValue.NAME_BRANCH);

        this.repo = repo;

        if (user != null) {
            // get myProfile
            myRef.child(ConstValue.NAME_BRANCH).child(user.getUid())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            repo.setMyProfile(dataSnapshot.getValue(Profile.class));
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

            // get id all Profiles
            myRef.child(ConstValue.BRANCH_ID_PROFILES)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            ArrayList<String> idProfiles = new ArrayList<>();
                            for (DataSnapshot data : dataSnapshot.getChildren())
                                idProfiles.add(data.getValue(String.class));

                            repo.setIdProfiles(idProfiles);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        }


    }

    public LiveData<InfRepo.UserInformation> getNewProfile(@NonNull final String idProfile) {
        newUserProfile = new MutableLiveData<>();
        final InfRepo.UserInformation newInf = new InfRepo.UserInformation();

        storageRef.child(idProfile).child("AvatarImage").
                getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bmpImage = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                newInf.setBitmap(bmpImage);
            }
        });


        myRef.child(ConstValue.NAME_BRANCH).child(idProfile)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Profile profile = dataSnapshot.getValue(Profile.class);
                        newInf.setProfile(profile);

                        newUserProfile.postValue(newInf);
                        myRef.child(ConstValue.NAME_BRANCH).child(idProfile).removeEventListener(this);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        return;
                    }
                });

        return newUserProfile;
    }

    public void addSeenById (String id, final String newSeen) {
        if (id == null)
            id = user.getUid();

        final String finalId = id;
        myRef.child(ConstValue.NAME_BRANCH).child(id)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Profile profile = dataSnapshot.getValue(Profile.class);
                        ArrayList<String> seen = profile.getSeen();
                        if (seen == null)
                            seen = new ArrayList<>();
                        seen.add(newSeen);
                        profile.setSeen(seen);

                        myRef.child(ConstValue.NAME_BRANCH).child(finalId).setValue(profile);
                        myRef.child(ConstValue.NAME_BRANCH).child(finalId).removeEventListener(this);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    public void addLikeById(String id) {
        final String finalId = id;
        myRef.child(ConstValue.NAME_BRANCH).child(id)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Profile profile = dataSnapshot.getValue(Profile.class);
                        ArrayList<String> likes = profile.getLikes();
                        if (likes == null)
                            likes = new ArrayList<>();
                        likes.add(user.getUid());
                        profile.setLikes(likes);

                        myRef.child(ConstValue.NAME_BRANCH).child(finalId).setValue(profile);
                        myRef.child(ConstValue.NAME_BRANCH).child(finalId).removeEventListener(this);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    public  void addMatchesById (String id, final InfRepo.MainProfileInf newMatches) {
        if (id == null)
            id = user.getUid();
        if (newMatches.getId() == null)
            newMatches.setId(user.getUid());

        final String finalId = id;
        myRef.child(ConstValue.NAME_BRANCH).child(id)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Profile profile = dataSnapshot.getValue(Profile.class);

                        ArrayList<Profile.Matches> matches = profile.getMatches();
                        if (matches == null)
                            matches = new ArrayList<>();
                        matches.add(new Profile.Matches(newMatches.getId(), newMatches.getName(), "false"));
                        profile.setMatches(matches);

                        myRef.child(ConstValue.NAME_BRANCH).child(finalId).setValue(profile);
                        myRef.child(ConstValue.NAME_BRANCH).child(finalId).removeEventListener(this);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    public void removeLike (String id, final String removeLike) {
        if (id == null)
            id = user.getUid();
        final String finalId = id;
        myRef.child(ConstValue.NAME_BRANCH).child(id)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Profile profile = dataSnapshot.getValue(Profile.class);
                        ArrayList<String> likes = profile.getLikes();
                        if (likes.indexOf(removeLike) != -1)
                            likes.remove(likes.indexOf(removeLike));
                        profile.setLikes(likes);

                        myRef.child(ConstValue.NAME_BRANCH).child(finalId).setValue(profile);
                        myRef.child(ConstValue.NAME_BRANCH).child(finalId).removeEventListener(this);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

}
