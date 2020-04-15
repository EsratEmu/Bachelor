package com.salekur.bachelor.classes;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.salekur.bachelor.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewHolder extends RecyclerView.ViewHolder {
    public CircleImageView InfoImage;
    public TextView InfoTitle, InfoSubTitle, InfoBody;
    public ImageView InfoAction;

    public ViewHolder(@NonNull View itemView) {
        super(itemView);

        InfoImage = (CircleImageView) itemView.findViewById(R.id.information_image);
        InfoTitle = (TextView) itemView.findViewById(R.id.information_header);
        InfoSubTitle = (TextView) itemView.findViewById(R.id.information_sub_header);
        InfoBody = (TextView) itemView.findViewById(R.id.information_body);
        InfoAction = (ImageView) itemView.findViewById(R.id.information_action);
    }
}
