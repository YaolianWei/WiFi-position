package com.capstone.wifiposition.adapter;

import android.net.wifi.ScanResult;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.wifiposition.R;

import java.util.ArrayList;
import java.util.List;

public class WifiResultsAdapter extends RecyclerView.Adapter<WifiResultsAdapter.ViewHolder> {

    public List<ScanResult> scanResults = new ArrayList<>();

//    当需要新的ViewHolder来显示列表项时，会调用onCreateViewHolder方法去创建ViewHolder
//    Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public WifiResultsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_wifi_results, parent, false);
        // set the view's size, margins, paddings and layout parameters
        WifiResultsAdapter.ViewHolder viewHolder = new WifiResultsAdapter.ViewHolder(linearLayout);
        return viewHolder;
    }

//    将数据绑定在ViewHolder上。
//    Replace/Bind the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull WifiResultsAdapter.ViewHolder holder, int position) {
        holder.bssid.setText("MAC: " + scanResults.get(position).BSSID);
        holder.ssid.setText("SSID: " + scanResults.get(position).SSID);
        holder.capabilities.setText("Type: " + scanResults.get(position).capabilities);
        holder.frequency.setText("Frequency: " + String.valueOf(scanResults.get(position).frequency));
        holder.level.setText(String.valueOf("RSSI:" + scanResults.get(position).level));
    }

//    返回总共要显示的列表的数量(创建的ViewHolder数量比前者要小得多)。
//    Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return scanResults.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView bssid, ssid, capabilities, level, frequency;

        public ViewHolder(LinearLayout v) {
            super(v);
            bssid = v.findViewById(R.id.wifi_bssid);
            ssid = v.findViewById(R.id.wifi_ssid);
            capabilities = v.findViewById(R.id.wifi_capabilities);
            frequency = v.findViewById(R.id.wifi_frequency);
            level = v.findViewById(R.id.wifi_level);
        }
    }

    public List<ScanResult> getScanResults() {
        return scanResults;
    }

    public void setScanResults(List<ScanResult> scanResults) {
        this.scanResults = scanResults;
    }
}
