package com.salekur.bachelor.classes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.salekur.bachelor.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessageViewHolder> {
    private List<Messages> UserMessagesList;
    private DatabaseReference RootRef, UserRef;
    private FirebaseAuth auth;

    public MessagesAdapter(List<Messages> UserMessagesList) {
        this.UserMessagesList = UserMessagesList;
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {
        public TextView SendMsgText, ReceiveMsgText;
        public CircleImageView ReceiverProfileImage;

        public  MessageViewHolder(View itemView) {
            super(itemView);

            SendMsgText = (TextView) itemView.findViewById(R.id.sender_text_msg);
            ReceiveMsgText = (TextView) itemView.findViewById(R.id.receiver_text_msg);
            ReceiverProfileImage = (CircleImageView) itemView.findViewById(R.id.messege_profile_image);
        }
    }



    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.custom_messages_layout, viewGroup, false);

        auth = FirebaseAuth.getInstance();

        return new MessageViewHolder(view);
    }

//onbind.......................................................


   @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder holder, int position) {
        String messageSenderID = auth.getCurrentUser().getUid();
        Messages messages = UserMessagesList.get(position);

        String FromUserID = messages.getFrom();

        RootRef = FirebaseDatabase.getInstance().getReference();
        UserRef = RootRef.child("Users").child(FromUserID);

        UserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("profile_image")) {
                    String ReceiverImage = dataSnapshot.child("profile_image").getValue().toString();

                    Picasso.get().load(ReceiverImage).placeholder(R.drawable.boy_image).into(holder.ReceiverProfileImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if(FromUserID.equals(messageSenderID)) {
            holder.SendMsgText.setBackgroundResource(R.drawable.colorful_msg_layout);
            holder.SendMsgText.setText(messages.getMessage());

            holder.ReceiveMsgText.setVisibility(View.INVISIBLE);
            holder.ReceiverProfileImage.setVisibility(View.INVISIBLE);
            holder.SendMsgText.setVisibility(View.VISIBLE);

        }  else {
            holder.SendMsgText.setVisibility(View.INVISIBLE);
            holder.ReceiverProfileImage.setVisibility(View.VISIBLE);
            holder.ReceiveMsgText.setVisibility(View.VISIBLE);

            holder.ReceiveMsgText.setBackgroundResource(R.drawable.silver_msg_layout);
            holder.ReceiveMsgText.setText(messages.getMessage());
        }


    }


    @Override
    public int getItemCount() {

        return UserMessagesList.size();
    }

}