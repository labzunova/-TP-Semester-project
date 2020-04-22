package com.example.first;

import android.widget.ProgressBar;

import java.util.ArrayList;

public class Profile {
    private String name;
    private String email;
    private String phone;
    private String breed;
    private String age;
    private String country;
    private String city;
    private String address;
    private ArrayList<String> likes;
    private ArrayList<String> matches;
    private ArrayList<String> seen;

    Profile () {

    }

    public Profile (String name, String email, String phone,
                    String breed, String age, String country,
                    String city, String address,
                    ArrayList<String> likes, ArrayList<String> matches, ArrayList<String> seen) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.breed = breed;
        this.age = age;
        this.country = country;
        this.city = city;
        this.address = address;
        this.likes = likes;
        this.matches = matches;
        this.seen = seen;
    }


    // getters
    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getBreed() {
        return breed;
    }

    public String getAge() {
        return age;
    }

    public String getCountry() {
        return country;
    }

    public String getCity() {
        return city;
    }

    public String getAddress() {
        return address;
    }

    public ArrayList<String> getLikes() { return likes; }

    public ArrayList<String> getMatches() { return matches; }

    public ArrayList<String> getSeen() { return seen; }


    // setters
    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setLikes(ArrayList<String> likes) { this.likes = likes; }

    public void setMatches(ArrayList<String> matches) { this.matches = matches; }

    public void setSeen(ArrayList<String> seen) { this.seen = seen; }
}
