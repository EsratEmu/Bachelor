package com.salekur.bachelor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
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

public class MainActivity extends AppCompatActivity {
    // declaring variable to allocate design elements
    private DrawerLayout drawer;
    private Toolbar toolbar;
    private ActionBarDrawerToggle drawerToggle;
    private NavigationView navigationView;

    // variables for drawer elements
    private CircleImageView XmlDrawerUserImage;
    private TextView XmlDrawerUserFullName, XmlDrawerUserEmail;

    // variables for this activity
    private FirebaseUser CurrentUser;
    private DatabaseReference RootRef;

    private ProgressDialog LoadingBar;
    private String CurrentUserName, CurrentUserEmail, CurrentUserPhoneNumber;
    private Uri CurrentUserImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // setting drawer and toolbar
        drawer = (DrawerLayout) findViewById(R.id.main_drawer);
        toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        drawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.app_name, R.string.app_name);
        drawer.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.main_navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.main_menu_profile:
                        CloseDrawer();
                        SendUserToProfileActivity();
                        break;
                    case R.id.main_menu_notification:
                        CloseDrawer();
                        SendUserToNotificationActivity();
                        break;
                    case R.id.main_menu_bmeal:
                        CloseDrawer();
                        SendUserToBMealActivity();
                        break;
                    case R.id.main_menu_bchat:
                        CloseDrawer();
                        SendUserToBChatActivity();
                        break;
                    case R.id.main_menu_btask:
                        CloseDrawer();
                        SendUserToBTaskActivity();
                        break;
                    case R.id.main_menu_bshop:
                        CloseDrawer();
                        SendUserToBShopActivity();
                        break;
                    case R.id.main_menu_logout:
                        CloseDrawer();
                        UserSignOut();
                        break;
                    case R.id.main_menu_support:
                        CloseDrawer();
                        SendUserToSupportActivity();
                        break;
                    case R.id.main_menu_settings:
                        CloseDrawer();
                        SendUserToSettingsActivity();
                        break;
                }
                return false;
            }
        });

        // drawer variable assigning
        View drawerView =  navigationView.getHeaderView(0);
        XmlDrawerUserImage = (CircleImageView) drawerView.findViewById(R.id.main_drawer_user_image);
        XmlDrawerUserFullName = (TextView) drawerView.findViewById(R.id.main_drawer_user_name);
        XmlDrawerUserEmail = (TextView) drawerView.findViewById(R.id.main_drawer_user_email);

        // getting current user from firebase database
        CurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        RootRef = FirebaseDatabase.getInstance().getReference();

        LoadingBar = new ProgressDialog(this);
    }

    @Override
    protected void onStart() { // this function ran on starting of this activity
        super.onStart();

        if (CurrentUser == null) { // if user is not found on opening this activity
            SendUserToLoginActivity(); // then sending user to login activity
        } else { // else user has found means already logged in then work in this activity
            CheckCurrentServerStatus();
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // initializing all global variable in this functions
    private void InitializeUserVariables() {
        //getting user information of logged in user from firebase auth
        CurrentUserEmail = CurrentUser.getEmail(); // for email
        CurrentUserName = String.valueOf(CurrentUser.getDisplayName()); // for user full name
        CurrentUserPhoneNumber = String.valueOf(CurrentUser.getPhoneNumber()); // for user phone number
        CurrentUserImage = CurrentUser.getPhotoUrl(); // for user profile image

        if (CurrentUserName.isEmpty() || CurrentUserName == "null") { // checking if logged in user full name is empty or not
            SendUserToUpdateNameActivity(); // if name is empty then calling update name function
        } else if (CurrentUserPhoneNumber.isEmpty() || CurrentUserPhoneNumber== "null") { // checking if logged in user phone number is empty or not
            SendUserToUpdatePhoneNumberActivity(); // if phone number is empty then calling update phone number function
        } else if (CurrentUserImage == null) { // checking if logged in user profile image is empty or not
            SendUserToUpdateProfileImageActivity(); // if profile image is empty then calling update profile image function
        } else { // user has set up all the information which we need us
            UpdateUserHome(); // so work in this activity

            LoadingBar.setMessage("Checking profile");
            LoadingBar.setCanceledOnTouchOutside(false);
            LoadingBar.show();
            RootRef.child("Users").child(CurrentUser.getUid()).child("profile_image").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.exists()) {
                        LoadingBar.dismiss();
                        SendUserToUpdateProfileImageActivity();
                    } else {
                        LoadingBar.dismiss();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void UpdateUserHome() {
        Picasso.get().load(CurrentUserImage).placeholder(R.drawable.boy_image).into(XmlDrawerUserImage);
        XmlDrawerUserFullName.setText(CurrentUserName);
        XmlDrawerUserEmail.setText(CurrentUserEmail);
    }















    // basic functions for this activity
    private void CheckCurrentServerStatus() {
        LoadingBar.setMessage("Checking server");
        LoadingBar.setCanceledOnTouchOutside(false);
        LoadingBar.show();
        RootRef.child("Bachelor").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("Admins").hasChild(CurrentUser.getUid())) {
                    LoadingBar.dismiss();
                    InitializeUserVariables();
                } else {
                    LoadingBar.dismiss();
                    if (dataSnapshot.child("Server").child("status").getValue().toString().equals("down")) {
                        final Dialog dialog = new Dialog(MainActivity.this);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                        dialog.setCancelable(false);
                        dialog.setContentView(R.layout.dialog_default);

                        TextView DialogTitle = (TextView) dialog.findViewById(R.id.dialog_default_title);
                        TextView DialogBody = (TextView) dialog.findViewById(R.id.dialog_default_message);
                        MaterialButton DialogOk = (MaterialButton) dialog.findViewById(R.id.dialog_default_ok);

                        DialogTitle.setText("Server Down");
                        DialogTitle.setTextColor(Color.RED);
                        DialogBody.setText("Sorry! Bachelor server has down. Please try again later");
                        DialogBody.setVisibility(View.VISIBLE);

                        DialogOk.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                UserSignOut();
                            }
                        });

                        dialog.show();
                    } else {
                        LoadingBar.dismiss();
                        InitializeUserVariables();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void UserSignOut() {
        // signing out from account
        FirebaseAuth.getInstance().signOut();
        // sending user to login activity
        SendUserToLoginActivity();
    }

    private void CloseDrawer() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
    }

    private void SendUserToProfileActivity() {
        Intent ProfileIntent = new Intent(MainActivity.this, ProfileActivity.class);
        ProfileIntent.putExtra("ProfileID", CurrentUser.getUid());
        startActivity(ProfileIntent);
    }

    private void SendUserToSettingsActivity() {
        Intent SettingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(SettingsIntent);
    }

    private void SendUserToSupportActivity() {
        Intent SupportIntent = new Intent(MainActivity.this, SupportActivity.class);
        startActivity(SupportIntent);
    }

    private void SendUserToBMealActivity() {
        Intent BMealIntent = new Intent(MainActivity.this, BMealActivity.class);
        startActivity(BMealIntent);
    }

    private void SendUserToBChatActivity() {
        Intent BChatIntent = new Intent(MainActivity.this, BChatActivity.class);
        startActivity(BChatIntent);
    }

    private void SendUserToBTaskActivity() {
        Intent BTaskIntent = new Intent(MainActivity.this, BTaskActivity.class);
        startActivity(BTaskIntent);
    }

    private void SendUserToBShopActivity() {
        Intent BShopIntent = new Intent(MainActivity.this, BShopActivity.class);
        startActivity(BShopIntent);
    }

    private void SendUserToNotificationActivity() {
        Intent NotificationIntent = new Intent(MainActivity.this, NotificationActivity.class);
        startActivity(NotificationIntent);
    }

    private void SendUserToLoginActivity() {
        Intent LoginIntent = new Intent(MainActivity.this, LoginActivity.class);
        LoginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(LoginIntent);
    }

    private void SendUserToUpdateProfileImageActivity() {
        Intent UpdateProfileImageIntent = new Intent(MainActivity.this, UpdateProfileImageActivity.class);
        UpdateProfileImageIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(UpdateProfileImageIntent);
    }

    private void SendUserToUpdatePhoneNumberActivity() {
        Intent UpdatePhoneNumberIntent = new Intent(MainActivity.this, UpdatePhoneNumberActivity.class);
        UpdatePhoneNumberIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(UpdatePhoneNumberIntent);
    }

    private void SendUserToUpdateNameActivity() {
        Intent UpdateNameIntent = new Intent(MainActivity.this, UpdateNameActivity.class);
        UpdateNameIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(UpdateNameIntent);
    }
}
