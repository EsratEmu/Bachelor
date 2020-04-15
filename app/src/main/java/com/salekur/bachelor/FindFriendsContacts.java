package com.salekur.bachelor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.salekur.bachelor.authentication.LoginActivity;
import com.salekur.bachelor.classes.Contacts;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindFriendsContacts extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView FindFriendsRecyclerList;
    private DatabaseReference RootRef;

    private FirebaseUser CurrentUser;
    private String CurrentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends_contacts);

        CurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        RootRef = FirebaseDatabase.getInstance().getReference();

        FindFriendsRecyclerList = (RecyclerView) findViewById(R.id.find_friends_recycler_list);
        FindFriendsRecyclerList.setLayoutManager(new LinearLayoutManager(this));

        toolbar = (Toolbar) findViewById(R.id.find_friend_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Find Friends");
    }

    @Override
    protected void onStart() {
        super.onStart();


        if (CurrentUser == null) {
            SendUserToLoginActivity();
        } else {
            CurrentUserID = CurrentUser.getUid();

        }


        FirebaseRecyclerOptions<Contacts> options = new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(RootRef.child("Users"), Contacts.class).build();

        FirebaseRecyclerAdapter<Contacts, FindFriendsViewHolder> adapter = new FirebaseRecyclerAdapter<Contacts, FindFriendsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FindFriendsViewHolder holder, int position, @NonNull Contacts model) {
                holder.userNameBchat.setText((model.getFirst_name() + " " + model.getLast_name()));
                holder.userStatusBchat.setText(model.getAbout());
                Picasso.get().load(model.getProfile_image()).placeholder(R.drawable.boy_image).into(holder.profileImageBchat);

            }


            @NonNull
            @Override
            public FindFriendsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.contact_display_layout, viewGroup, false);
                FindFriendsViewHolder viewHolder = new FindFriendsViewHolder(view);
                return viewHolder;
            }

        };

        FindFriendsRecyclerList.setAdapter(adapter);

        adapter.startListening();
    }

    public static class FindFriendsViewHolder extends RecyclerView.ViewHolder {
        TextView userNameBchat, userStatusBchat;
        CircleImageView profileImageBchat;

        public FindFriendsViewHolder(@NonNull View itemView) {
            super(itemView);

            userNameBchat = itemView.findViewById(R.id.user_profile_name_contacts_bchat);
            userStatusBchat = itemView.findViewById(R.id.user_status_contacts_bchat);
            profileImageBchat = itemView.findViewById(R.id.user_profile_image_contacts_bchat);

        }
    }

    private void SendUserToLoginActivity () {
        Intent LoginIntent = new Intent(FindFriendsContacts.this, LoginActivity.class);
        LoginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(LoginIntent);
    }
}

