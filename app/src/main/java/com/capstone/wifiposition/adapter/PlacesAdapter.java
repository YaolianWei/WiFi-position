package com.capstone.wifiposition.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.wifiposition.R;
import com.capstone.wifiposition.model.Places;

import io.realm.RealmResults;

public class PlacesAdapter extends RecyclerView.Adapter<PlacesAdapter.ViewHolder> {

    private RealmResults<Places> places;

    public PlacesAdapter(RealmResults<Places> mPlaces){
        places = mPlaces;
    }

    @Override
    public PlacesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_places, parent, false);
        ViewHolder viewHolder = new ViewHolder(linearLayout);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(PlacesAdapter.ViewHolder holder, int position) {

        holder.name.setText(places.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return places.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        // each data item is just a string in this case
        public TextView name;

        public ViewHolder(LinearLayout v) {
            super(v);
            name = v.findViewById(R.id.places_name);
        }
    }
}
