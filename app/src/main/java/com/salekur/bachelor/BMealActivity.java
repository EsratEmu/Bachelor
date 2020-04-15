package com.salekur.bachelor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.salekur.bachelor.authentication.LoginActivity;
import com.salekur.bachelor.classes.TabAccessorAdapter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class BMealActivity extends AppCompatActivity {
    private RelativeLayout BMealPreLayout, BMealPostLayout;
    private Toolbar BMealPreToolbar, BMealPostToolbar;
    private TabLayout BMealTabLayout;
    private ViewPager BMealViewPager;
    private MaterialButton CreateNewGroupButton;

    private DatabaseReference RootRef;
    private FirebaseUser CurrentUser;
    private ProgressDialog LoadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b_meal);

        BMealPreToolbar = (Toolbar) findViewById(R.id.bmeal_pre_toolbar);
        setSupportActionBar(BMealPreToolbar);
        BMealPostToolbar = (Toolbar) findViewById(R.id.bmeal_post_toolbar);
        setSupportActionBar(BMealPostToolbar);
        getSupportActionBar().setTitle("BMeal");

        BMealTabLayout = (TabLayout) findViewById(R.id.bmeal_tab_bar);
        BMealViewPager = (ViewPager) findViewById(R.id.bmeal_view_pager);
        BMealPreLayout = (RelativeLayout) findViewById(R.id.bmeal_pre_layout);
        BMealPostLayout = (RelativeLayout) findViewById(R.id.bmeal_post_layout);
        CreateNewGroupButton = (MaterialButton) findViewById(R.id.bmeal_button_create_group);

        TabAccessorAdapter adapter = new TabAccessorAdapter(getSupportFragmentManager());
        BMealViewPager.setAdapter(adapter);

        BMealTabLayout.setupWithViewPager(BMealViewPager);
        BMealTabLayout.setSelectedTabIndicatorColor(Color.parseColor("#FFFFFF"));
        BMealTabLayout.setTabTextColors(Color.parseColor("#727272"), Color.parseColor("#FFFFFF"));

        CurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        RootRef = FirebaseDatabase.getInstance().getReference();
        LoadingBar = new ProgressDialog(this);

        CreateNewGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateNewBMealGroup();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (CurrentUser == null) {
            SendUserToLoginActivity();
        } else {
            RootRef.child("BMeal").child("Users").child(CurrentUser.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // user has a BMeal group
                        BMealPreLayout.setVisibility(View.GONE);
                        BMealPostLayout.setVisibility(View.VISIBLE);
                    } else {
                        // user has not any BMeal group
                        BMealPostLayout.setVisibility(View.GONE);
                        BMealPreLayout.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void CreateNewBMealGroup() {
        final Dialog dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_default);

        TextView DialogTitle = (TextView) dialog.findViewById(R.id.dialog_default_title);
        TextView DialogDes = (TextView) dialog.findViewById(R.id.dialog_default_description);
        final EditText DialogInput = (EditText) dialog.findViewById(R.id.dialog_default_input);
        MaterialButton DialogOk = (MaterialButton) dialog.findViewById(R.id.dialog_default_ok);
        MaterialButton DialogCancel = (MaterialButton) dialog.findViewById(R.id.dialog_default_cancel);

        DialogTitle.setText("Create Group");
        DialogDes.setText("Enter a new name of your group");
        DialogInput.setHint("Enter group name");
        DialogOk.setText("Create");

        DialogDes.setVisibility(View.VISIBLE);
        DialogInput.setVisibility(View.VISIBLE);
        DialogCancel.setVisibility(View.VISIBLE);

        DialogOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                final String GroupName = DialogInput.getText().toString();
                if (GroupName.isEmpty()) {
                    dialog.show();
                    DialogInput.setError("Enter group name");
                } else {
                    Calendar calendar = Calendar.getInstance();

                    SimpleDateFormat DateFormat = new SimpleDateFormat("MMM dd, YYYY");
                    String CurrentDate = DateFormat.format(calendar.getTime());

                    SimpleDateFormat TimeFormat = new SimpleDateFormat("hh:mm aa");
                    String CurrentTime = TimeFormat.format(calendar.getTime());

                    String GroupID = RootRef.child("BMeal").child("Groups").push().getKey();

                    Map GroupMap = new HashMap();
                    GroupMap.put("name", GroupName);
                    GroupMap.put("group_id", GroupID);
                    GroupMap.put("admin", CurrentUser.getUid());
                    GroupMap.put("register_date", CurrentDate);
                    GroupMap.put("register_time", CurrentTime);

                    Map UserGroupMap = new HashMap();
                    UserGroupMap.put("BMeal/Groups/" + GroupID, GroupMap);
                    UserGroupMap.put("BMeal/Users/" + CurrentUser.getUid(), GroupID);

                    LoadingBar.setMessage("Creating " + GroupName);
                    LoadingBar.setCanceledOnTouchOutside(false);
                    LoadingBar.show();
                    RootRef.updateChildren(UserGroupMap).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()) {
                                LoadingBar.dismiss();
                                Toast.makeText(BMealActivity.this, GroupName + " is created", Toast.LENGTH_SHORT).show();
                            } else {
                                LoadingBar.dismiss();
                                Toast.makeText(BMealActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
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

    private void SendUserToLoginActivity() {
        Intent LoginIntent = new Intent(BMealActivity.this, LoginActivity.class);
        LoginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(LoginIntent);
    }
}
