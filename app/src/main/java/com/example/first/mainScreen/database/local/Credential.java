package com.example.first.mainScreen.database.local;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

@Entity
public class Credential {
    @PrimaryKey @NonNull
    public String id = "";
    public String name;
    String email;
    String phone;
    String breed;
    String age;
    String country;
    String city;
    String address;

    @TypeConverters({ConverterData.class})
    public Bitmap bitmap;

    Credential() {
    }

    @Ignore
    Credential(@NonNull String id, String name, String email, String phone, String breed, String age, String country, String city, String address) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.breed = breed;
        this.age = age;
        this.country = country;
        this.city = city;
        this.address = address;
    }
}
