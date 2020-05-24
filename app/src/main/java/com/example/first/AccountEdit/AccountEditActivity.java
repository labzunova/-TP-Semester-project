package com.example.first.AccountEdit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.first.AccountActivity;
import com.example.first.Profile;
import com.example.first.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
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
import java.util.List;
import java.util.function.ToDoubleBiFunction;

public class AccountEditActivity extends AppCompatActivity {
    public final static int PICK_IMAGE_REQUEST = 71;
    private static final String TAG = "EditAccountActivity";

    EditActivityViewModel mViewModel;

    // layout references
    private TextInputEditText mNameField;
    private TextInputEditText mEmailField;
    private TextInputEditText mBreedField;
    private TextInputEditText mAgeField;
    private TextInputEditText mCountryField;
    private TextInputEditText mCityField;
    private TextInputEditText mAddressField;
    private TextInputEditText mPhoneField;
    private ImageView imgPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_edit);

        UISetup();

        mViewModel = new ViewModelProvider(this).get(EditActivityViewModel.class);
        mViewModel.getProgress().observe(this, new Observer<EditActivityViewModel.ValidationStatus>() {
            @Override
            public void onChanged(EditActivityViewModel.ValidationStatus validationStatus) {
                // update UI when Data is changed (validation)
                // if data is ok - exit editActivity
                if (validationStatus == EditActivityViewModel.ValidationStatus.SUCCESS) {
                    Log.d(TAG, "Data validation success. Starting AccountActivity..");
                    startActivity(new Intent(AccountEditActivity.this, AccountActivity.class));
                } else if (validationStatus == EditActivityViewModel.ValidationStatus.FAILURE) {
                    Log.d(TAG, "Data validation failure. Updating UI to notify user about incorrect data input..");
                    // TODO: Update UI to make warning to user
                } else if (validationStatus == EditActivityViewModel.ValidationStatus.NONE) {
                    Log.d(TAG, "Data validation status is NONE");
                }
            }
        });

        mViewModel.getAvatarImage().observe(this, new Observer<EditActivityViewModel.AvatarImage>() {
            @Override
            public void onChanged(EditActivityViewModel.AvatarImage avatarImage) {
                // update avatar image
                Log.d(TAG, "getAvatarImage onChanged()");
                imgPreview.setImageBitmap(avatarImage.getAvatarBitmap());
            }
        });

        mViewModel.getProfileInfo().observe(this, new Observer<EditActivityViewModel.ProfileInfo>() {
            @Override
            public void onChanged(EditActivityViewModel.ProfileInfo profileInfo) {
                Log.d(TAG, "getProfileInfo onChanged()");
                // update text fields
                mNameField.setText(profileInfo.getName());
                mEmailField.setText(profileInfo.getEmail());
                mBreedField.setText(profileInfo.getBreed());
                mAgeField.setText(profileInfo.getAge());
                mCountryField.setText(profileInfo.getCountry());
                mCityField.setText(profileInfo.getCity());
                mAddressField.setText(profileInfo.getAddress());
                mPhoneField.setText(profileInfo.getPhone());
            }
        });

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Log.d(TAG, "User id: " + user.getUid());

        // getData either from cash or from firebase
        Log.d(TAG, "mViewModel.getData();");
        mViewModel.getData();
    }



    private void UISetup() {
        // init views
        mNameField = findViewById(R.id.nameFieldInp);
        mEmailField = findViewById(R.id.emailFieldInp);
        mBreedField = findViewById(R.id.dogBreedFieldInp);
        mAgeField = findViewById(R.id.dogAgeFieldInp);
        mCountryField = findViewById(R.id.countryFieldInp);
        mCityField = findViewById(R.id.cityFieldInp);
        mAddressField = findViewById(R.id.addressFieldInp);
        mPhoneField = findViewById(R.id.phoneFieldInp);
        Button doneBtn = findViewById(R.id.saveBtn);
        Button changeProfilePhotoBtn = findViewById(R.id.changePhotoBtn);
        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);
        imgPreview = findViewById(R.id.imageView);

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // убрать создание здесь структуры - чтобы структура формировалась уже во ViewModel
                EditActivityViewModel.ProfileInfo profileInfo = new EditActivityViewModel.ProfileInfo(
                        mNameField.getText().toString(),
                        mEmailField.getText().toString(),
                        mPhoneField.getText().toString(),
                        mBreedField.getText().toString(),
                        mAgeField.getText().toString(),
                        mCountryField.getText().toString(),
                        mCityField.getText().toString(),
                        mAddressField.getText().toString()
                );
                mViewModel.onDoneClicked(profileInfo);
            }
        });

        changeProfilePhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Log.d(TAG, "UISetup: NavigationOnClickListener is set");
            topAppBar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(AccountEditActivity.this, AccountActivity.class));
                }
            });
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
            Uri filepath = data.getData();
            try {
                Bitmap bitmap;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(getContentResolver(), filepath));
                } else {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filepath);
                }

                // запрос на загрузку фото в кэш и firebase + загрузка в imageview
                Log.d(TAG, "onActivityResult: mViewModel.uploadAvatarImage()");
                mViewModel.uploadAvatarImage(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
