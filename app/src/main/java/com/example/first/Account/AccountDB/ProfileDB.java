package com.example.first.Account.AccountDB;

import androidx.room.RoomDatabase;

public abstract class ProfileDB extends RoomDatabase {
    public abstract ProfileDao getProfileDao();
}
