package com.salekur.bachelor.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.salekur.bachelor.PrivateChatActivity;
import com.salekur.bachelor.ProfileActivity;
import com.salekur.bachelor.R;
import com.salekur.bachelor.classes.Contacts;
import com.salekur.bachelor.classes.ViewHolder;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactsFragment extends Fragment {
    private RecyclerView ContactsList;
    private DatabaseReference RootRef;
    private FirebaseUser CurrentUser;


    public ContactsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);

        ContactsList = (RecyclerView) view.findViewById(R.id.fragments_contacts_recycle_view);
        ContactsList.setLayoutManager(new LinearLayoutManager(getContext()));
        RootRef = FirebaseDatabase.getInstance().getReference();
        CurrentUser = FirebaseAuth.getInstance().getCurrentUser();


        return view;
    }


    @Override
    public void onStart() {
        super.onStart();

        if(CurrentUser == null) {
            //SendUserToLoginActivity();
        } else {
            DisplayContactListFromDatabase();
        }


    }

    private void DisplayContactListFromDatabase() {
        FirebaseRecyclerOptions<Contacts> options = new FirebaseRecyclerOptions.Builder<Contacts>().setQuery(RootRef.child("Users"), Contacts.class).build();
        FirebaseRecyclerAdapter<Contacts, ViewHolder> adapter = new FirebaseRecyclerAdapter<Contacts, ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Contacts model) {
                final String UserID = getRef(position).getKey();

                Picasso.get().load(model.getProfile_image()).placeholder(R.drawable.boy_image).into(holder.InfoImage);
                holder.InfoTitle.setText(model.getFirst_name() + " " + model.getLast_name());
                holder.InfoSubTitle.setText(model.getPhone_number());
                holder.InfoBody.setText(model.getAbout());
                holder.InfoAction.setImageDrawable(getResources().getDrawable(R.drawable.image_send));

                holder.InfoSubTitle.setVisibility(View.VISIBLE);
                holder.InfoBody.setVisibility(View.VISIBLE);
                holder.InfoAction.setVisibility(View.VISIBLE);

                holder.InfoImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent ProfileIntent = new Intent(getContext(), ProfileActivity.class);
                        ProfileIntent.putExtra("ProfileID", UserID);
                        startActivity(ProfileIntent);
                    }
                });

                holder.InfoAction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent PrivateChatIntent = new Intent(getContext(), PrivateChatActivity.class);
                        PrivateChatIntent.putExtra("ProfileID", UserID);
                        startActivity(PrivateChatIntent);
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
        ContactsList.setAdapter(adapter);
        adapter.startListening();
    }
}
