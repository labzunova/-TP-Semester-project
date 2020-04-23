package com.example.first;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegistrationActivity extends AppCompatActivity {

    private Button mRegisterBtn;
    private TextInputEditText mEmailField;
    private TextInputEditText mPasswordField;
    private TextInputEditText mNameField;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
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

        mEmailField = (TextInputEditText) findViewById(R.id.emailFieldInp);
        mPasswordField = (TextInputEditText) findViewById(R.id.passwordFieldInp);
        mRegisterBtn = (Button) findViewById(R.id.registerBtn);
        mNameField = (TextInputEditText) findViewById(R.id.nameFieldInp);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("Profiles");
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
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

                    myRef.child(user.getUid()).setValue(profile);

                    startActivity(new Intent(RegistrationActivity.this, AccountActivity.class));
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

