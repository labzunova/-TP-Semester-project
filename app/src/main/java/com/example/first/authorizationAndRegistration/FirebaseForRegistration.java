package com.example.first.authorizationAndRegistration;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.example.first.Profile;
import com.example.first.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
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

public class FirebaseForRegistration {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Context context;
    FirebaseStorage storage;
    StorageReference storageRef;
    private String email;

    public FirebaseForRegistration(Context context){
        this.context = context;
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        startListening();
        mAuth.addAuthStateListener(mAuthListener);
    }

    interface Auth {
        void goToAccount();
    }

    interface Toasts {
        void makeToast(String toast);
    }


    public void startRegister(String email, String password) {
        this.email = email;
        if ((email.equals("")) || (password.equals(""))) {
            ((Toasts)context).makeToast("Fields are empty");
        }
            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()) {
                        ((FirebaseForRegistration.Toasts)context).makeToast("Sign in problem");
                    }
                }
            });
    }

    public void startListening() {
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    databaseFilling();
                    ((FirebaseForRegistration.Auth) context).goToAccount(); // Start account activity cause user != null
                }
            }
        };
    }

    public void databaseFilling() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        Profile profile = new Profile();
        String str = "";
        profile.setName(str);
        profile.setEmail(email);
        profile.setPhone(str);
        profile.setAddress(str);
        profile.setAge(str);
        profile.setCountry(str);
        profile.setCity(str);
        profile.setBreed(str);
        ArrayList<String> seen = new ArrayList<>();
        seen.add(user.getUid());
        profile.setSeen(seen);

        myRef.child("Profiles").child(user.getUid()).setValue(profile);

        myRef.child("IdProfiles").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> idProfiles = new ArrayList<>();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren ()) {
                    idProfiles.add(postSnapshot.getValue(String.class));
                }
                idProfiles.add(user.getUid());
                myRef.child("IdProfiles").setValue(idProfiles);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // uploading default avatar to firebase storage
        Uri imageUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                "://" + context.getResources().getResourcePackageName(R.drawable.default_avatar)
                + '/' + context.getResources().getResourceTypeName(R.drawable.default_avatar) + '/' + context.getResources().getResourceEntryName(R.drawable.default_avatar) );
        StorageReference ref = storageRef.child("Profiles").child(user.getUid()).child("AvatarImage");
        ref.putFile(imageUri);
    }

}
