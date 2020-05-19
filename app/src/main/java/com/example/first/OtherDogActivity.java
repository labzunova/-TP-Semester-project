package com.example.first;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class OtherDogActivity extends AppCompatActivity {

    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_dog);
        id = getIntent().getStringExtra("id");
    }
}
