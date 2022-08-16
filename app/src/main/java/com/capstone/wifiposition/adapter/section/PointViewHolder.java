package com.capstone.wifiposition.adapter.section;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.wifiposition.R;

public class PointViewHolder extends RecyclerView.ViewHolder {

    final TextView name;
    final TextView identifier;
    final TextView pointX;
    final TextView pointY;

    public PointViewHolder(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.point_name);
        identifier = itemView.findViewById(R.id.point_identifier);
        pointX = itemView.findViewById(R.id.point_x);
        pointY = itemView.findViewById(R.id.point_y);
    }
}
