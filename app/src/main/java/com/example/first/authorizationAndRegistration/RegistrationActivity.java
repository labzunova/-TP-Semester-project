package com.example.first.authorizationAndRegistration;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.first.AccountActivity;
import com.example.first.R;
import com.google.android.material.textfield.TextInputEditText;

public class RegistrationActivity extends AppCompatActivity implements FirebaseForAuth.Auth, FirebaseForAuth.Toasts  {

    private Button mRegisterBtn;
    private TextInputEditText mEmailField;
    private TextInputEditText mPasswordField;

    public void InitView(){

        mEmailField = (TextInputEditText) findViewById(R.id.emailFieldInp);
        mPasswordField = (TextInputEditText) findViewById(R.id.passwordFieldInp);
        mPasswordField.setTransformationMethod(new PasswordTransformationMethod()); // for font family
        mPasswordField.setTypeface(Typeface.DEFAULT);
        mRegisterBtn = (Button) findViewById(R.id.registerBtn);
        Button mBackButton = (Button) findViewById(R.id.backToAuthBtn);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegistrationActivity.this, AuthorizationActivity.class));
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        InitView();

        final FirebaseForRegistration firebase = new FirebaseForRegistration(this);

        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmailField.getText().toString();
                String password = mPasswordField.getText().toString();
                firebase.startRegister(email,password);
            }
        });
    }

    @Override
    public void goToAccount() {
        startActivity(new Intent(RegistrationActivity.this, AccountActivity.class));
    }

    @Override
    public void makeToast(String toast) {
        Toast.makeText(RegistrationActivity.this, toast, Toast.LENGTH_LONG).show();
    }
}

