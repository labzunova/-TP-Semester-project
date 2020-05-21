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

public class AuthorizationActivity extends AppCompatActivity implements FirebaseForAuth.Auth, FirebaseForAuth.Toasts {

    private TextInputEditText mEmailField;
    private TextInputEditText mPasswordField;
    private Button mLoginButton;

    //private InitDataViewModel model;

    public void InitView(){
        mEmailField = (TextInputEditText) findViewById(R.id.emailFieldInp);
        mPasswordField = (TextInputEditText) findViewById(R.id.passwordFieldInp);
        mPasswordField.setTransformationMethod(new PasswordTransformationMethod()); // for font family
        mPasswordField.setTypeface(Typeface.DEFAULT);
        mLoginButton = (Button) findViewById(R.id.loginBtn);
        Button mRegistrationButton = (Button) findViewById(R.id.registrationBtn);
        mRegistrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AuthorizationActivity.this, RegistrationActivity.class));
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorization);

        InitView();

        String email = mEmailField.getText().toString();
        String password = mPasswordField.getText().toString();

      /*  model = new ViewModelProvider(this).get(InitDataViewModel.class);
        model.init(email,password);
        model.getLiveData().observe(this, new Observer<InitData>() {
            @Override
            public void onChanged(InitData initData) {

            }
        }); */

        final FirebaseForAuth firebase = new FirebaseForAuth(this);

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmailField.getText().toString();
                String password = mPasswordField.getText().toString();
                firebase.startSignIn(email,password);
            }
        });

    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }


    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void goToAccount() {
        startActivity(new Intent(AuthorizationActivity.this, AccountActivity.class));
    }

    @Override
    public void makeToast(String toast) {
        Toast.makeText(AuthorizationActivity.this, toast, Toast.LENGTH_LONG).show();
    }
}
