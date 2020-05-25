package com.example.first.Account.Repositories;

import android.content.Context;
import android.graphics.Bitmap;

import com.example.first.Profile;

public class CompositeRepo implements RepoDB {
    private LocalRepo localRepo;
    private AccountRepo accountRepo;

    public CompositeRepo(Context context) {
        localRepo = new LocalRepo(context);
        accountRepo = new AccountRepo();
    }


    @Override
    public void getProfile(final CallbackProfile callback) {
        localRepo.getProfile(new CallbackProfile() {
            @Override
            public void onSuccess(Profile profile) {
                callback.onSuccess(profile);
            }

            @Override
            public void notFound() {
                accountRepo.getProfile(callback);
            }

            @Override
            public void Error() {
                accountRepo.getProfile(callback);
            }
        });
    }

    @Override
    public void getImage(final CallbackImage callback) {
        localRepo.getImage(new CallbackImage() {
            @Override
            public void onSuccess(Bitmap bitmap) {
                callback.onSuccess(bitmap);
            }

            @Override
            public void notFound() {
                accountRepo.getImage(callback);
            }

            @Override
            public void Error() {
                accountRepo.getImage(callback);
            }
        });
    }

    @Override
    public void exit() {
        localRepo.exit();
        accountRepo.exit();
    }
}
