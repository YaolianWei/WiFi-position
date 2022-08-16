package com.capstone.wifiposition.adapter.section;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.capstone.wifiposition.model.ReferencePoint;

import java.util.ArrayList;
import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;

public class ReferencePointSection extends StatelessSection {

    private List<ReferencePoint> referencePointList = new ArrayList<>();

    public ReferencePointSection(SectionParameters sectionParameters) {
        super(sectionParameters);
    }

    @Override
    public int getContentItemsTotal() {
        return referencePointList.size();
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new PointViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        PointViewHolder pointViewHolder = (PointViewHolder) viewHolder;
        ReferencePoint referencePoint = referencePointList.get(i);
        pointViewHolder.name.setText(referencePoint.getName());
        pointViewHolder.pointX.setText(String.valueOf(referencePoint.getX()));
        pointViewHolder.pointY.setText(String.valueOf(referencePoint.getY()));
    }

    public List<ReferencePoint> getReferencePointList() {
        return referencePointList;
    }

    public void setReferencePointList(List<ReferencePoint> referencePointList) {
        this.referencePointList = referencePointList;
    }

    //    bind list header
    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
        super.onBindHeaderViewHolder(holder);
        SectionHeaderViewHolder headerViewHolder = (SectionHeaderViewHolder) holder;
        headerViewHolder.title.setText("Reference Points");
    }

    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
        return new SectionHeaderViewHolder(view);
    }
}
