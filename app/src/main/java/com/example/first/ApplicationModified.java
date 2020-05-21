package com.example.first;

import android.app.Application;
import android.content.Context;

import com.example.first.mainScreen.InfRepo;
import com.example.first.mainScreen.Storage.NetworkData;

public class ApplicationModified extends Application {
    private InfRepo mInfRepo;
    private NetworkData networkData;

    @Override
    public void onCreate() {
        super.onCreate();
        networkData = new NetworkData();
        mInfRepo = new InfRepo(networkData);
    }

    public InfRepo getInfRepo() {
        return mInfRepo;
    }

    public static  ApplicationModified from(Context context) {
        return (ApplicationModified) context.getApplicationContext();
    }


}
