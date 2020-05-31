package com.example.first.ProfilesDB;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface CacheProfilesDao {

    @Query("SELECT * FROM ProfileEntity")
    List<ProfileEntity> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void change(ProfileEntity profileEntity);

    @Delete
    void delete(ProfileEntity profileEntity);

    @Query("DELETE FROM ProfileEntity")
    void deleteAll();
}
