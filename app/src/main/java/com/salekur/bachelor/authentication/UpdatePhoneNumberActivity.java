package com.salekur.bachelor.authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.renderscript.Sampler;
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
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;
import com.salekur.bachelor.MainActivity;
import com.salekur.bachelor.R;
import com.salekur.bachelor.classes.TimeDateAndAddress;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class UpdatePhoneNumberActivity extends AppCompatActivity {
    private TextView XmlPhoneNumberHintText, XmlVerificationCodeHintText;
    private CountryCodePicker XmlPhoneCountryCode;
    private TextInputLayout XmlUserPhoneNumber, XmlVerificationCode;
    private MaterialButton XmlUpdatePhoneNumberButton, XmlVerifyPhoneCodeButton;
    private TextView XmlEditPhoneNumberButton;

    private FirebaseAuth mAuth;
    private FirebaseUser CurrentUser;
    private DatabaseReference RootRef;
    private ProgressDialog LoadingBar;

    private String VerificationId;
    private String PhoneNumberWithCountryCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_phone_number);

        mAuth = FirebaseAuth.getInstance();
        CurrentUser = mAuth.getCurrentUser();
        RootRef = FirebaseDatabase.getInstance().getReference();
        LoadingBar = new ProgressDialog(this);

        XmlPhoneNumberHintText = (TextView) findViewById(R.id.update_phone_number_text_phone_number_hint);
        XmlVerificationCodeHintText = (TextView) findViewById(R.id.update_phone_number_text_verify_code_hint);
        XmlPhoneCountryCode = (CountryCodePicker) findViewById(R.id.update_phone_number_edit_country_code);
        XmlUserPhoneNumber = (TextInputLayout) findViewById(R.id.update_phone_number_edit_phone_number);
        XmlVerificationCode = (TextInputLayout) findViewById(R.id.update_phone_number_edit_verification_code);
        XmlUpdatePhoneNumberButton = (MaterialButton) findViewById(R.id.update_phone_number_button_update);
        XmlVerifyPhoneCodeButton = (MaterialButton) findViewById(R.id.update_phone_number_button_verify_code);
        XmlEditPhoneNumberButton = (TextView) findViewById(R.id.update_phone_number_text_phone_number_edit);

        XmlUpdatePhoneNumberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetFullPhoneNumber();
            }
        });

        XmlVerifyPhoneCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SubmitVerificationCodeManually();
            }
        });

        XmlEditPhoneNumberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangeLayoutToEditMobileNumber();
            }
        });
    }

    private void ChangeLayoutToEditMobileNumber() {
        XmlVerificationCodeHintText.setVisibility(View.GONE);
        XmlVerificationCode.setVisibility(View.GONE);
        XmlVerifyPhoneCodeButton.setVisibility(View.GONE);
        XmlEditPhoneNumberButton.setVisibility(View.GONE);

        XmlPhoneNumberHintText.setVisibility(View.VISIBLE);
        XmlPhoneCountryCode.setVisibility(View.VISIBLE);
        XmlUserPhoneNumber.setVisibility(View.VISIBLE);
        XmlUpdatePhoneNumberButton.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (CurrentUser == null) {
            SendUserToLoginActivity();
        } else {
            RootRef.child("Users").child(CurrentUser.getUid()).child("information").child("phone_number").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        XmlUserPhoneNumber.setHelperText(dataSnapshot.getValue().toString());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            FusedLocationProviderClient LocationClient = LocationServices.getFusedLocationProviderClient(UpdatePhoneNumberActivity.this);
            LocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        Geocoder geocoder = new Geocoder(UpdatePhoneNumberActivity.this, Locale.getDefault());
                        TimeDateAndAddress address = new TimeDateAndAddress(geocoder, location.getLatitude(), location.getLongitude());
                        XmlPhoneCountryCode.setCountryForNameCode(address.getShortCountryCode());
                    } else {
                        Toast.makeText(UpdatePhoneNumberActivity.this, "Server connection failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void SubmitVerificationCodeManually() {
        String UserEnteredCode = XmlVerificationCode.getEditText().getText().toString();
        if (UserEnteredCode.isEmpty() || UserEnteredCode.length() < 6) {
            XmlVerificationCode.setError("Invalid code");
        } else {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(VerificationId, UserEnteredCode);

            LoadingBar.setMessage("Verifying code");
            LoadingBar.setCanceledOnTouchOutside(false);
            LoadingBar.show();
            CurrentUser.linkWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        LoadingBar.dismiss();
                        UpdateMobileNumberToDatabase();
                    } else {
                        LoadingBar.dismiss();
                        Toast.makeText(UpdatePhoneNumberActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void UpdateMobileNumberToDatabase() {
        LoadingBar.setMessage("Updating mobile number");
        LoadingBar.setCanceledOnTouchOutside(false);
        LoadingBar.show();

        RootRef.child("Users").child(CurrentUser.getUid()).child("information").child("phone_number").setValue(PhoneNumberWithCountryCode).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    LoadingBar.dismiss();
                    SendUserToMainActivity();
                } else {
                    LoadingBar.dismiss();
                    Toast.makeText(UpdatePhoneNumberActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void GetFullPhoneNumber() {
        String CountryCodeWithPlus = XmlPhoneCountryCode.getSelectedCountryCodeWithPlus();
        String PhoneNumber = XmlUserPhoneNumber.getEditText().getText().toString();

        if (PhoneNumber.length() != 10) {
            if (PhoneNumber.isEmpty()) {
                XmlUserPhoneNumber.setError("Enter phone number");
            } else if (PhoneNumber.length() == 11) {
                XmlUserPhoneNumber.setError("Without country code");
            } else {
                XmlUserPhoneNumber.setError("Enter valid phone number");
            }
        } else {
            PhoneNumberWithCountryCode = CountryCodeWithPlus + PhoneNumber;
            VerifyUserPhoneNumber();
        }
    }

    private void SendUserToLoginActivity() {
        Intent LoginIntent = new Intent(UpdatePhoneNumberActivity.this, LoginActivity.class);
        LoginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(LoginIntent);
    }

    private void VerifyUserPhoneNumber () {
        LoadingBar.setMessage("Sending verification code");
        LoadingBar.setCanceledOnTouchOutside(false);
        LoadingBar.show();

        PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                String VerificationCodeAutomatically = credential.getSmsCode();
                if (VerificationCodeAutomatically == null) {
                    SubmitVerificationCodeManually();
                } else {
                    CurrentUser.linkWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                LoadingBar.dismiss();
                                UpdateMobileNumberToDatabase();
                            } else {
                                LoadingBar.dismiss();
                                Toast.makeText(UpdatePhoneNumberActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                LoadingBar.dismiss();
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(UpdatePhoneNumberActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    Toast.makeText(UpdatePhoneNumberActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(UpdatePhoneNumberActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                VerificationId = verificationId;

                XmlPhoneNumberHintText.setVisibility(View.GONE);
                XmlPhoneCountryCode.setVisibility(View.GONE);
                XmlUserPhoneNumber.setVisibility(View.GONE);
                XmlUpdatePhoneNumberButton.setVisibility(View.GONE);

                XmlVerificationCodeHintText.setText("Please enter the code we have sent to\n" + PhoneNumberWithCountryCode);
                XmlVerificationCodeHintText.setVisibility(View.VISIBLE);
                XmlVerificationCode.setVisibility(View.VISIBLE);
                XmlVerifyPhoneCodeButton.setVisibility(View.VISIBLE);
                XmlEditPhoneNumberButton.setVisibility(View.VISIBLE);

                LoadingBar.dismiss();
                Toast.makeText(UpdatePhoneNumberActivity.this, "Verification code sent", Toast.LENGTH_SHORT).show();
            }
        };

        PhoneAuthProvider.getInstance().verifyPhoneNumber(PhoneNumberWithCountryCode, 60, TimeUnit.SECONDS, this, mCallbacks);
    }



    private void SendUserToMainActivity() {
        Intent MainIntent = new Intent(UpdatePhoneNumberActivity.this, MainActivity.class);
        startActivity(MainIntent);
    }
}
