package com.capstone.wifiposition.adapter.section;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.wifiposition.R;

public class SectionHeaderViewHolder extends RecyclerView.ViewHolder {

    final TextView title;

    public SectionHeaderViewHolder(@NonNull View itemView) {
        super(itemView);
        title = (TextView) itemView.findViewById(R.id.section_name);
    }
}
