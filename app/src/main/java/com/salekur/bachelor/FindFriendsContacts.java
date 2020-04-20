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
import com.salekur.bachelor.classes.ViewHolder;
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


        FirebaseRecyclerOptions<Contacts> options = new FirebaseRecyclerOptions.Builder<Contacts>().setQuery(RootRef.child("Users"), Contacts.class).build();
        FirebaseRecyclerAdapter<Contacts, ViewHolder> adapter = new FirebaseRecyclerAdapter<Contacts, ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder holder, final int position, @NonNull Contacts model) {
                holder.InfoTitle.setText((model.getFirst_name() + " " + model.getLast_name()));
                holder.InfoSubTitle.setText(model.getPhone_number());
                holder.InfoBody.setText(model.getAbout());
                Picasso.get().load(model.getProfile_image()).placeholder(R.drawable.boy_image).into(holder.InfoImage);

                //holder.InfoSubTitle.setVisibility(View.VISIBLE);
                holder.InfoBody.setVisibility(View.VISIBLE);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String visit_user_id = getRef(position).getKey();

                        Intent profileIntent = new Intent(FindFriendsContacts.this, ProfileActivity.class);
                        profileIntent.putExtra("ProfileID", visit_user_id);

                        startActivity(profileIntent);
                    }
                });

            }


            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.information_layout, viewGroup, false);
                ViewHolder viewHolder = new ViewHolder(view);
                return viewHolder;
            }

        };
        FindFriendsRecyclerList.setAdapter(adapter);
        adapter.startListening();
    }

    private void SendUserToLoginActivity () {
        Intent LoginIntent = new Intent(FindFriendsContacts.this, LoginActivity.class);
        LoginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(LoginIntent);
    }
}

