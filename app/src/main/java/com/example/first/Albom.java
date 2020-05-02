package com.example.first;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class Albom extends AppCompatActivity {

    private ImageView img;

    private FirebaseUser user;
    FirebaseStorage storage;
    StorageReference storageRef;
    private int count = 0;
    private int allCount;
    private List<StorageReference> galleryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_albom);

        img = findViewById(R.id.photo_albom);

        user = FirebaseAuth.getInstance().getCurrentUser();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference().child("Profiles").child(user.getUid());

        img.setOnTouchListener(new OnSwipeListener(Albom.this));

        storageRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                galleryList = listResult.getItems();
                allCount = galleryList.size();
                displayingPhotos(count);
            }
        });
    }

    class OnSwipeListener implements View.OnTouchListener {

        private  final GestureDetector gestureDetector;
        private static final int SWIPE_VELOCITY_THRESHOLD = 10;
        private static final int SWIPE_THRESHOLD = 10;

        public OnSwipeListener(Context context) {

                gestureDetector = new GestureDetector(context, new GestureListener());

        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return gestureDetector.onTouchEvent(event);
        }

        private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                float diffX = e2.getX() - e1.getX();

                if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) {
                        onSwipeRight();
                    } else {
                        onSwipeLeft();
                    }
                }
                return true;
            }

            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            public void onSwipeRight() {
                if (count >0) {
                    count--;
                    displayingPhotos(count);
                }

            }

            public void onSwipeLeft() {
                if (count <(allCount - 1)) {
                    count++;
                    displayingPhotos(count);
                }
            }

        }
    }

    public void displayingPhotos(int counter){
        final long THREE_MEGABYTE = 3 * 1024 * 1024;
        StorageReference photo = galleryList.get(counter);
        photo.getBytes(THREE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                img.setImageBitmap(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Albom.this, "No Such file or Path found!!", Toast.LENGTH_LONG).show();
            }
        });

    }
}