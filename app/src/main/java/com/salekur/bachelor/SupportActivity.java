package com.salekur.bachelor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.salekur.bachelor.admin.AdminActivity;
import com.salekur.bachelor.authentication.ForgetPasswordActivity;
import com.salekur.bachelor.authentication.RegisterActivity;
import com.salekur.bachelor.classes.TimeDateAndAddress;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class SupportActivity extends AppCompatActivity {
    private ImageView XmlImageAdmin;
    private CardView XmlCardForgetPassword, XmlCardLiveChat, XmlCardBMeal, XmlCardBChat, XmlCardBTask, XmlCardBShop, XmlCardWebsite, XmlCardBlog, XmlCardComplain, XmlCardAbout;

    private FirebaseUser CurrentUser;
    private DatabaseReference RootRef;

    private ProgressDialog LoadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);

        CurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        RootRef = FirebaseDatabase.getInstance().getReference();
        LoadingBar = new ProgressDialog(this);

        XmlImageAdmin = (ImageView) findViewById(R.id.support_image_admin);
        XmlCardForgetPassword = (CardView) findViewById(R.id.support_card_password);
        XmlCardLiveChat = (CardView) findViewById(R.id.support_card_live_chat);
        XmlCardBMeal = (CardView) findViewById(R.id.support_card_bmeal);
        XmlCardBChat = (CardView) findViewById(R.id.support_card_bchat);
        XmlCardBTask = (CardView) findViewById(R.id.support_card_btask);
        XmlCardBShop = (CardView) findViewById(R.id.support_card_bshop);
        XmlCardWebsite = (CardView) findViewById(R.id.support_card_website);
        XmlCardBlog = (CardView) findViewById(R.id.support_card_blog);
        XmlCardComplain = (CardView) findViewById(R.id.support_card_complain);
        XmlCardAbout = (CardView) findViewById(R.id.support_card_about);

        XmlImageAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckAdminStatus();
            }
        });

        XmlCardForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToForgetPasswordActivity();
            }
        });

        XmlCardLiveChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SupportActivity.this, "Coming Soon", Toast.LENGTH_SHORT).show();
            }
        });

        XmlCardBMeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToWebViewActivity("https://dev-bac.blogspot.com/search/label/Bachelor%20Meal");
            }
        });

        XmlCardBChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToWebViewActivity("https://dev-bac.blogspot.com/search/label/Bachelor%20Chat");
            }
        });

        XmlCardBTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToWebViewActivity("https://dev-bac.blogspot.com/search/label/Bachelor%20Task");
            }
        });

        XmlCardBShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToWebViewActivity("https://dev-bac.blogspot.com/search/label/Bachelor%20Shop");
            }
        });

        XmlCardWebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToWebViewActivity("https://devbac.github.io");
            }
        });

        XmlCardBlog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToWebViewActivity("https://dev-bac.blogspot.com");
            }
        });

        XmlCardComplain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetAndSendComplainFromDialog();
            }
        });

        XmlCardAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToWebViewActivity("https://devbac.github.io/about.html");
            }
        });
    }

    private void GetAndSendComplainFromDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_default);

        TextView DialogTitle = (TextView) dialog.findViewById(R.id.dialog_default_title);
        TextView DialogDes = (TextView) dialog.findViewById(R.id.dialog_default_description);
        final EditText DialogPreInput = (EditText) dialog.findViewById(R.id.dialog_default_pre_input);
        final EditText DialogInput = (EditText) dialog.findViewById(R.id.dialog_default_input);
        MaterialButton DialogOk = (MaterialButton) dialog.findViewById(R.id.dialog_default_ok);
        MaterialButton DialogCancel = (MaterialButton) dialog.findViewById(R.id.dialog_default_cancel);

        DialogTitle.setText("Bachelor Complain");
        DialogDes.setText("Please write your complaint or suggestion briefly if you have any");
        DialogPreInput.setHint("Complain title");
        DialogInput.setHint("Complain details");
        DialogOk.setText("Submit");

        DialogDes.setVisibility(View.VISIBLE);
        DialogPreInput.setVisibility(View.VISIBLE);
        DialogInput.setVisibility(View.VISIBLE);
        DialogCancel.setVisibility(View.VISIBLE);

        DialogOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ComplainTitle = DialogPreInput.getText().toString();
                String ComplainDetails = DialogInput.getText().toString();

                if (ComplainTitle.isEmpty()) {
                    DialogPreInput.setError("Enter title");
                } else if (ComplainDetails.isEmpty()) {
                    DialogInput.setError("Write complain");
                } else {
                    dialog.dismiss();
                    Calendar calendar = Calendar.getInstance();

                    SimpleDateFormat DateFormat = new SimpleDateFormat("MMM dd, YYYY");
                    String CurrentDate = DateFormat.format(calendar.getTime());

                    SimpleDateFormat TimeFormat = new SimpleDateFormat("hh:mm aa");
                    String CurrentTime = TimeFormat.format(calendar.getTime());

                    Map ComplainMap = new HashMap();
                    ComplainMap.put("title", ComplainTitle);
                    ComplainMap.put("details", ComplainDetails);
                    ComplainMap.put("date", CurrentDate);
                    ComplainMap.put("time", CurrentTime);

                    LoadingBar.setMessage("Sending Complain");
                    LoadingBar.setCanceledOnTouchOutside(false);
                    LoadingBar.show();
                    String ComplainKey = RootRef.child("Bachelor").child("Complains").push().getKey();
                    RootRef.child("Bachelor").child("Complains").child(ComplainKey).updateChildren(ComplainMap).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()) {
                                LoadingBar.dismiss();
                                Toast.makeText(SupportActivity.this, "Complain Sent", Toast.LENGTH_SHORT).show();
                            } else {
                                LoadingBar.dismiss();
                                Toast.makeText(SupportActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                dialog.show();
                            }
                        }
                    });
                }
            }
        });

        DialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void CheckAdminStatus() {
        if (CurrentUser == null) {
            Toast.makeText(SupportActivity.this, "Admin Panel: Login first", Toast.LENGTH_SHORT).show();
        } else {
            LoadingBar.setMessage("Checking Status");
            LoadingBar.setCanceledOnTouchOutside(false);
            LoadingBar.show();
            RootRef.child("Bachelor").child("Admins").child(CurrentUser.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        LoadingBar.dismiss();
                        VerifyAdminByPassword();
                    } else {
                        LoadingBar.dismiss();
                        Toast.makeText(SupportActivity.this, "You're not an admin. Contact with an admin to see admin panel.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void VerifyAdminByPassword() {
        final Dialog dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_default);

        TextView DialogTitle = (TextView) dialog.findViewById(R.id.dialog_default_title);
        TextView DialogDes = (TextView) dialog.findViewById(R.id.dialog_default_description);
        final EditText DialogInput = (EditText) dialog.findViewById(R.id.dialog_default_input);
        MaterialButton DialogOk = (MaterialButton) dialog.findViewById(R.id.dialog_default_ok);
        MaterialButton DialogCancel = (MaterialButton) dialog.findViewById(R.id.dialog_default_cancel);

        DialogInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        DialogTitle.setText("Admin Verification");
        DialogDes.setText("Please enter your account password to verify your account as admin");
        DialogInput.setHint("Enter password");
        DialogOk.setText("Verify");

        DialogDes.setVisibility(View.VISIBLE);
        DialogInput.setVisibility(View.VISIBLE);
        DialogCancel.setVisibility(View.VISIBLE);

        DialogOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = DialogInput.getText().toString();

                if (password.isEmpty()) {
                    DialogInput.setError("Password Required");
                } else {
                    dialog.dismiss();
                    AuthCredential credential = EmailAuthProvider.getCredential(CurrentUser.getEmail(), password);

                    LoadingBar.setMessage("Verifying");
                    LoadingBar.setCanceledOnTouchOutside(false);
                    LoadingBar.show();
                    CurrentUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                LoadingBar.dismiss();
                                SendUserToAdminActivity();
                            } else {
                                LoadingBar.dismiss();
                                Toast.makeText(SupportActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                dialog.show();
                            }
                        }
                    });
                }
            }
        });

        DialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void SendUserToAdminActivity() {
        Intent AdminIntent = new Intent(SupportActivity.this, AdminActivity.class);
        startActivity(AdminIntent);
    }

    private void SendUserToForgetPasswordActivity() {
        Intent ForgetPasswordIntent = new Intent(SupportActivity.this, ForgetPasswordActivity.class);
        startActivity(ForgetPasswordIntent);
    }

    private void SendUserToWebViewActivity(String address) {
        Intent WebIntent = new Intent(SupportActivity.this, WebActivity.class);
        WebIntent.putExtra("address", address);
        startActivity(WebIntent);
    }

}
