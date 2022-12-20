package com.vinitagarwal.DemoLaundry;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class Splash extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Button getstarted = findViewById(R.id.getstarted);
        getstarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainactivity;
                if(FirebaseAuth.getInstance().getCurrentUser() == null){
                    mainactivity = new Intent(Splash.this,login.class);
                }else{
                    mainactivity = new Intent(Splash.this,MainActivity.class);
                    mainactivity.putExtra("new","false");
                }
                startActivity(mainactivity);
            }
        });
    }
}