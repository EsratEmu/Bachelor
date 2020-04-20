package com.salekur.bachelor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.salekur.bachelor.classes.Messages;
import com.salekur.bachelor.classes.MessagesAdapter;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class PrivateChatActivity extends AppCompatActivity {
    private String MsgReceiverID, MsgSenderID; // MsgReceiverImage;
    private TextView UserName;
    private CircleImageView UserImage;

    private Toolbar PrivateChatToolbar;
    private DatabaseReference RootRef;

    private ImageView SendMsgButton;
    private EditText MsgInputText;
    private FirebaseAuth auth;

    private RecyclerView UserMessageList;

    private final List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessagesAdapter messagesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_chat);

        auth = FirebaseAuth.getInstance();
        MsgSenderID = auth.getCurrentUser().getUid();
        MsgReceiverID = getIntent().getStringExtra("ProfileID");
        //MsgReceiverImage = getIntent().getExtras().get("profile_image").toString();

        RootRef = FirebaseDatabase.getInstance().getReference();

        InitializeController();

        SendMsgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendMessage();

            }
        });
    }


    private void InitializeController(){

        PrivateChatToolbar = (Toolbar) findViewById(R.id.private_chat_toolbar);
        setSupportActionBar(PrivateChatToolbar);

        ActionBar actionBar = getSupportActionBar();
        //actionBar.setDisplayHomeAsUpEnabled(true); // for toolbar upper back button
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View actionBarView = layoutInflater.inflate(R.layout.custom_chat_bar , null);
        actionBar.setCustomView(actionBarView);

        UserImage = (CircleImageView) findViewById(R.id.custom_profile_image);
        UserName = (TextView) findViewById(R.id.custom_profile_name);

        SendMsgButton = (ImageView) findViewById(R.id.send_chat_msg_button);
        MsgInputText = (EditText) findViewById(R.id.input_private_chat_msg);

        messagesAdapter = new MessagesAdapter(messagesList);
        UserMessageList = (RecyclerView) findViewById(R.id.private_msg_list);
        linearLayoutManager = new LinearLayoutManager(this);
        UserMessageList.setLayoutManager(linearLayoutManager);
        UserMessageList.setAdapter(messagesAdapter);
    }


    @Override
    protected void onStart() {
        super.onStart();

        RootRef.child("Users").child(MsgReceiverID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.hasChild("first_name") && dataSnapshot.hasChild("last_name")) {
                        UserName.setText(dataSnapshot.child("first_name").getValue().toString() + " " + dataSnapshot.child("last_name").getValue().toString());
                    }
                    if (dataSnapshot.hasChild("profile_image")) {
                        Picasso.get().load(dataSnapshot.child("profile_image").getValue().toString()).placeholder(R.drawable.boy_image).into(UserImage);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        RootRef.child("BChat").child("Messages").child(MsgSenderID).child(MsgReceiverID).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Messages messages = dataSnapshot.getValue(Messages.class);
                messagesList.add(messages);
                messagesAdapter.notifyDataSetChanged();

                UserMessageList.smoothScrollToPosition(UserMessageList.getAdapter().getItemCount());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

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



    private void SendMessage(){
        String messageText = MsgInputText.getText().toString();

        if(TextUtils.isEmpty(messageText)) {
            Toast.makeText(this, "Write Messages plz..", Toast.LENGTH_SHORT).show();
        } else {
            String messageSenderRef = "BChat/Messages/" + MsgSenderID + "/" + MsgReceiverID;
            String messageReceiverRef = "BChat/Messages/" + MsgReceiverID + "/" + MsgSenderID;

            String messagesPushID = RootRef.child("BChat").child("Messages").child(MsgSenderID).child(MsgReceiverID).push().getKey();

            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat DateFormat = new SimpleDateFormat("MMM dd, YYYY");
            String CurrentDate = DateFormat.format(calendar.getTime());

            SimpleDateFormat TimeFormat = new SimpleDateFormat("hh:mm aa");
            String CurrentTime = TimeFormat.format(calendar.getTime());

            Map MsgTextBody = new HashMap();
            MsgTextBody.put("message", messageText);
            MsgTextBody.put("from", MsgSenderID);
            MsgTextBody.put("date", CurrentDate);
            MsgTextBody.put("time", CurrentTime);

            Map messagesBodyDetails = new HashMap();
            messagesBodyDetails.put(messageSenderRef + "/" + messagesPushID, MsgTextBody);
            messagesBodyDetails.put(messageReceiverRef + "/" + messagesPushID, MsgTextBody);

            RootRef.updateChildren(messagesBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (!task.isSuccessful()){
                        Toast.makeText(PrivateChatActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    MsgInputText.setText("");
                }
            });

        }
    }
}
