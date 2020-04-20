package com.salekur.bachelor.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.salekur.bachelor.PrivateChatActivity;
import com.salekur.bachelor.R;
import com.salekur.bachelor.classes.Contacts;
import com.salekur.bachelor.classes.ViewHolder;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatsFragment extends Fragment {
    private View PrivateChatView;
    private RecyclerView ChatListView;
    private FirebaseUser CurrentUser;

    private DatabaseReference RootRef, MessageRef;
    private FirebaseAuth auth;

    public ChatsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        PrivateChatView = inflater.inflate(R.layout.fragment_chats, container, false);

        CurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        RootRef = FirebaseDatabase.getInstance().getReference();
        ChatListView = (RecyclerView) PrivateChatView.findViewById(R.id.chat_list_view);
        ChatListView.setLayoutManager(new LinearLayoutManager(getContext()));

        return PrivateChatView;
    }


    @Override
    public void onStart() {
        super.onStart();
        if (CurrentUser == null) {
            //SendUserToLoginActivity();
        } else {
            DisplayChatListFromDatabase();
        }
    }

    private void DisplayChatListFromDatabase() {
        FirebaseRecyclerOptions<Contacts> options = new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(RootRef.child("BChat").child("Messages").child(CurrentUser.getUid()), Contacts.class).build();
        FirebaseRecyclerAdapter<Contacts, ViewHolder> adapter = new FirebaseRecyclerAdapter<Contacts, ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ViewHolder holder, final int position, @NonNull Contacts model) {
                final String UserID = getRef(position).getKey();

                RootRef.child("Users").child(UserID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            if (dataSnapshot.hasChild("profile_image")) {
                                String image = dataSnapshot.child("profile_image").getValue().toString();
                                Picasso.get().load(image).placeholder(R.drawable.boy_image).into(holder.InfoImage);
                            }
                            if (dataSnapshot.hasChild("first_name") && dataSnapshot.hasChild("last_name")) {
                                String first_name = dataSnapshot.child("first_name").getValue().toString();
                                String last_name = dataSnapshot.child("last_name").getValue().toString();
                                holder.InfoTitle.setText(first_name + " " + last_name);
                            }

                            MessageRef = RootRef.child("BChat").child("Messages");
                            Query LastMessageQuery = MessageRef.child(CurrentUser.getUid()).child(UserID).orderByKey().limitToLast(1);
                            LastMessageQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        for (DataSnapshot LastMessageData: dataSnapshot.getChildren()) {
                                            if (LastMessageData.hasChild("message") && LastMessageData.hasChild("from")) {
                                                String LastMessage = LastMessageData.child("message").getValue().toString();
                                                String LastMessageUser = LastMessageData.child("from").getValue().toString();
                                                String LastMessageDate = LastMessageData.child("date").getValue().toString();
                                                String LastMessageTime = LastMessageData.child("time").getValue().toString();

                                                if (CurrentUser.getUid().equals(LastMessageUser)) {
                                                    holder.InfoBody.setText("You: " + LastMessage);
                                                    holder.InfoSubTitle.setText("Last seen: " + LastMessageDate + " " + LastMessageTime);
                                                    holder.InfoBody.setVisibility(View.VISIBLE);
                                                    holder.InfoSubTitle.setVisibility(View.VISIBLE);
                                                } else {
                                                    holder.InfoBody.setText(LastMessage);
                                                    holder.InfoSubTitle.setText("Last seen: " + LastMessageDate + " " + LastMessageTime);
                                                    holder.InfoBody.setVisibility(View.VISIBLE);
                                                    holder.InfoSubTitle.setVisibility(View.VISIBLE);
                                                }
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent ChatIntent = new Intent(getContext(), PrivateChatActivity.class);
                        ChatIntent.putExtra("ProfileID", UserID);
                        startActivity(ChatIntent);
                    }
                });
            }

            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.information_layout, parent, false);
                ViewHolder viewHolder = new ViewHolder(view);
                return viewHolder;
            }
        };
        ChatListView.setAdapter(adapter);
        adapter.startListening();
    }
}
