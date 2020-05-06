package com.example.first;

import android.app.Application;

public class ApplicationCustom extends Application {
    private static ApplicationCustom instance;
    public static ApplicationCustom get() { return instance; }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }
}
