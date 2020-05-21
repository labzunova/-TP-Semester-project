package com.example.first.authorizationAndRegistration;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class FirebaseForAuth {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Context context;

    public FirebaseForAuth(Context context){
        this.context = context;
        mAuth = FirebaseAuth.getInstance();
        startListening();
        mAuth.addAuthStateListener(mAuthListener);
    }

    interface Auth {
        void goToAccount();
    }

    interface Toasts {
        void makeToast(String toast);
    }

    public void startSignIn(String email, String password) {
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            ((Toasts)context).makeToast("Fields are empty");
        } else {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()) {
                        ((Toasts)context).makeToast("Sign in problem");
                    }
                }
            });
        }
    }

    public void startListening(){
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    ((Auth)context).goToAccount(); // Start account activity cause user != null
                }
            }
        };
    }




}
