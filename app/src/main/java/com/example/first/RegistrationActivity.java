package com.example.first;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RegistrationActivity extends AppCompatActivity {

    // layout references
    private Button mRegisterBtn;
    private TextInputEditText mEmailField;
    private TextInputEditText mPasswordField;
    private TextInputEditText mNameField;

    // firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseStorage storage;
    StorageReference storageRef;

    private String str = new String("");

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        mEmailField = (TextInputEditText) findViewById(R.id.emailFieldInp);
        mPasswordField = (TextInputEditText) findViewById(R.id.passwordFieldInp);
        mRegisterBtn = (Button) findViewById(R.id.registerBtn);
        mNameField = (TextInputEditText) findViewById(R.id.nameFieldInp);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                final DatabaseReference myRef = database.getReference();
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String name = mNameField.getText().toString();
                String email = mEmailField.getText().toString();

                if (firebaseAuth.getCurrentUser() != null) {

                    Profile profile = new Profile();
                    profile.setName(name);
                    profile.setEmail(email);
                    profile.setPhone(str);
                    profile.setAddress(str);
                    profile.setAge(str);
                    profile.setCountry(str);
                    profile.setCity(str);
                    profile.setBreed(str);
                    ArrayList<String> seen = new ArrayList<>();
                    seen.add(user.getUid());
                    profile.setSeen(seen);

                    // add in seen new user
//                    Intent intentService;
//                    intentService = new Intent(RegistrationActivity.this, RegistrationService.class);
//
//                    startService(intentService);

                    myRef.child("Profiles").child(user.getUid()).setValue(profile);

                    myRef.child("IdProfiles").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            ArrayList<String> idProfiles = new ArrayList<>();
                            for (DataSnapshot postSnapshot: dataSnapshot.getChildren ()) {
                                idProfiles.add(postSnapshot.getValue(String.class));
                            }
                            idProfiles.add(user.getUid());
                            myRef.child("IdProfiles").setValue(idProfiles);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                    // uploading default avatar to firebase storage
                    // Uri uri = Uri.parse("/Users/Ivan/AndroidStudioProjects/DashasFirebase/-TP-Semester-project/app/src/main/res/drawable/dog.jpg");
                    Uri imageUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                            "://" + getResources().getResourcePackageName(R.drawable.default_avatar)
                            + '/' + getResources().getResourceTypeName(R.drawable.default_avatar) + '/' + getResources().getResourceEntryName(R.drawable.default_avatar) );
                    StorageReference ref = storageRef.child("Profiles").child(user.getUid()).child("AvatarImage");
                    ref.putFile(imageUri);


                  //  startActivity(new Intent(RegistrationActivity.this, AccountActivity.class));
                    Intent intent = new Intent(RegistrationActivity.this, AccountActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        };

        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRegister();
            }
        });
    }

    private void startRegister() {
        String email = mEmailField.getText().toString();
        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) ) {
            Toast.makeText(RegistrationActivity.this, "Fields are empty", Toast.LENGTH_LONG).show(); } else
                mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(RegistrationActivity.this, "Sign in problem", Toast.LENGTH_LONG).show();
                        }
                    }
                });
                }
    }

