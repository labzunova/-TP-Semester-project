package com.example.first;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class AccountEditActivity extends AppCompatActivity {
    // constants
    public final static int PICK_IMAGE_REQUEST = 71;
    private final static String PHOTO_EDITING = "photoEditing";

    // layout references
    private TextInputEditText mNameField;
    private TextInputEditText mEmailField;
    private TextInputEditText mBreedField;
    private TextInputEditText mAgeField;
    private TextInputEditText mCountryField;
    private TextInputEditText mCityField;
    private TextInputEditText mAddressField;
    private TextInputEditText mPhoneField;
    private Button saveBtn, editGalleryBtn;
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

        UISetup();
        listenersSetup(savedInstanceState);
        gettingDataFromFirebase();

    }

    private void uploadImageToFirebaseStorage() {
        Log.d(PHOTO_EDITING, "uploadInage() started");
        if (filepath != null) {
            Log.d(PHOTO_EDITING, "filepath != null");
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading..");
            progressDialog.show();

            StorageReference ref = storageRef.child("Profiles").child(userId).child("AvatarImage");

            byte[] bytes = compressImageFromDevise(filepath);

            // ref.putFile(filepath).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            ref.putBytes(bytes).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    progressDialog.dismiss();
                    Toast.makeText(AccountEditActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(AccountEditActivity.this, AccountActivity.class));
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
        } else {
            Log.d(PHOTO_EDITING, "filepath is null");
            // if new photo was not chosen - start AccountActivity anyway
            startActivity(new Intent(AccountEditActivity.this, AccountActivity.class));
        }
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select picture"), PICK_IMAGE_REQUEST);
        // When user chose photo - onActivityResult() is called
    }

    // result from image chooser activity
    // set chosen photo to imageview
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filepath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filepath);
               Glide
                        .with(getApplicationContext())
                        .load(resizeBitmap(bitmap, 600.0f))
                        .centerCrop()
                        .into(imgPreview);
                 // imgPreview.setRotation((float) 90.0);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void UISetup() {
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
        imgPreview = (ImageView) findViewById(R.id.imageView);
        editGalleryBtn = (Button) findViewById(R.id.editGalleryBtn);
    }

    private void listenersSetup(final Bundle savedInstanceState) {
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadDataToDatabase();
                uploadImageToFirebaseStorage();
            }
        });

        imgPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        editGalleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (savedInstanceState == null) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .add(R.id.galleryFragment, new GalleryFragment())
                            .commit();
                }
            }
        });
    }

    private void gettingDataFromFirebase() {
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

        // Загрузка фотки профиля со Storage
        final long ONE_MEGABYTE = 1024 * 1024;
        StorageReference avatarRef = storageRef.child("Profiles").child(userId).child("AvatarImage");
        avatarRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
               Glide
                        .with(getApplicationContext())
                        .load(resizeBitmap(bitmap, 600.0f))
                        .centerCrop()
                        .into(imgPreview);
                // imgPreview.setRotation((float) 90.0);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "No Such file or Path found!!", Toast.LENGTH_LONG).show();
            }
        });
    }

    private byte[] compressImageFromDevise(Uri filepath) {
        byte[] bytes = null;
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filepath);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            // requlate quality to change size of image, uploading to firebase storage
            bitmap.compress(Bitmap.CompressFormat.JPEG, 60, baos);
            bytes = baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (bytes == null)
            Log.d(PHOTO_EDITING, "bytes is null in compressImageFromDevise()");

        return bytes;
    }

    public static Bitmap resizeBitmap(Bitmap bitmap, float maxResolution) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int newWidth = width;
        int newHeight = height;
        float rate;

        if (width > height) {
            if (maxResolution < width) {
                rate = maxResolution / width;
                newHeight = (int) (height * rate);
                newWidth = (int) maxResolution;
            }
        } else {
            if (maxResolution < height) {
                rate = maxResolution / height;
                newWidth = (int) (width * rate);
                newHeight = (int) maxResolution;
            }
        }

        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
    }

    private void uploadDataToDatabase() {
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
