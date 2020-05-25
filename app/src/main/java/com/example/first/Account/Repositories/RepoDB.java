package com.example.first.Account.Repositories;

import android.graphics.Bitmap;

import androidx.annotation.MainThread;

import com.example.first.Profile;

public interface RepoDB {
     void getProfile(CallbackProfile callback);
     void getImage(CallbackImage callback);
     void exit();

     interface CallbackProfile {
          @MainThread
          void onSuccess(Profile profile);
          void notFound();
          @MainThread
          void Error();
     }

     interface CallbackImage {
          @MainThread
          void onSuccess(Bitmap bitmap);
          void notFound();
          @MainThread
          void Error();
     }
}
