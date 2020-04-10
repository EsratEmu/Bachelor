package com.salekur.bachelor.admin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.salekur.bachelor.R;
import com.salekur.bachelor.authentication.LoginActivity;

public class AdminActivity extends AppCompatActivity {
    private FirebaseUser CurrentUser;
    private DatabaseReference RootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        CurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        RootRef = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (CurrentUser == null) {
            SendUserToLoginActivity();
        } else {

        }
    }

    private void SendUserToLoginActivity() {
        Intent LoginIntent = new Intent(AdminActivity.this, LoginActivity.class);
        startActivity(LoginIntent);
    }
}
