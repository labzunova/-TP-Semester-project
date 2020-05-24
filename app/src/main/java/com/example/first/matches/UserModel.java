package com.example.first.matches;
import java.io.Serializable;

public class UserModel implements Serializable {
    public String id, name, seen;
    public UserModel(){}
    public UserModel(String id,  String name, String seen) {
        this.id = id;
        this.name = name;
        this.seen = seen;
    }
}