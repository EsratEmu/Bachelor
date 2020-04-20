package com.salekur.bachelor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.salekur.bachelor.authentication.LoginActivity;
import com.salekur.bachelor.authentication.UpdateNameActivity;
import com.salekur.bachelor.authentication.UpdatePhoneNumberActivity;
import com.salekur.bachelor.authentication.UpdateProfileImageActivity;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    // declaring xml variables
    private CircleImageView XmlUserImage;
    private TextView XmlUserName, XmlUserNameDown, XmlUserEmail, XmlUserPhoneNumber, XmlUserAddress, XmlUserAbout;
    private TextView XmlChatWithVisitorButton;
    private FrameLayout XmlProfileBackButton;

    private FirebaseUser CurrentUser;
    private DatabaseReference RootRef;
    private String ProfileID, CurrentUserID;

    private ProgressDialog LoadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        CurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        RootRef = FirebaseDatabase.getInstance().getReference();
        LoadingBar = new ProgressDialog(this);

        XmlUserImage = (CircleImageView) findViewById(R.id.profile_user_image);
        XmlUserName = (TextView) findViewById(R.id.profile_user_name);
        XmlUserNameDown = (TextView) findViewById(R.id.profile_user_name_down);
        XmlUserEmail = (TextView) findViewById(R.id.profile_user_email);
        XmlUserPhoneNumber = (TextView) findViewById(R.id.profile_user_phone_number);
        XmlUserAddress = (TextView) findViewById(R.id.profile_user_address);
        XmlUserAbout = (TextView) findViewById(R.id.profile_user_about);

        CurrentUserID = getIntent().getExtras().get("ProfileID").toString();

        XmlProfileBackButton = (FrameLayout) findViewById(R.id.profile_button_back);
        XmlChatWithVisitorButton = (TextView) findViewById(R.id.profile_button_chat);


        SetButtonController();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (CurrentUser == null) {
            SendUserToLoginActivity();
        } else {
            CurrentUserID = CurrentUser.getUid();
            ProfileID = getIntent().getStringExtra("ProfileID");

            if (ProfileID.equals(CurrentUserID)) {
                ControlAsMyProfile();
            } else {
                ControlAsOtherProfile();
            }

            RetrieveUserProfileInformation();
        }
    }


    private void ShowAboutDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Tell us something about you");

        final EditText AboutInput = new EditText(this);
        AboutInput.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(AboutInput);

        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String ProfileAbout = AboutInput.getText().toString();

                if (ProfileAbout.isEmpty()) {
                    AboutInput.setError("Please enter something");
                } else {
                    dialog.dismiss();

                    LoadingBar.setMessage("Updating status");
                    LoadingBar.setCanceledOnTouchOutside(false);
                    LoadingBar.show();
                    RootRef.child("Users").child(CurrentUserID).child("about").setValue(ProfileAbout).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                LoadingBar.dismiss();
                                Toast.makeText(ProfileActivity.this, "Status Updated", Toast.LENGTH_SHORT).show();
                            } else {
                                LoadingBar.dismiss();
                                Toast.makeText(ProfileActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();





    }

    private void RetrieveUserProfileInformation() {
        RootRef.child("Users").child(ProfileID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.hasChild("profile_image")) {
                    }
                    String ProfileImage = dataSnapshot.child("profile_image").getValue().toString();
                    Picasso.get().load(ProfileImage).placeholder(R.drawable.boy_image).into(XmlUserImage);

                    if (dataSnapshot.hasChild("first_name") && dataSnapshot.hasChild("last_name")) {
                        String ProfileFirstName = dataSnapshot.child("first_name").getValue().toString();
                        String ProfileLastName = dataSnapshot.child("last_name").getValue().toString();

                        XmlUserName.setText(ProfileFirstName + " " + ProfileLastName);
                        XmlUserNameDown.setText(ProfileFirstName + " " + ProfileLastName);
                    }

                    if (dataSnapshot.hasChild("email")) {
                        String ProfileEmail = dataSnapshot.child("email").getValue().toString();
                        XmlUserEmail.setText(ProfileEmail);
                    }

                    if (dataSnapshot.hasChild("phone_number")) {
                        String ProfileNumber = dataSnapshot.child("phone_number").getValue().toString();
                        XmlUserPhoneNumber.setText(ProfileNumber);
                    }

                    if (dataSnapshot.hasChild("register_address")) {
                        String ProfileAddress = dataSnapshot.child("register_address").getValue().toString();
                        XmlUserAddress.setText(ProfileAddress);
                    }

                    if (dataSnapshot.hasChild("about")) {
                        String ProfileAbout = dataSnapshot.child("about").getValue().toString();

                        if (!ProfileAbout.isEmpty()) {
                            XmlUserAbout.setText(ProfileAbout);
                            XmlUserAbout.setVisibility(View.VISIBLE);
                        }
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void ControlAsMyProfile() {
        XmlUserImage.setClickable(true);
        XmlUserName.setClickable(true);
        XmlUserNameDown.setClickable(true);
        XmlUserAbout.setClickable(true);
        XmlUserEmail.setClickable(true);
        XmlUserPhoneNumber.setClickable(true);

        XmlChatWithVisitorButton.setVisibility(View.GONE);
    }

    private void ControlAsOtherProfile() {
        XmlUserImage.setClickable(false);
        XmlUserName.setClickable(false);
        XmlUserNameDown.setClickable(false);
        XmlUserAbout.setClickable(false);
        XmlUserEmail.setClickable(false);
        XmlUserPhoneNumber.setClickable(false);

        XmlChatWithVisitorButton.setVisibility(View.VISIBLE);
    }

    private void SetButtonController() {
        XmlUserImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToUpdateImageActivity();
            }
        });

        XmlUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToUpdateNameActivity();
            }
        });

        XmlUserAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowAboutDialog();
            }
        });

        XmlUserNameDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToUpdateNameActivity();
            }
        });

        XmlUserEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToUpdateEmailActivity();
            }
        });

        XmlUserPhoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToUpdatePhoneNumberActivity();
            }
        });

        XmlProfileBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToMainActivity();
            }
        });

        XmlChatWithVisitorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToChatActivity();
            }
        });
    }

    private void SendUserToMainActivity() {
        Intent ManiIntent = new Intent(ProfileActivity.this, MainActivity.class);
        startActivity(ManiIntent);
    }

    private void SendUserToLoginActivity() {
        Intent LoginIntent = new Intent(ProfileActivity.this, LoginActivity.class);
        LoginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(LoginIntent);
    }

    private void SendUserToChatActivity() {
        Intent ChatIntent = new Intent(ProfileActivity.this, PrivateChatActivity.class);
        ChatIntent.putExtra("ProfileID", ProfileID);
        startActivity(ChatIntent);
    }

    private void SendUserToUpdateNameActivity() {
        Intent UpdateNameIntent = new Intent(ProfileActivity.this, UpdateNameActivity.class);
        startActivity(UpdateNameIntent);
    }

    private void SendUserToUpdateEmailActivity() {
        //Intent UpdateEmailIntent = new Intent(ProfileActivity.this, UpdateEmailActivity.class);
        //startActivity(UpdateEmailIntent);
        Toast.makeText(this, "Coming Soon", Toast.LENGTH_SHORT).show();
    }

    private void SendUserToUpdatePhoneNumberActivity() {
        Intent UpdatePhoneNumberIntent = new Intent(ProfileActivity.this, UpdatePhoneNumberActivity.class);
        startActivity(UpdatePhoneNumberIntent);
    }

    private void SendUserToUpdateImageActivity() {
        Intent UpdateImageIntent = new Intent(ProfileActivity.this, UpdateProfileImageActivity.class);
        startActivity(UpdateImageIntent);
    }

}
