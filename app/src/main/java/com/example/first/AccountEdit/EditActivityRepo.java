package com.example.first.AccountEdit;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.first.Profile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class EditActivityRepo {
    private static final String TAG = "EditAccountActivity";
    private static final int INITIAL_QUALITY = 100;

    private ProfileCash mProfileCash;

    private MutableLiveData<EditActivityViewModel.AvatarImage> userImage = new MutableLiveData<>();
    private MutableLiveData<EditActivityViewModel.ProfileInfo> userInfo = new MutableLiveData<>();

    private DatabaseReference userProfileRef;
    private StorageReference userStorageRef;

    private static final EditActivityRepo sInstance = new EditActivityRepo();
    private EditActivityRepo() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userProfileRef = FirebaseDatabase.getInstance().getReference("Profiles").child(user.getUid());
        userStorageRef = FirebaseStorage.getInstance().getReference().child("Profiles").child(user.getUid());

        // получаем кэш
        mProfileCash = ProfileCash.getInstance();
    }

    public static EditActivityRepo getInstance() {
        return sInstance;
    }

    public LiveData<EditActivityViewModel.AvatarImage> getUserImage() {
        return userImage;
    }

    public LiveData<EditActivityViewModel.ProfileInfo> getUserInfo() {
        return userInfo;
    }

    public void refreshUserCash() {
        // проверка, есть ли что то в кэше - потом сделать проверку нормальной (не за счет вспомогательной пер. isEmpty)
        if (mProfileCash.isEmpty) {
            Log.d(TAG, "refreshUserCash: mProfileCash.isEmpty == true");
            updateUserCash();
        } else {
            Log.d(TAG, "refreshUserCash: mProfileCash.isEmpty == false");
            userInfo.postValue(mProfileCash.getProfileData());
            userImage.postValue(mProfileCash.getProfileImage());
        }
    }

    private void updateUserCash() {
        // получение текущих данных с Database
        Log.d(TAG, "updateUserCash()");
        userProfileRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "updateUserCash: onDataChange()");
                // получение данных текущего пользователя
                Profile profile = dataSnapshot.getValue(Profile.class);

                if (profile != null) {
                    Log.d(TAG, "updateUserCash: onDataChange(): profile != null");
                    mProfileCash.setProfileData(profile);
                    userInfo.postValue(mProfileCash.getProfileData());
                } else {
                    Log.d(TAG, "onDataChange: updateUserCash() profile is null");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: updateUserCash()");
            }
        });

        // получение аватарки со Storage
        final long BATCH_SIZE = 1024 * 1024; // 1 mb
        StorageReference avatarRef = userStorageRef.child("AvatarImage");
        avatarRef.getBytes(BATCH_SIZE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Log.d(TAG, "updateUserCash: AvatarImage getBytes onSuccess()");
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                bitmap = resizeBitmap(bitmap, 600.0f);

                mProfileCash.setAvatarBitmap(bitmap);
                userImage.postValue(mProfileCash.getProfileImage());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "updateUserCash: AvatarImage getBytes onFailure()");
            }
        });
    }

    public void updateAvatarImageCashe(Bitmap bitmap) {
        //bitmap = resizeBitmap(bitmap, 600.0f);
        
        // обновление кэша
        Log.d(TAG, "updateAvatarImageCashe: ");
        mProfileCash.setAvatarBitmap(bitmap);
    }

    public void uploadAvatarImage() {
        // загрузка в firebase
        Log.d(TAG, "uploadAvatarImage: ");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        mProfileCash.getAvatarBitmap().compress(Bitmap.CompressFormat.JPEG, (int)(INITIAL_QUALITY*0.6), baos);
        byte[] bytes = baos.toByteArray();
        StorageReference ref = userStorageRef.child("AvatarImage");
        ref.putBytes(bytes).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                Log.d(TAG, "onComplete: in uploadAvatarImage()");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: in uploadAvatarImage()");
            }
        });
    }

    public void uploadProfileData(EditActivityViewModel.ProfileInfo profileInfo) {
        // обновление кэша
        mProfileCash.setProfileData(profileInfo);

        // загрузка в firebase
        // такой хардкод из-за несовершенства структуры хранения данных в Firebase, это поправляется
        userProfileRef.child("name").setValue(profileInfo.getName());
        userProfileRef.child("phone").setValue(profileInfo.getPhone());
        userProfileRef.child("breed").setValue(profileInfo.getBreed());
        userProfileRef.child("age").setValue(profileInfo.getAge());
        userProfileRef.child("country").setValue(profileInfo.getCountry());
        userProfileRef.child("city").setValue(profileInfo.getCity());
        userProfileRef.child("address").setValue(profileInfo.getAddress());
    }

    public static Bitmap resizeBitmap(Bitmap bitmap, float maxResolution) {
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
