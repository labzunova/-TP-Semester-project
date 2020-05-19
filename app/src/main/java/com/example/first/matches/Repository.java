package com.example.first.matches;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;


public class Repository {

    static Repository instance;
    FirebaseStorage storage;
    StorageReference storageRef;
    final long BATCH_SIZE = 1024 * 1024;
    private ArrayList<UserModel> matches = new ArrayList<>();
    private MutableLiveData<ArrayList<UserModel>> dog = new MutableLiveData<>();

    public static Repository getInstance() {
        if(instance == null){
            instance = new Repository();
        }
        return instance;
    }

    public MutableLiveData<ArrayList<UserModel>> getMatches() {
        if (matches.size() == 0 ) {
            loadMatches();
            //loadPhotos();
        }
        dog.setValue(matches);
        return dog;
    }

   /* public void loadPhotos() {

        for (int i = 0; i < matches.size(); i++) {
            UserModel user = matches.get(i);
            storage = FirebaseStorage.getInstance();
            storageRef = storage.getReference();
            StorageReference myRef = storageRef.child("Profiles").child(user.id).child("AvatarImage");
            myRef.getBytes(BATCH_SIZE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    //holder.photoView.setImageBitmap(bmp);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                }
            });
        }
    }*/

    private void loadMatches() {
        final FirebaseUser user;
        user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref = reference.child("Profiles").child(user.getUid()).child("matches");

        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                matches.add(dataSnapshot.getValue(UserModel.class));
                dog.postValue(matches);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                UserModel model = dataSnapshot.getValue(UserModel.class);
                int index = getItemIndex(model);
                matches.set(index,model);
                dog.postValue(matches);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                UserModel model = dataSnapshot.getValue(UserModel.class);
                int index = getItemIndex(model);
                matches.remove(index);
                dog.postValue(matches);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private int getItemIndex(UserModel user){
        int index = -1;
        for(int i = 0; i< matches.size(); i++) {
            if(matches.get(i).id.equals(user.id)) {
                index = i;
                break;
            }
        }
        return index;
    }

}
