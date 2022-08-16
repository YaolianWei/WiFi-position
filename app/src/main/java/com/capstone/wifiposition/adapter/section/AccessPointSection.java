package com.capstone.wifiposition.adapter.section;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.capstone.wifiposition.model.AccessPoint;

import java.util.ArrayList;
import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;

public class AccessPointSection extends StatelessSection {

    private List<AccessPoint> accessPointList = new ArrayList<>();

    public AccessPointSection(SectionParameters sectionParameters) {
        super(sectionParameters);
    }

    @Override
    public int getContentItemsTotal() {
        return accessPointList.size();
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new PointViewHolder(view);
    }

//    bind list item
    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        PointViewHolder pointViewHolder = (PointViewHolder) viewHolder;
        AccessPoint accessPoint = accessPointList.get(i);
        pointViewHolder.name.setText(accessPoint.getSsid());
        pointViewHolder.identifier.setText(accessPoint.getMac_address());
        pointViewHolder.pointX.setText(String.valueOf(accessPoint.getX()));
        pointViewHolder.pointY.setText(String.valueOf(accessPoint.getY()));
    }

    public List<AccessPoint> getAccessPointList() {
        return accessPointList;
    }

    public void setAccessPointList(List<AccessPoint> accessPointList) {
        this.accessPointList = accessPointList;
    }

//    bind list header
    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
        super.onBindHeaderViewHolder(holder);
        SectionHeaderViewHolder headerViewHolder = (SectionHeaderViewHolder) holder;
        headerViewHolder.title.setText("Access Points");
    }

    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
        return new SectionHeaderViewHolder(view);
    }
}
