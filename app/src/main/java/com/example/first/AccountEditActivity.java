package com.example.first;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AccountEditActivity extends AppCompatActivity {


    TextInputEditText mNameField;
    TextInputEditText mEmailField;
    TextInputEditText mBreedField;
    TextInputEditText mAgeField;
    TextInputEditText mCountryField;
    TextInputEditText mCityField;
    TextInputEditText mAddressField;
    TextInputEditText mPhoneField;
    Button saveBtn;

    DatabaseReference databaseProfile;
    FirebaseUser user;
    String userId;
    Profile currentUserProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_edit);

        databaseProfile = FirebaseDatabase.getInstance().getReference("Profiles"); // Expected to be automatically created if Profiles node not yet created
        user = FirebaseAuth.getInstance().getCurrentUser();
        userId = user.getUid();

        mNameField = (TextInputEditText) findViewById(R.id.nameFieldInp);
        mEmailField = (TextInputEditText) findViewById(R.id.emailFieldInp);
        mBreedField = (TextInputEditText) findViewById(R.id.dogBreedFieldInp);
        mAgeField = (TextInputEditText) findViewById(R.id.dogAgeFieldInp);
        mCountryField = (TextInputEditText) findViewById(R.id.countryFieldInp);
        mCityField = (TextInputEditText) findViewById(R.id.cityFieldInp);
        mAddressField = (TextInputEditText) findViewById(R.id.addressFieldInp);
        mPhoneField = (TextInputEditText) findViewById(R.id.phoneFieldInp);
        saveBtn = (Button) findViewById(R.id.saveBtn);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
            }
        });

        databaseProfile.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
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


    }

    private void saveData() {
        String name = mNameField.getText().toString();

        /*
        if (!TextUtils.isEmpty(name)) {
            // Profile profile = new Profile();
            currentUserProfile.setName(name);

            //String id = databaseProfile.push().getKey();
            databaseProfile.child(user.getUid()).setValue(currentUserProfile);

        } else {
            Toast.makeText(this, "Name field is empty", Toast.LENGTH_SHORT).show();
        }
         */

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
