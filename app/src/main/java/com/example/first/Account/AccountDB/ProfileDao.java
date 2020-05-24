package com.example.first.Account.AccountDB;

import androidx.room.Dao;
import androidx.room.Query;

@Dao
public interface ProfileDao {
    @Query("SELECT * FROM myProfile WHERE id = :id")
    ProfileEntity getById(String id);
}
