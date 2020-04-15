package com.salekur.bachelor.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.salekur.bachelor.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class BMealFragment extends Fragment {
    private TextView XmlGroupName;

    private DatabaseReference RootRef;
    private FirebaseUser CurrentUser;

    public BMealFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View BMealFragment = inflater.inflate(R.layout.fragment_b_meal, container, false);

        XmlGroupName = (TextView) BMealFragment.findViewById(R.id.fragment_bmeal_group_name);

        RootRef = FirebaseDatabase.getInstance().getReference();
        CurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        RetrieveGroupInformation();

        return BMealFragment;
    }

    private void RetrieveGroupInformation() {

        RootRef.child("BMeal").child("Users").child(CurrentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String GroupID = dataSnapshot.getValue().toString();

                    RootRef.child("BMeal").child("Groups").child(GroupID).child("name").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                String group_name = dataSnapshot.getValue().toString();
                                XmlGroupName.setText(group_name);
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
    }


}
