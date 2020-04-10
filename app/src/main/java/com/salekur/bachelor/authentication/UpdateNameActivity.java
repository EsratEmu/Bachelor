package com.salekur.bachelor.authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.salekur.bachelor.MainActivity;
import com.salekur.bachelor.R;

import java.util.HashMap;
import java.util.Map;

public class UpdateNameActivity extends AppCompatActivity {
    private TextInputLayout XmlUserFirstName, XmlUserLastName;
    private MaterialButton XmlUpdateNameButton;

    private FirebaseAuth mAuth;
    private FirebaseUser CurrentUser;
    private DatabaseReference RootRef;

    private ProgressDialog LoadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_name);

        mAuth = FirebaseAuth.getInstance();
        CurrentUser = mAuth.getCurrentUser();
        RootRef = FirebaseDatabase.getInstance().getReference();
        LoadingBar = new ProgressDialog(this);

        XmlUserFirstName = (TextInputLayout) findViewById(R.id.update_name_edit_first_name);
        XmlUserLastName = (TextInputLayout) findViewById(R.id.update_name_edit_last_name);
        XmlUpdateNameButton = (MaterialButton) findViewById(R.id.update_name_button_update);

        XmlUpdateNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckUserNameInputs();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (CurrentUser == null) {
            SendUserToLoginActivity();
        } else {
            RetrieveUserNameFromDatabase();
        }
    }

    private void RetrieveUserNameFromDatabase() {
        RootRef.child("Users").child(CurrentUser.getUid()).child("information").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.hasChild("first_name")) {
                        String FirstName = dataSnapshot.child("first_name").getValue().toString();
                        XmlUserFirstName.setHelperText(FirstName);
                    }
                    if (dataSnapshot.hasChild("last_name")) {
                        String LastName = dataSnapshot.child("last_name").getValue().toString();
                        XmlUserLastName.setHelperText(LastName);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void CheckUserNameInputs() {
        String UserFirstName = XmlUserFirstName.getEditText().getText().toString();
        String UserLastName = XmlUserLastName.getEditText().getText().toString();

        if (UserFirstName.isEmpty()) {
            XmlUserFirstName.setError("Please enter first name");
        } else if (UserLastName.isEmpty()) {
            XmlUserLastName.setError("Please enter last name");
        } else {
            UpdateUserName(UserFirstName, UserLastName);
        }
    }

    private void UpdateUserName(final String FirstName, final String LastName) {
        UserProfileChangeRequest UpdateProfile = new UserProfileChangeRequest.Builder().setDisplayName(FirstName + " " + LastName).build();

        LoadingBar.setMessage("Updating Name");
        LoadingBar.setCanceledOnTouchOutside(false);
        LoadingBar.show();
        CurrentUser.updateProfile(UpdateProfile).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Map Information = new HashMap();
                    Information.put("first_name", FirstName);
                    Information.put("last_name", LastName);

                    RootRef.child("Users").child(CurrentUser.getUid()).child("information").updateChildren(Information).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()) {
                                LoadingBar.dismiss();
                                SendUserToMainActivity();
                            } else {
                                LoadingBar.dismiss();
                                Toast.makeText(UpdateNameActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    LoadingBar.dismiss();
                    Toast.makeText(UpdateNameActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void SendUserToLoginActivity() {
        Intent LoginIntent = new Intent(UpdateNameActivity.this, LoginActivity.class);
        LoginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(LoginIntent);
    }

    private void SendUserToMainActivity() {
        Intent MainIntent = new Intent(UpdateNameActivity.this, MainActivity.class);
        startActivity(MainIntent);
    }
}
