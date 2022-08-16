package com.capstone.wifiposition.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.wifiposition.R;
import com.capstone.wifiposition.model.Distance;

import java.util.ArrayList;
import java.util.List;

public class DistanceAdapter extends RecyclerView.Adapter<DistanceAdapter.ViewHolder> {

    private ArrayList<Distance> distances = new ArrayList<>();

    @NonNull
    @Override
    public DistanceAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_point_info, parent, false);
        // set the view's size, margins, paddings and layout parameters
        DistanceAdapter.ViewHolder viewHolder = new DistanceAdapter.ViewHolder(linearLayout);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull DistanceAdapter.ViewHolder holder, int position) {
        holder.distance.setText((int) distances.get(position).getDistance());
        holder.location.setText(distances.get(position).getLocation());
        holder.name.setText(String.valueOf(distances.get(position).getName()));
    }

    @Override
    public int getItemCount() {
        return distances.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView name, location, distance;

        public ViewHolder(LinearLayout v) {
            super(v);
            name = v.findViewById(R.id.wifi_ssid);
            location = v.findViewById(R.id.wifi_bssid);
            distance = v.findViewById(R.id.wifi_level);
        }
    }

    public List<Distance> getDistances() {
        return distances;
    }

    public void setDistances(ArrayList<Distance> distances) {
        this.distances = distances;
    }

    public void addAP(Distance distance) {
        distances.add(distance);
    }
}
