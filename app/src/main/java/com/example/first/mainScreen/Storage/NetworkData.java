package com.example.first.mainScreen.Storage;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.first.Matches;
import com.example.first.Profile;
import com.example.first.mainScreen.InfRepo;
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

public class NetworkData {
    public static final String NAME_BRANCH = "Profiles";
    public static final String BRANCH_ID_PROFILES = "IdProfiles";
    public static final String BRANCH_SEEN = "seen";
    public static final String BRANCH_LIKES = "likes";
    public static final String BRANCH_MATCHES = "matches";

    private DatabaseReference myRef;
    private FirebaseUser user;
    private StorageReference storageRef;

    private final long ONE_MEGABYTE = 1024 * 1024;

    private MutableLiveData<InfRepo.UserInformation> newUserProfile;

    public NetworkData(){
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        storageRef = FirebaseStorage.getInstance().getReference().child(NAME_BRANCH);
    }

    public interface InfListener {
        void setMyProfile(InfRepo.UserInformation user);
        void setIdProfiles(ArrayList<String> idProfiles);
    }

    public void Connect(@NonNull final InfListener repo) {
        if (user != null) {
            // get myProfile
            myRef.child(NAME_BRANCH).child(user.getUid())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            InfRepo.UserInformation inf = new InfRepo.UserInformation();
                            inf.id = user.getUid();
                            inf.profile = dataSnapshot.getValue(Profile.class);
                            if (inf.profile != null)
                                inf.name = inf.profile.getName();
                            repo.setMyProfile(inf);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

            // get id all Profiles
            myRef.child(BRANCH_ID_PROFILES)
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
        final InfRepo.UserInformation newInfo = new InfRepo.UserInformation();
        newInfo.id = idProfile;

        myRef.child(NAME_BRANCH).child(idProfile)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Profile profile = dataSnapshot.getValue(Profile.class);
                        newInfo.profile = profile;
                        if (profile != null)
                            newInfo.name = newInfo.profile.getName();

                        storageRef.child(idProfile).child("AvatarImage").
                                getBytes(5 * ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                            @Override
                            public void onSuccess(byte[] bytes) {
                                Bitmap bmpImage = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                bmpImage = resizeBitmap(bmpImage);

                                newInfo.bitmap = bmpImage;

                                newUserProfile.postValue(newInfo);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                newUserProfile.postValue(newInfo);
                            }
                        });

                        myRef.child(NAME_BRANCH).child(idProfile).removeEventListener(this);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });

        return newUserProfile;
    }


    private Bitmap resizeBitmap(Bitmap bitmap) {
        float maxResolution = (float) 400.0;    //edit 'maxResolution' to fit your need
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int newWidth = width;
        int newHeight = height;
        float rate;

        if (width > height) {
            if (maxResolution < width) {
                rate = maxResolution / width;
                newHeight = (int) (height * rate);
                newWidth = (int) maxResolution;
            }
        } else {
            if (maxResolution < height) {
                rate = maxResolution / height;
                newWidth = (int) (width * rate);
                newHeight = (int) maxResolution;
            }
        }

        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
    }



    public void addSeenById (@NonNull final String id, final String newSeen) {
        myRef.child(NAME_BRANCH).child(id)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Profile profile = dataSnapshot.getValue(Profile.class);
                        ArrayList<String> seen = profile.getSeen();
                        if (seen == null)
                            seen = new ArrayList<>();
                        seen.add(newSeen);

                        myRef.child(NAME_BRANCH).child(id).child(BRANCH_SEEN).setValue(seen);
                        myRef.child(NAME_BRANCH).child(id).removeEventListener(this);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    public void addLikeById(@NonNull final String id, final String newLike) {
        myRef.child(NAME_BRANCH).child(id)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Profile profile = dataSnapshot.getValue(Profile.class);
                        ArrayList<String> likes = profile.getLikes();
                        if (likes == null)
                            likes = new ArrayList<>();
                        likes.add(newLike);

                        myRef.child(NAME_BRANCH).child(id).child(BRANCH_LIKES).setValue(likes);
                        myRef.child(NAME_BRANCH).child(id).removeEventListener(this);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    public  void addMatchesById (@NonNull final String id, final Matches newMatch) {
        myRef.child(NAME_BRANCH).child(id)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Profile profile = dataSnapshot.getValue(Profile.class);

                        ArrayList<Matches> matches = profile.getMatches();
                        if (matches == null)
                            matches = new ArrayList<>();
                        matches.add(newMatch);

                        myRef.child(NAME_BRANCH).child(id).child(BRANCH_MATCHES).setValue(matches);
                        myRef.child(NAME_BRANCH).child(id).removeEventListener(this);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    public void removeLike (@NonNull final String id, final String removeLike) {
        myRef.child(NAME_BRANCH).child(id)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Profile profile = dataSnapshot.getValue(Profile.class);
                        ArrayList<String> likes = profile.getLikes();
                        if (likes == null)
                            likes = new ArrayList<>();
                        if (likes.indexOf(removeLike) != -1)
                            likes.remove(likes.indexOf(removeLike));

                        myRef.child(NAME_BRANCH).child(id).child(BRANCH_LIKES).setValue(likes);
                        myRef.child(NAME_BRANCH).child(id).removeEventListener(this);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

}
