package com.example.first;

import android.app.Application;
import android.content.Context;

import com.example.first.mainScreen.InfRepo;

public class ApplicationModified extends Application {
    private InfRepo mInfRepo;

    @Override
    public void onCreate() {
        super.onCreate();
        mInfRepo = new InfRepo();
    }

    public InfRepo getInfRepo() {
        return mInfRepo;
    }

    public static  ApplicationModified from(Context context) {
        return (ApplicationModified) context.getApplicationContext();
    }


}
