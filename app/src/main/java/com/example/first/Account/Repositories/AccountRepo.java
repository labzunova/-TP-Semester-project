package com.example.first.Account.Repositories;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;

import com.example.first.AccountEdit.ProfileCash;
import com.example.first.Profile;
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

public class AccountRepo implements RepoDB{
    private static final String BRANCH = "Profiles";
    private static final String AVATAR_IMAGE = "AvatarImage";

    private DatabaseReference databaseProfile;
    private FirebaseUser user;
    private Profile profileData;
    private StorageReference storageRef;

    AccountRepo() {
        getUser();
        databaseProfile = FirebaseDatabase.getInstance().getReference(BRANCH);
        storageRef = FirebaseStorage.getInstance().getReference().child(BRANCH);
    }

    @Override
    public void getProfile(final CallbackProfile callback) {
        if (user == null)
            getUser();

        databaseProfile.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                profileData = dataSnapshot.getValue(Profile.class);
                callback.onSuccess(profileData);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.Error();
            }
        });
    }

    @Override
    public void getImage(final CallbackImage callback) {
        final long ONE_MEGABYTE = 1024*1024;

        if (user == null)
            getUser();

        storageRef.child(user.getUid()).child(AVATAR_IMAGE).getBytes(3 * ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                bitmap = resizeBitmap(bitmap);
                callback.onSuccess(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                callback.Error();
            }
        });
    }

    public void exit() {
        ProfileCash.getInstance().isEmpty = true;
        FirebaseAuth.getInstance().signOut();
        user = null;
    }

    private void getUser() {
        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    private Bitmap resizeBitmap(Bitmap bitmap) {
        float maxResolution = 600f;
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
}
