package com.example.first;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
//import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
//import android.widget.Button;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

//import com.facebook.drawee.backends.pipeline.Fresco;
//import com.facebook.drawee.view.SimpleDraweeView;
//import com.facebook.imagepipeline.request.ImageRequest;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class AccountActivity extends AppCompatActivity {
    private ImageView imgSetting, imgExit, imgScroll, imgMatch, photoProfil;
    private TextView labName, labEmail, labPhone, labBreed, labAge, labCountry, labCity, labAddres, progressText;
    private ProgressBar progressBar;
    private LinearLayout layoutPhone, layoutBreed, layoutAge, layoutCountry, layoutCity, layoutAddres, layoutMail;
    private boolean isImageScaled = false;
    private String str = new String("");




    private DatabaseReference databaseProfile;
    private FirebaseUser user;
    private String userId;
    private Profile currentUserProfile;
    private Uri filepath;
    FirebaseStorage storage;
    StorageReference storageRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();

        databaseProfile = FirebaseDatabase.getInstance().getReference("Profiles");
        user = FirebaseAuth.getInstance().getCurrentUser();
        userId = user.getUid();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        imgSetting = findViewById(R.id.edit);
        imgExit = findViewById(R.id.exit);
        imgScroll = findViewById(R.id.to_str_scroll);
        imgMatch = findViewById(R.id.match);

        labName = findViewById(R.id.i_name);
        labEmail = findViewById(R.id.i_email);
        labPhone = findViewById(R.id.i_phone);
        labBreed = findViewById(R.id.i_breed);
        labAge = findViewById(R.id.i_age);
        labCountry = findViewById(R.id.i_country);
        labCity = findViewById(R.id.i_city);
        labAddres = findViewById(R.id.i_addres);

        photoProfil = findViewById(R.id.photo_profil);

        progressBar = findViewById(R.id.progress);
        progressText = findViewById(R.id.progress_text);

        layoutPhone = (LinearLayout) findViewById(R.id.layout_telephone);
        layoutBreed = (LinearLayout) findViewById(R.id.layout_breed);
        layoutAge = (LinearLayout) findViewById(R.id.layout_age);
        layoutCountry = (LinearLayout) findViewById(R.id.layout_country);
        layoutCity = (LinearLayout) findViewById(R.id.layout_city);
        layoutAddres = (LinearLayout) findViewById(R.id.layout_addres);
        layoutMail = (LinearLayout) findViewById(R.id.layout_mail);

        imgExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* FirebaseAuth.getInstance().signOut();
               // startActivity(new Intent(AccountActivity.this,AuthorizationActivity.class));
                Intent intent = new Intent(AccountActivity.this, AuthorizationActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent); */

                final Dialog dialog = new Dialog(AccountActivity.this);
                dialog.setContentView(R.layout.exit_dialog);
                Button btYes = dialog.findViewById(R.id.btn_yes);
                Button btNo = dialog.findViewById(R.id.btn_no);
                btNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                btYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FirebaseAuth.getInstance().signOut();
                        // startActivity(new Intent(AccountActivity.this,AuthorizationActivity.class));
                        Intent intent = new Intent(AccountActivity.this, AuthorizationActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                });
                dialog.show();
            }
        });

        photoProfil.setOnClickListener(new View.OnClickListener() {
             @Override
               public void onClick(View v) {
                 startActivity(new Intent(AccountActivity.this, Albom.class));

                }
             });

        imgSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AccountActivity.this, AccountEditActivity.class));
            }
        });

        imgScroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AccountActivity.this, MainActivity.class));
            }
        });

        imgMatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AccountActivity.this, MatchesActivity.class));
            }
        });

        databaseProfile.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentUserProfile = dataSnapshot.child(userId).getValue(Profile.class);

                labName.setText(currentUserProfile.getName());

                if (currentUserProfile.getEmail() != null && !currentUserProfile.getEmail().equals(str) ){
                    labEmail.setText(currentUserProfile.getEmail());
                } else
                      layoutMail.setVisibility(View.GONE);


                if ( currentUserProfile.getBreed() != null && !currentUserProfile.getBreed().equals(str) ){
                    labBreed.setText(currentUserProfile.getBreed());
                } else
                         layoutBreed.setVisibility(View.GONE);

                  if (currentUserProfile.getAge() != null && !currentUserProfile.getAge().equals(str) ){
                  labAge.setText(currentUserProfile.getAge());
                } else
                      layoutAge.setVisibility(View.GONE);

                if (currentUserProfile.getCountry() != null && !currentUserProfile.getCountry().equals(str) ){
                   labCountry.setText(currentUserProfile.getCountry());
                } else
                      layoutCountry.setVisibility(View.GONE);

                if (currentUserProfile.getCity() != null && !currentUserProfile.getCity().equals(str) ){
                   labCity.setText(currentUserProfile.getCity());
                } else
                    layoutCity.setVisibility(View.GONE);

                if (currentUserProfile.getAddress() != null && !currentUserProfile.getAddress().equals(str) ){
                     labAddres.setText(currentUserProfile.getAddress());
                } else
                    layoutAddres.setVisibility(View.GONE);

                if (currentUserProfile.getPhone() != null && !currentUserProfile.getPhone().equals(str) ){
                  labPhone.setText(currentUserProfile.getPhone());
               } else
                layoutPhone.setVisibility(View.GONE);

                     if (currentUserProfile.getLikes().size()<5){
                         progressBar.setProgress(35);
                         progressText.setText("низкая популярность");
                       } else
                     if(currentUserProfile.getLikes().size()<11){
                         progressBar.setProgress(55);
                         progressText.setText("средняя популярность");
                       } else {
                         progressBar.setProgress(85);
                         progressText.setText("высокая популярность");
                     }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final long ONE_MEGABYTE = 1024*1024;
        StorageReference avatarPhoto =  storageRef.child("Profiles").child(userId).child("AvatarImage"); //берем из storage
        avatarPhoto.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                photoProfil.setImageBitmap(AccountEditActivity.resizeBitmap(bitmap, 1000.0f));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                photoProfil.setImageResource(R.drawable.dog);


            }
        });
    }



}
