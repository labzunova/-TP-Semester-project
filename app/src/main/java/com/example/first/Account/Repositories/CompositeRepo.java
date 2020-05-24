package com.example.first.Account.Repositories;

import androidx.lifecycle.LiveData;

public class CompositeRepo implements RepoDB {
    private LocalRepo localRepo;
    private AccountRepo accountRepo;

    public CompositeRepo() {
        localRepo = new LocalRepo();
        accountRepo = new AccountRepo();
    }
    @Override
    public LiveData getProfile() {
        LiveData liveData;
        liveData = localRepo.getProfile();

        if (liveData == null)
            liveData = accountRepo.getProfile();

        return liveData;
    }

    @Override
    public LiveData getImage() {
        LiveData liveData;
        liveData = localRepo.getImage();

        if (liveData == null)
            liveData = accountRepo.getImage();

        return liveData;
    }

    @Override
    public void exit() {
        accountRepo.exit();
    }
}
