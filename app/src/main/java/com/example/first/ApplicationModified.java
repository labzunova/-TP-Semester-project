package com.example.first;

import android.app.Application;
import android.content.Context;

import com.example.first.mainScreen.database.CompositeDatabase;
import com.example.first.mainScreen.database.local.LocalDatabase;
import com.example.first.mainScreen.database.network.NetworkDatabase;
import com.example.first.mainScreen.repositories.InfoRepo;

public class ApplicationModified extends Application {
    private InfoRepo mInfRepo;
    private NetworkDatabase networkDatabase;
    private LocalDatabase localDatabase;
    private CompositeDatabase compositeDatabase;

    @Override
    public void onCreate() {
        super.onCreate();
        networkDatabase = new NetworkDatabase();
        localDatabase = new LocalDatabase(getApplicationContext(), networkDatabase);
        compositeDatabase = new CompositeDatabase(localDatabase, networkDatabase);
        mInfRepo = new InfoRepo(compositeDatabase);
    }

    public InfoRepo getInfRepo() {
        return mInfRepo;
    }

    public static  ApplicationModified from(Context context) {
        return (ApplicationModified) context.getApplicationContext();
    }


}
