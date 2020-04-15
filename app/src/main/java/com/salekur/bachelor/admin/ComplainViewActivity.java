package com.salekur.bachelor.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.salekur.bachelor.R;
import com.salekur.bachelor.classes.Complain;
import com.salekur.bachelor.classes.Users;
import com.salekur.bachelor.classes.ViewHolder;

public class ComplainViewActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private RecyclerView ComplainList;
    private DatabaseReference RootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complain_view);

        toolbar = (Toolbar) findViewById(R.id.complain_view_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Complains");

        ComplainList = (RecyclerView) findViewById(R.id.complain_view_recycler);
        ComplainList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        RootRef = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Complain>().setQuery(RootRef.child("Bachelor").child("Complains"), Complain.class).build();
        FirebaseRecyclerAdapter<Complain, ViewHolder> adapter = new FirebaseRecyclerAdapter<Complain, ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull final Complain model) {
                holder.InfoImage.setImageDrawable(getResources().getDrawable(R.drawable.image_complain));
                holder.InfoTitle.setText(model.getTitle());
                holder.InfoSubTitle.setText(model.getDate() + " " + model.getTime());
                holder.InfoSubTitle.setVisibility(View.VISIBLE);
                holder.InfoBody.setText(model.getDetails());
                holder.InfoBody.setVisibility(View.VISIBLE);
                holder.InfoAction.setVisibility(View.VISIBLE);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Dialog dialog = new Dialog(ComplainViewActivity.this);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                        dialog.setCancelable(false);
                        dialog.setContentView(R.layout.dialog_default);

                        TextView DialogTitle = (TextView) dialog.findViewById(R.id.dialog_default_title);
                        TextView DialogDes = (TextView) dialog.findViewById(R.id.dialog_default_description);
                        TextView DialogBody = (TextView) dialog.findViewById(R.id.dialog_default_message);
                        MaterialButton DialogOk = (MaterialButton) dialog.findViewById(R.id.dialog_default_ok);

                        DialogTitle.setText(model.getTitle());
                        DialogDes.setText("Date: " + model.getDate() + " " + model.getTime());
                        DialogBody.setText(model.getDetails());
                        DialogOk.setText("Close");

                        DialogDes.setVisibility(View.VISIBLE);
                        DialogBody.setVisibility(View.VISIBLE);

                        DialogOk.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
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
        ComplainList.setAdapter(adapter);
        adapter.startListening();
    }
}
