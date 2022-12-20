package com.vinitagarwal.DemoLaundry;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;

public class profile extends Fragment {
    MainActivity mainActivity;

    public profile(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }
    Button submit,logout;
    EditText name,phone,delivery,pickup;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        logout =getView().findViewById(R.id.logoutbtn);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);

                builder.setTitle("Confirm Logout");
                builder.setMessage("Are you sure you want to Logout?");

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseAuth.getInstance().signOut();
                        Intent backtosplash = new Intent(mainActivity,Splash.class);
                        startActivity(backtosplash);
                    }
                });

                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
        name= getView().findViewById(R.id.customername);
        phone=getView().findViewById(R.id.customerphone);
        delivery = getView().findViewById(R.id.deliverylocation);
        pickup = getView().findViewById(R.id.pickuplocation);
        submit = getView().findViewById(R.id.submitbtn);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = "";
                text = name.getText()+System.lineSeparator()+phone.getText()+System.lineSeparator()+"Address"+System.lineSeparator()+delivery.getText() + "@" + name.getText()+System.lineSeparator()+phone.getText()+System.lineSeparator()+"Address"+System.lineSeparator()+pickup.getText();
                Uri uri = Uri.parse(text);
                Log.d("test", "onClick: "+text);
                UserProfileChangeRequest request = new UserProfileChangeRequest.Builder().setDisplayName(name.getText().toString()).setPhotoUri(uri).build();
                FirebaseAuth.getInstance().getCurrentUser().updateProfile(request).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(mainActivity, "Profile Updated", Toast.LENGTH_SHORT).show();
                    }
                });
                SharedPreferences sharedPreferencesrate;
                SharedPreferences.Editor editorrate;
                sharedPreferencesrate = mainActivity.getSharedPreferences("rates", Context.MODE_PRIVATE);
                editorrate = sharedPreferencesrate.edit();
                editorrate.putString("delivery", text.split("@")[0]);
                editorrate.putString("pickup", text.split("@")[1]);
                editorrate.commit();
            }
        });
        updateprofile();
        Log.d("test", "onViewCreated: profile");
    }
    public void updateprofile(){
        name.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        phone.setText(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
        String str = "";
        for (int i = 3; i <FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString().split("@")[0].split(System.lineSeparator()).length; i++) {
            str += FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString().split("@")[0].split(System.lineSeparator())[i];
            str += System.lineSeparator();
        }
        delivery.setText(str);
        str = "";
        for (int i = 3; i <FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString().split("@")[1].split(System.lineSeparator()).length; i++) {
            str += FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString().split("@")[1].split(System.lineSeparator())[i];
            str += System.lineSeparator();
        }
        pickup.setText(str);

    }
}