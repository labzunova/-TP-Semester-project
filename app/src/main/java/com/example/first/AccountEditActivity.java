package com.example.first;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class AccountEditActivity extends AppCompatActivity {
    // constants
    private final static int PICK_IMAGE_REQUEST = 71;

    // layout references
    private TextInputEditText mNameField;
    private TextInputEditText mEmailField;
    private TextInputEditText mBreedField;
    private TextInputEditText mAgeField;
    private TextInputEditText mCountryField;
    private TextInputEditText mCityField;
    private TextInputEditText mAddressField;
    private TextInputEditText mPhoneField;
    private Button saveBtn, chooseBtn, uploadBtn;
    private ImageView imgPreview;

    // firebase
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
        setContentView(R.layout.activity_account_edit);

        // firebase init
        databaseProfile = FirebaseDatabase.getInstance().getReference("Profiles"); // Expected to be automatically created if Profiles node not yet created
        user = FirebaseAuth.getInstance().getCurrentUser();
        userId = user.getUid();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        // init views
        mNameField = (TextInputEditText) findViewById(R.id.nameFieldInp);
        mEmailField = (TextInputEditText) findViewById(R.id.emailFieldInp);
        mBreedField = (TextInputEditText) findViewById(R.id.dogBreedFieldInp);
        mAgeField = (TextInputEditText) findViewById(R.id.dogAgeFieldInp);
        mCountryField = (TextInputEditText) findViewById(R.id.countryFieldInp);
        mCityField = (TextInputEditText) findViewById(R.id.cityFieldInp);
        mAddressField = (TextInputEditText) findViewById(R.id.addressFieldInp);
        mPhoneField = (TextInputEditText) findViewById(R.id.phoneFieldInp);
        saveBtn = (Button) findViewById(R.id.saveBtn);
        chooseBtn = (Button) findViewById(R.id.chooseBtn);
        uploadBtn = (Button) findViewById(R.id.uploadBtn);
        imgPreview = (ImageView) findViewById(R.id.imageView);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
            }
        });

        // получение текущих данных с Database
        databaseProfile.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // получение данных текущего пользователя
                currentUserProfile = dataSnapshot.child(userId).getValue(Profile.class);

                mNameField.setText(currentUserProfile.getName());
                mEmailField.setText(currentUserProfile.getEmail());
                mBreedField.setText(currentUserProfile.getBreed());
                mAgeField.setText(currentUserProfile.getAge());
                mCountryField.setText(currentUserProfile.getCountry());
                mCityField.setText(currentUserProfile.getCity());
                mAddressField.setText(currentUserProfile.getAddress());
                mPhoneField.setText(currentUserProfile.getPhone());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // Загрузка текущих файлов со Storage
        /*
        StorageReference avatarRef = storageRef.child("Profiles").child(userId).child("AvatarImage");
        final Uri[] imgUri = new Uri[1];
        avatarRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                imgUri[0] = uri;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
        avatarRef.getFile(imgUri[0]);
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imgUri[0]);
            imgPreview.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }

         */

        final long ONE_MEGABYTE = 1024 * 1024;
        StorageReference avatarRef = storageRef.child("Profiles").child(userId).child("AvatarImage");
        avatarRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                imgPreview.setImageBitmap(bmp);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "No Such file or Path found!!", Toast.LENGTH_LONG).show();
            }
        });



        chooseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });


    }

    private void uploadImage() {
        if (filepath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading..");
            progressDialog.show();

            StorageReference ref = storageRef.child("Profiles").child(userId).child("AvatarImage");
            ref.putFile(filepath).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    progressDialog.dismiss();
                    Toast.makeText(AccountEditActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(AccountEditActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            })
            .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    progressDialog.setMessage("Uploaded " + (int)progress + "%");
                }
            });
        }
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filepath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filepath);
                imgPreview.setImageBitmap(bitmap);
                imgPreview.setRotation((float) 90.0);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveData() {
        String name = mNameField.getText().toString();

        if (!mNameField.getText().toString().equals(currentUserProfile.getName()))
            currentUserProfile.setName(mNameField.getText().toString());

        if (!mEmailField.getText().toString().equals(currentUserProfile.getEmail()))
            currentUserProfile.setEmail(mEmailField.getText().toString());

        if (!mBreedField.getText().toString().equals(currentUserProfile.getBreed()))
            currentUserProfile.setBreed(mBreedField.getText().toString());

        if (!mAgeField.getText().toString().equals(currentUserProfile.getAge()))
            currentUserProfile.setAge(mAgeField.getText().toString());

        if (!mCountryField.getText().toString().equals(currentUserProfile.getCountry()))
            currentUserProfile.setCountry(mCountryField.getText().toString());

        if (!mCityField.getText().toString().equals(currentUserProfile.getCity()))
            currentUserProfile.setCity(mCityField.getText().toString());

        if (!mAddressField.getText().toString().equals(currentUserProfile.getAddress()))
            currentUserProfile.setAddress(mAddressField.getText().toString());

        if (!mPhoneField.getText().toString().equals(currentUserProfile.getPhone()))
            currentUserProfile.setPhone(mPhoneField.getText().toString());

        databaseProfile.child(user.getUid()).setValue(currentUserProfile);

    }
}
