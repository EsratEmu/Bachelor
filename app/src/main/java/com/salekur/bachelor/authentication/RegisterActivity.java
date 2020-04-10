package com.salekur.bachelor.authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.salekur.bachelor.R;
import com.salekur.bachelor.classes.TimeDateAndAddress;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class RegisterActivity extends AppCompatActivity {
    private static final int LOCATION_REQUEST_CODE = 1001;

    private TextInputLayout XmlUserRegisterInputEmail, XmlUserRegisterInputPassword;
    private MaterialButton XmlUserRegisterButton;
    private TextView XmlUserRegisterLoginButton;

    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;
    private ProgressDialog LoadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        RootRef = FirebaseDatabase.getInstance().getReference();
        LoadingBar = new ProgressDialog(this);

        XmlUserRegisterInputEmail = (TextInputLayout) findViewById(R.id.register_edit_email);
        XmlUserRegisterInputPassword = (TextInputLayout) findViewById(R.id.register_edit_password);
        XmlUserRegisterButton = (MaterialButton) findViewById(R.id.register_button);
        XmlUserRegisterLoginButton = (TextView) findViewById(R.id.register_text_login);

        XmlUserRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckUserRegisterInputs();
            }
        });

        XmlUserRegisterLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToLoginActivity();
            }
        });
    }

    private void CheckUserRegisterInputs() {
        String RegisterEmail = XmlUserRegisterInputEmail.getEditText().getText().toString();
        String RegisterPassword = XmlUserRegisterInputPassword.getEditText().getText().toString();

        if (RegisterEmail.isEmpty()) {
            XmlUserRegisterInputEmail.setError("Please enter email");
        } else if (RegisterPassword.isEmpty()) {
            XmlUserRegisterInputPassword.setError("Please enter password");
        } else if (ActivityCompat.checkSelfPermission(RegisterActivity.this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            RegisterUserWithEmailAndPassword(RegisterEmail, RegisterPassword);
        } else {
            ShowLocationPermissionDialog();
        }
    }

    private void RegisterUserWithEmailAndPassword (String email, String password) {
        LoadingBar.setMessage("Creating account");
        LoadingBar.setCanceledOnTouchOutside(false);
        LoadingBar.show();
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FusedLocationProviderClient LocationClient = LocationServices.getFusedLocationProviderClient(RegisterActivity.this);

                    LocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                Geocoder geocoder = new Geocoder(RegisterActivity.this, Locale.getDefault());
                                TimeDateAndAddress address = new TimeDateAndAddress(geocoder, location.getLatitude(), location.getLongitude());
                                TimeDateAndAddress time = new TimeDateAndAddress(new Date(location.getTime()));

                                String device_id = Settings.Secure.getString(RegisterActivity.this.getContentResolver(), Settings.Secure.ANDROID_ID);
                                String device_brand = android.os.Build.BRAND;
                                String device_model = android.os.Build.MODEL;

                                Map UserInformation = new HashMap();
                                UserInformation.put("uid", mAuth.getCurrentUser().getUid());
                                UserInformation.put("email", mAuth.getCurrentUser().getEmail());
                                UserInformation.put("device_id", device_id);
                                UserInformation.put("device_brand", device_brand);
                                UserInformation.put("device_model", device_model);

                                UserInformation.put("register_latitude", address.getLatitude());
                                UserInformation.put("register_longitude", address.getLongitude());
                                UserInformation.put("register_address", address.getShortAddress());
                                UserInformation.put("register_country", address.getFullCountryName());
                                UserInformation.put("register_division", address.getDivisionName());
                                UserInformation.put("register_district", address.getDistrictName());
                                UserInformation.put("register_city", address.getCityName());
                                UserInformation.put("register_postal_code", address.getPostalCode());

                                UserInformation.put("register_year", time.getYearInFullNumber());
                                UserInformation.put("register_month", time.getMonthInNumber());
                                UserInformation.put("register_day", time.getDayInNumber());
                                UserInformation.put("register_time", time.getTimeInTogether());

                                RootRef.child("Users").child(mAuth.getCurrentUser().getUid()).child("information").updateChildren(UserInformation).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            LoadingBar.dismiss();
                                            SendUserToUpdateNameActivity();
                                        } else {
                                            LoadingBar.dismiss();
                                            Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {
                                LoadingBar.dismiss();
                                Toast.makeText(RegisterActivity.this, "Server connection failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    LoadingBar.dismiss();
                    Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_REQUEST_CODE) {
            // Checking whether user granted the permission or not.
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(this, "Please accept permission to register", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void ShowLocationPermissionDialog() {
        ActivityCompat.requestPermissions(RegisterActivity.this, new String[] { ACCESS_FINE_LOCATION }, LOCATION_REQUEST_CODE);
    }

    private void SendUserToUpdateNameActivity() {
        Intent UpdateNameIntent = new Intent(RegisterActivity.this, UpdateNameActivity.class);
        startActivity(UpdateNameIntent);
    }

    private void SendUserToLoginActivity() {
        Intent LoginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(LoginIntent);
    }
}
