package com.example.first.Account.Repositories;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.first.Account.AccountDB.DatabaseHelper;
import com.example.first.Profile;

public class LocalRepo implements RepoDB,  {
    Context context;

    MutableLiveData<Profile> profileLiveData = new MutableLiveData<>();
    MutableLiveData<Bitmap> imageLiveData = new MutableLiveData<>();

    LocalRepo(Context context) {
        this.context = context;
    }

    @Override
    public LiveData getProfile() {
        DatabaseHelper.getInstance(context).getProfileDB().getProfileDao().getById()

        return profileLiveData;
    }

    @Override
    public LiveData getImage() {
        return null;
    }

    @Override
    public void exit() {

    }
}
