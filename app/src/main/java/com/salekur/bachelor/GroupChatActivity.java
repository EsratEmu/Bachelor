package com.salekur.bachelor;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.salekur.bachelor.authentication.LoginActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

public class GroupChatActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageView XmlSendMsgButton;
    private EditText XmlUserMsgInput;
    private ScrollView XmlScrollView;
    private TextView XmlDisplayTextMsg;

    private FirebaseAuth myauth;
    private FirebaseUser CurrentUser;
    private DatabaseReference UserRef, GroupNameRef, GroupMsgKeyRef;

    private String currentGroupName, currentUserID, currentUserName, currentDate, currentTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);


        currentGroupName = getIntent().getExtras().get("groupName").toString();
        Toast.makeText(GroupChatActivity.this, currentGroupName, Toast.LENGTH_SHORT).show();


        myauth = FirebaseAuth.getInstance();
        CurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        currentUserID = myauth.getCurrentUser().getUid();
        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        GroupNameRef = FirebaseDatabase.getInstance().getReference().child("BChat").child("Groups").child(currentGroupName);

        InitializeFields();


        GetUserInfo();


        XmlSendMsgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveMsgInfoToDatabase();

                XmlUserMsgInput.setText("");

                XmlScrollView.fullScroll(ScrollView.FOCUS_DOWN);

            }
        });
    }



    //methods...........................

    @Override
    protected void onStart() {
        super.onStart();

        if (CurrentUser == null) {
            SendUserToLoginActivity();
        }

        GroupNameRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists()){
                    DisplayMsg(dataSnapshot);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists()){
                    DisplayMsg(dataSnapshot);
                }

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    private void InitializeFields() {
        toolbar = (Toolbar) findViewById(R.id.group_chat_bar_layout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(currentGroupName);

        XmlSendMsgButton = (ImageView) findViewById(R.id.send_msg_button);
        XmlUserMsgInput = (EditText) findViewById(R.id.input_group_msg);
        XmlDisplayTextMsg = (TextView) findViewById(R.id.group_chat_text_display);
        XmlScrollView = (ScrollView) findViewById(R.id.my_scroll_view);

    }



    private void GetUserInfo() {
        UserRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    currentUserName = dataSnapshot.child("information").child("first_name").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void saveMsgInfoToDatabase() {
        String message = XmlUserMsgInput.getText().toString();
        String messageKEY = GroupNameRef.push().getKey();

        if(TextUtils.isEmpty(message)){
            Toast.makeText(this, "please write msg first..", Toast.LENGTH_SHORT).show();
        }else{
            Calendar calendar_date = Calendar.getInstance();
            SimpleDateFormat currentDateFormat = new SimpleDateFormat("MMM, dd, yyyy");
            currentDate = currentDateFormat.format(calendar_date.getTime());

            Calendar calendar_time = Calendar.getInstance();
            SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm");
            currentTime = currentTimeFormat.format(calendar_time.getTime());

            HashMap<String, Object> groupMsgKey = new HashMap<>();
            GroupNameRef.updateChildren(groupMsgKey);

            GroupMsgKeyRef = GroupNameRef.child(messageKEY);

            HashMap<String, Object> msgInfoMap = new HashMap<>();
            msgInfoMap.put("name", currentUserName);
            msgInfoMap.put("message", message);
            msgInfoMap.put("date", currentDate);
            msgInfoMap.put("time", currentTime);

            GroupMsgKeyRef.updateChildren(msgInfoMap);
        }

    }

    private void SendUserToLoginActivity() {
        Intent LoginIntent = new Intent(GroupChatActivity.this, LoginActivity.class);
        LoginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(LoginIntent);
    }


    private void DisplayMsg(DataSnapshot dataSnapshot) {
        Iterator iterator = dataSnapshot.getChildren().iterator();

        while (iterator.hasNext()){
            String chatDate = (String) ((DataSnapshot)iterator.next()).getValue();
            String chatMsg = (String) ((DataSnapshot)iterator.next()).getValue();
            String chatName = (String) ((DataSnapshot)iterator.next()).getValue();
            String chatTime = (String) ((DataSnapshot)iterator.next()).getValue();

            XmlDisplayTextMsg.append(chatName +":\n" + chatMsg + "\n" + chatTime + ", "+chatDate +" \n\n");

            XmlScrollView.fullScroll(ScrollView.FOCUS_DOWN);
        }
    }

}
