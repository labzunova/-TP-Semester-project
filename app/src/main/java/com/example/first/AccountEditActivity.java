package com.example.first;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_edit);

        databaseProfile = FirebaseDatabase.getInstance().getReference("Profiles"); // Expected to be automatically created if Profiles node not yet created

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


    }

    private void saveData() {
        String name = mNameField.getText().toString();

        if (!TextUtils.isEmpty(name)) {
            Profile profile = new Profile();
            profile.setName(name);
            profile.setBreed("Bulldog");
            profile.setAge(7);

            String id = databaseProfile.push().getKey();
            databaseProfile.child(id).setValue(profile);

        } else {
            Toast.makeText(this, "Name field is empty", Toast.LENGTH_SHORT).show();
        }


    }
}
