package com.capstone.wifiposition.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.wifiposition.R;
import com.capstone.wifiposition.adapter.WifiResultsAdapter;
import com.capstone.wifiposition.model.AccessPoint;
import com.capstone.wifiposition.utils.RecyclerItemClickListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class ScanWifiActivity extends AppCompatActivity implements View.OnClickListener, RecyclerItemClickListener.OnItemClickListener {

    private String TAG = "ScanWifiActivity";
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private WifiManager manager;
    private WifiListReceiver listReceiver;
    private final Handler handler = new Handler();
    private Button bnRefresh;
    private List<ScanResult> results = new ArrayList<>();
    private WifiResultsAdapter wifiResultsAdapter = new WifiResultsAdapter();
    private boolean wifiEnabled;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_wifi);
        initView();
        manager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        listReceiver = new WifiListReceiver();
        wifiEnabled = manager.isWifiEnabled();
        if (!manager.isWifiEnabled()) {
            manager.setWifiEnabled(true);
        }
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(wifiResultsAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerView, this));
    }

    private void initView() {
        recyclerView = findViewById(R.id.rv_wifis);
        bnRefresh = findViewById(R.id.bn_wifi_refresh);
        bnRefresh.setOnClickListener(this);
    }

    public void refresh() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                manager.startScan();
            }
        }, 1000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(listReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        refresh();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(listReceiver);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == bnRefresh.getId()) {
            refresh();
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        AccessPoint accessPoint = new AccessPoint();
        ScanResult scanResult = results.get(position);
        accessPoint.setId(UUID.randomUUID().toString());
        accessPoint.setMac_address(scanResult.BSSID);
        accessPoint.setSsid(scanResult.SSID);
        accessPoint.setBssid(scanResult.BSSID);
        accessPoint.setDescription(scanResult.capabilities);

        Intent intent = new Intent();
        intent.putExtra("accessPoint", accessPoint);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onItemLongClick(View view, int position) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!wifiEnabled) {
            manager.setWifiEnabled(false);
        }
    }

    class WifiListReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            results = manager.getScanResults();
            Collections.sort(results, new Comparator<ScanResult>() {
                @Override
                public int compare(ScanResult scanResult1, ScanResult scanResult2) {
                    //  return 1 if rhs should be before lhs
                    //  return -1 if lhs should be before rhs
                    //  return 0 otherwise
                    if (scanResult1.level > scanResult2.level) {
                        return -1;
                    } else if (scanResult1.level < scanResult2.level) {
                        return 1;
                    }
                    return 0;
                }
            });
            wifiResultsAdapter.setScanResults(results);
            wifiResultsAdapter.notifyDataSetChanged();
            final int N = results.size();

            Log.v(TAG, "Wi-Fi Scan Results ... Count:" + N);
            for(int i=0; i < N; ++i) {
                Log.v(TAG, "  BSSID       =" + results.get(i).BSSID);
                Log.v(TAG, "  SSID        =" + results.get(i).SSID);
                Log.v(TAG, "  Capabilities=" + results.get(i).capabilities);
                Log.v(TAG, "  Frequency   =" + results.get(i).frequency);
                Log.v(TAG, "  Level       =" + results.get(i).level);
                Log.v(TAG, "---------------");
            }
        }
    }
}
