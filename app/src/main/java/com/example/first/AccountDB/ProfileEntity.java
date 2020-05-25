package com.example.first.AccountDB;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "myProfile")
public class ProfileEntity {
    public static final String DEFAULT_NUMBER = "def";

    @PrimaryKey
    @NonNull
    public String id = DEFAULT_NUMBER;
    public String name;
    public String email;
    public String phone;
    public String breed;
    public String age;
    public String country;
    public String city;
    public String address;

    public ProfileEntity() {
    }

    public ProfileEntity(String name, String email, String phone, String breed, String age, String country, String city, String address) {
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
