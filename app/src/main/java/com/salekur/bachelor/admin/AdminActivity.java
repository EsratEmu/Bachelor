package com.salekur.bachelor.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.salekur.bachelor.ProfileActivity;
import com.salekur.bachelor.R;
import com.salekur.bachelor.authentication.LoginActivity;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdminActivity extends AppCompatActivity {
    private TextView XmlUserAdminName;
    private CircleImageView XmlUserAdminImage;
    private CardView XmlCardComplain, XmlCardUsers, XmlCardAddAdmin, XmlCardServer;

    private FirebaseUser CurrentUser;
    private DatabaseReference RootRef;

    private ProgressDialog LoadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        CurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        RootRef = FirebaseDatabase.getInstance().getReference();
        LoadingBar = new ProgressDialog(this);

        XmlUserAdminName = (TextView) findViewById(R.id.admin_user_name);
        XmlUserAdminImage = (CircleImageView) findViewById(R.id.admin_user_image);
        XmlCardComplain = (CardView) findViewById(R.id.admin_card_complain);
        XmlCardUsers = (CardView) findViewById(R.id.admin_card_users);
        XmlCardAddAdmin = (CardView) findViewById(R.id.admin_card_add_admin);
        XmlCardServer = (CardView) findViewById(R.id.admin_card_server);

        XmlUserAdminImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToProfileActivity();
            }
        });

        XmlCardComplain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToComplainViewActivity();
            }
        });

        XmlCardUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToUserViewActivity();
            }
        });

        XmlCardAddAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetEmailAndCheckDatabase();
            }
        });

        XmlCardServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateDatabaseServerStatus();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (CurrentUser == null) {
            SendUserToLoginActivity();
        } else {
            XmlUserAdminName.setText(CurrentUser.getDisplayName());
            Picasso.get().load(CurrentUser.getPhotoUrl()).placeholder(R.drawable.boy_image).into(XmlUserAdminImage);
        }
    }

    private void UpdateDatabaseServerStatus() {
        final Dialog dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_default);

        TextView DialogTitle = (TextView) dialog.findViewById(R.id.dialog_default_title);
        TextView DialogDes = (TextView) dialog.findViewById(R.id.dialog_default_description);
        final Switch DialogSwitch = (Switch) dialog.findViewById(R.id.dialog_default_switch);
        MaterialButton DialogOk = (MaterialButton) dialog.findViewById(R.id.dialog_default_ok);

        DialogTitle.setText("Server Status");
        DialogDes.setText("Turn your server up or down for general user, admin can login anytime.");
        DialogOk.setText("Ok");

        DialogDes.setVisibility(View.VISIBLE);
        DialogSwitch.setText("Down Server");
        DialogSwitch.setVisibility(View.VISIBLE);

        RootRef.child("Bachelor").child("Server").child("status").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.getValue().toString().equals("down")) {
                        DialogSwitch.setChecked(true);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DialogOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                String status = "up";

                if (DialogSwitch.isChecked()) {
                    status = "down";
                }

                LoadingBar.setMessage("Updating Status");
                LoadingBar.setCanceledOnTouchOutside(false);
                LoadingBar.show();
                RootRef.child("Bachelor").child("Server").child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            LoadingBar.dismiss();
                            Toast.makeText(AdminActivity.this, "Server status updated", Toast.LENGTH_SHORT).show();
                        } else {
                            LoadingBar.dismiss();
                            Toast.makeText(AdminActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        dialog.show();
    }

    private void GetEmailAndCheckDatabase() {
        final Dialog dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_default);

        TextView DialogTitle = (TextView) dialog.findViewById(R.id.dialog_default_title);
        TextView DialogDes = (TextView) dialog.findViewById(R.id.dialog_default_description);
        final EditText DialogInput = (EditText) dialog.findViewById(R.id.dialog_default_input);
        MaterialButton DialogOk = (MaterialButton) dialog.findViewById(R.id.dialog_default_ok);
        MaterialButton DialogCancel = (MaterialButton) dialog.findViewById(R.id.dialog_default_cancel);

        DialogTitle.setText("Add Admin");
        DialogDes.setText("Please write user email to add them in admin panel");
        DialogInput.setHint("Enter email");
        DialogOk.setText("Add");

        DialogDes.setVisibility(View.VISIBLE);
        DialogInput.setVisibility(View.VISIBLE);
        DialogCancel.setVisibility(View.VISIBLE);

        DialogOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = DialogInput.getText().toString();

                if (email.isEmpty()) {
                    DialogInput.setError("Enter email");
                } else {
                    dialog.dismiss();

                    LoadingBar.setMessage("Checking email");
                    LoadingBar.setCanceledOnTouchOutside(false);
                    LoadingBar.show();
                    RootRef.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                                    String GetEmail = childSnapshot.child("email").getValue().toString();

                                    if (email.equals(GetEmail)) {
                                        LoadingBar.dismiss();
                                        final String UserID = childSnapshot.getKey();

                                        LoadingBar.setMessage("Checking admin");
                                        LoadingBar.setCanceledOnTouchOutside(false);
                                        LoadingBar.show();
                                        RootRef.child("Bachelor").child("Admins").child(UserID).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists()) {
                                                    LoadingBar.dismiss();
                                                    Toast.makeText(AdminActivity.this, "Admin already exist", Toast.LENGTH_SHORT).show();
                                                    dialog.show();
                                                } else {
                                                    LoadingBar.dismiss();

                                                    LoadingBar.setMessage("Adding admin");
                                                    LoadingBar.setCanceledOnTouchOutside(false);
                                                    LoadingBar.show();
                                                    RootRef.child("Bachelor").child("Admins").child(UserID).setValue("admin").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                LoadingBar.dismiss();
                                                                dialog.dismiss();
                                                                Toast.makeText(AdminActivity.this, email + " has been added on admin database", Toast.LENGTH_SHORT).show();
                                                            } else {
                                                                LoadingBar.dismiss();
                                                                Toast.makeText(AdminActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                }
                                LoadingBar.dismiss();
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
        });

        DialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void SendUserToLoginActivity() {
        Intent LoginIntent = new Intent(AdminActivity.this, LoginActivity.class);
        LoginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(LoginIntent);
    }

    private void SendUserToProfileActivity() {
        Intent ProfileIntent = new Intent(AdminActivity.this, ProfileActivity.class);
        ProfileIntent.putExtra("ProfileID", CurrentUser.getUid());
        startActivity(ProfileIntent);
    }

    private void SendUserToComplainViewActivity() {
        Intent ComplainViewIntent = new Intent(AdminActivity.this, ComplainViewActivity.class);
        startActivity(ComplainViewIntent);
    }

    private void SendUserToUserViewActivity() {
        Intent UserViewIntent = new Intent(AdminActivity.this, UserViewActivity.class);
        startActivity(UserViewIntent);
    }

}
