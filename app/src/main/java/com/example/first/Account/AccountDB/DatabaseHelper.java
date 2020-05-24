package com.example.first.Account.AccountDB;

import android.content.Context;
import androidx.room.Room;

public class DatabaseHelper {
    private static DatabaseHelper databaseHelper;
    private final ProfileDB profileDB;

    public static DatabaseHelper getInstance(Context context) {
        if(databaseHelper == null) {
            databaseHelper = new DatabaseHelper(context);
        }
        return databaseHelper;
    }

    DatabaseHelper(Context context) {
        profileDB = Room.databaseBuilder(context, ProfileDB.class, "profileInfo.db")
                .build();
    }

    public ProfileDB getProfileDB() {
        return profileDB;
    }
}
