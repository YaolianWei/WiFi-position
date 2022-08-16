package com.capstone.wifiposition.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.wifiposition.R;
import com.capstone.wifiposition.model.AccessPoint;

import java.util.ArrayList;
import java.util.List;

public class AccessPointAdapter extends RecyclerView.Adapter<AccessPointAdapter.ViewHolder> {

    private List<AccessPoint> accessPoints = new ArrayList<>();

    @Override
    public AccessPointAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_point_info, parent, false);
        // set the view's size, margins, paddings and layout parameters
        AccessPointAdapter.ViewHolder viewHolder = new ViewHolder(linearLayout);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AccessPointAdapter.ViewHolder holder, int position) {
        holder.bssid.setText(accessPoints.get(position).getBssid());
        holder.ssid.setText(accessPoints.get(position).getSsid());
        holder.level.setText(String.valueOf(accessPoints.get(position).getMeanRss()));
    }

    @Override
    public int getItemCount() {
        return accessPoints.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView bssid, ssid, level;

        public ViewHolder(LinearLayout v) {
            super(v);
            bssid = v.findViewById(R.id.wifi_bssid);
            ssid = v.findViewById(R.id.wifi_ssid);
            level = v.findViewById(R.id.wifi_level);
        }
    }

    public List<AccessPoint> getAccessPoints() {
        return accessPoints;
    }

    public void setAccessPoints(List<AccessPoint> accessPoints) {
        this.accessPoints = accessPoints;
    }

    public void addAP(AccessPoint accessPoint) {
        accessPoints.add(accessPoint);
    }
}
