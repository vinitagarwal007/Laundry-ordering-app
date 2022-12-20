package com.vinitagarwal.DemoLaundry;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.concurrent.TimeUnit;

public class login extends AppCompatActivity {
    Boolean otpsent = false;
    ProgressBar progressBar;
    EditText loginotp,loginPhone;
    Button loginbtn;
    ImageView tickimg;
    private String otp;
    boolean newuser;
    PhoneAuthCredential phoneAuthCredential;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        progressBar = findViewById(R.id.progressBar);
        tickimg = findViewById(R.id.tickimg);
        loginbtn = findViewById(R.id.loginbtn);
        loginPhone = findViewById(R.id.loginPhone);
        loginotp = findViewById(R.id.loginOtp);
        loginPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(loginPhone.getText().toString().trim().length() == 10){
                    hideKeyboard();
                    loginPhone.clearFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        loginotp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(loginotp.getText().toString().trim().length() == 6){
                    phoneAuthCredential = PhoneAuthProvider.getCredential(otp,loginotp.getText().toString().trim());
                    FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            hideKeyboard();
                            loginbtn.setText("Login");
                            otpsent = true;
                            loginbtn.setEnabled(true);
                            tickimg.setImageResource(R.drawable.tick);
                            progressBar.setVisibility(View.GONE);
                            tickimg.setVisibility(View.VISIBLE);
                        }else{
                            progressBar.setVisibility(View.GONE);
                            tickimg.setImageResource(R.drawable.cross);
                            tickimg.setVisibility(View.VISIBLE);
                            Toast.makeText(login.this, "Wrong OTP", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(loginPhone.getText().toString().trim().length() < 10){
                    Toast.makeText(login.this, "Invalid Phone Number", Toast.LENGTH_SHORT).show();
                }
                else if(otpsent){
                    if(FirebaseAuth.getInstance().getCurrentUser().getDisplayName() == null){
                        ConstraintLayout constraintLayout = findViewById(R.id.constraintLayout3);
                        constraintLayout.setVisibility(View.VISIBLE);
                        EditText name,delivery;
                        name = findViewById(R.id.loginName);
                        delivery = findViewById(R.id.loginaddress);
                        if(name.getText().toString().equals("")) {
                            Toast.makeText(login.this, "Invalid Name", Toast.LENGTH_SHORT).show();
                            name.setText("");
                            name.requestFocus();
                            return;
                        }
                        Uri uri = Uri.parse(name.getText() +System.lineSeparator()+FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()+System.lineSeparator()+delivery.getText().toString()+"@"+name.getText() +System.lineSeparator()+FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()+System.lineSeparator()+delivery.getText().toString());
                        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder().setDisplayName(name.getText().toString()).setPhotoUri(uri).build();
                        FirebaseAuth.getInstance().getCurrentUser().updateProfile(request).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                SharedPreferences sharedPreferences = getSharedPreferences("rates",MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("delivery",delivery.getText().toString());
                                editor.putString("pickup",delivery.getText().toString());
                                editor.commit();
                                Intent intent = new Intent(login.this,MainActivity.class);
                                startActivity(intent);
                            }
                        });
                    }else{
                        SharedPreferences sharedPreferences = getSharedPreferences("rates",MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("delivery",FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString().split("@")[0]);
                        editor.putString("pickup",FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString().split("@")[1]);
                        editor.commit();
                        Intent intent = new Intent(login.this,MainActivity.class);
                        startActivity(intent);
                    }
                }else{
                    loginbtn.setText("Sending OTP");
                    progressBar.setVisibility(View.VISIBLE);
                    loginbtn.setEnabled(false);
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    PhoneAuthOptions options =
                            PhoneAuthOptions.newBuilder(mAuth)
                                    .setPhoneNumber("+91" + loginPhone.getText().toString())       // Phone number to verify
                                    .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                                    .setActivity(login.this)                 // Activity (for callback binding)
                                    .setCallbacks(mCallbacks)// OnVerificationStateChangedCallbacks
                                    .build();
                    PhoneAuthProvider.verifyPhoneNumber(options);
                    loginPhone.setEnabled(false);
                }
            }
        });
    }
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(login.this, "OTP Sending Failed", Toast.LENGTH_SHORT).show();
            loginbtn.setEnabled(true);
            loginbtn.setText("Send OTP");
            loginPhone.setEnabled(true);
            loginPhone.setText("");
            loginPhone.requestFocus();
            Log.d("verfication", "onVerificationFailed: " + e.toString());
        }

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            progressBar.setVisibility(View.VISIBLE);
            otp = s;
            loginbtn.setText("Send OTP");
            Toast.makeText(login.this, "OTP Sent", Toast.LENGTH_SHORT).show();
            ConstraintLayout constraintLayout = findViewById(R.id.constraintLayout2);
            constraintLayout.setVisibility(View.VISIBLE);
            loginotp.setEnabled(true);
            loginotp.requestFocus();
        }
    };
    public void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager manager
                    = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(
                            view.getWindowToken(), 0);
    }
    }
}