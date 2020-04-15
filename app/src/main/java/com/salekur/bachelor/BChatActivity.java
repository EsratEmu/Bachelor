package com.salekur.bachelor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.salekur.bachelor.authentication.LoginActivity;
import com.salekur.bachelor.classes.BChatTabAccessAdapter;

import java.util.HashMap;
import java.util.Map;

public class BChatActivity extends AppCompatActivity {
    private Toolbar myToolbar;
    private ViewPager myViewPager;
    private TabLayout myTabLayout;
    private BChatTabAccessAdapter myTabAccessAdapter;

    private FirebaseUser CurrentUser;
    private DatabaseReference RootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b_chat);

        CurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        RootRef = FirebaseDatabase.getInstance().getReference();

        myToolbar= (Toolbar) findViewById(R.id.main_toolbar_bchat);
        myToolbar.setTitle("BChat");
        setSupportActionBar(myToolbar);

        myViewPager = (ViewPager) findViewById(R.id.main_tab_page);
        myTabAccessAdapter = new BChatTabAccessAdapter(getSupportFragmentManager());
        myViewPager.setAdapter(myTabAccessAdapter);

        myTabLayout = (TabLayout) findViewById(R.id.main_tab);
        myTabLayout.setupWithViewPager(myViewPager);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (CurrentUser == null) {
            SendUserToLoginActivity();
        } else {
            StartWorkingInThisActivity();
        }
    }

    private void StartWorkingInThisActivity() {
        String UserID = CurrentUser.getUid();
    }


    private void SendUserToLoginActivity() {
        Intent LoginIntent = new Intent(BChatActivity.this, LoginActivity.class);
        LoginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(LoginIntent);
    }

    private void SendUserToFindFriendsActivity() {
        Intent FindFriendsIntent = new Intent(BChatActivity.this, FindFriendsContacts.class);
        startActivity(FindFriendsIntent);
    }

    // bachelor chat toolbar option menu bar

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.options_menu_bchat, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId() == R.id.main_findfriends_option) {
            SendUserToFindFriendsActivity();

        }
        if(item.getItemId() == R.id.main_settings_option) {
            //..........
        }
        if(item.getItemId() == R.id.main_newgroup_option) {
            RequestNewGroup();
        }
        return true;
    }


    //For creating new group only........

    private void RequestNewGroup() {
        final Dialog dialog = new Dialog(BChatActivity.this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_default);

        TextView DialogTitle = (TextView) dialog.findViewById(R.id.dialog_default_title);
        TextView DialogDes = (TextView) dialog.findViewById(R.id.dialog_default_description);
        final EditText DialogInput = (EditText) dialog.findViewById(R.id.dialog_default_input);
        MaterialButton DialogOk = (MaterialButton) dialog.findViewById(R.id.dialog_default_ok);
        MaterialButton DialogCancel = (MaterialButton) dialog.findViewById(R.id.dialog_default_cancel);

        DialogTitle.setText("Create Group");
        DialogDes.setText("Write a new name for your new chat group");
        DialogInput.setHint("Enter group name");
        DialogOk.setText("Create");

        DialogDes.setVisibility(View.VISIBLE);
        DialogInput.setVisibility(View.VISIBLE);
        DialogCancel.setVisibility(View.VISIBLE);

        DialogOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                String groupName = DialogInput.getText().toString();

                if(TextUtils.isEmpty(groupName)) {
                    Toast.makeText(BChatActivity.this, "Please write your group name :)", Toast.LENGTH_SHORT).show();
                } else {
                    CreateNewGroup(groupName);
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

    private void CreateNewGroup(final String groupName) {
        RootRef.child("BChat").child("Groups").child(groupName).setValue("").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(BChatActivity.this, groupName + " is Created Successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(BChatActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}


