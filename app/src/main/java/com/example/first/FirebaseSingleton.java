package com.example.first;

import android.app.Activity;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import static androidx.core.content.ContextCompat.startActivity;

public class FirebaseSingleton {
    private static final String TAG = "FirebaseSingleton";
    private static FirebaseSingleton firebaseSingleton = null;

    private FirebaseStorage storage;
    private StorageReference storageReference;
    private Context appContext;

    private FirebaseSingleton() {
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        appContext = ApplicationCustom.get();
    }

    public static FirebaseSingleton getInstance() {
        if (firebaseSingleton == null) {
            firebaseSingleton = new FirebaseSingleton();
        }
        return firebaseSingleton;
    }


}
