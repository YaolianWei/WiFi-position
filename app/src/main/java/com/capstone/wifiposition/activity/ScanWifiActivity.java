package com.capstone.wifiposition.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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
    private List<ScanResult> scanResults = new ArrayList<>();
    private WifiResultsAdapter wifiResultsAdapter = new WifiResultsAdapter();
    private final int REQUEST_CODE = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_wifi);
        initView();
//        获得WifiManager 获取系统Wi-Fi服务
        manager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        listReceiver = new WifiListReceiver();
//        registerReceiver(listReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
//        manager.startScan();
        if (!manager.isWifiEnabled()) {
            if (manager.getWifiState() != WifiManager.WIFI_STATE_ENABLING){
                manager.setWifiEnabled(true);
            }
        }
        startScanWifi();

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
        handler.postDelayed(() -> manager.startScan(), 1000);
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
        ScanResult scanResult = scanResults.get(position);
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

    class WifiListReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
//            获取扫描到的所有Wi-Fi信息
            scanResults = manager.getScanResults();
            Log.d(TAG, "Wifi List Size: " + scanResults.size());
            Collections.sort(scanResults, (scanResult1, scanResult2) -> {
                //  return 1 if rhs should be before lhs
                //  return -1 if lhs should be before rhs
                //  return 0 otherwise
                if (scanResult1.level > scanResult2.level) {
                    return -1;
                } else if (scanResult1.level < scanResult2.level) {
                    return 1;
                }
                return 0;
            });
            wifiResultsAdapter.setScanResults(scanResults);
            wifiResultsAdapter.notifyDataSetChanged();
            final int N = scanResults.size();

            Log.v(TAG, "Wi-Fi Scan Results ... Count:" + N);
            for(int i=0; i < N; i++) {
                Log.v(TAG, "  BSSID       =" + scanResults.get(i).BSSID);
                Log.v(TAG, "  SSID        =" + scanResults.get(i).SSID);
                Log.v(TAG, "  Capabilities=" + scanResults.get(i).capabilities);
                Log.v(TAG, "  Frequency   =" + scanResults.get(i).frequency);
                Log.v(TAG, "  Level       =" + scanResults.get(i).level);
                Log.v(TAG, "---------------");
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!manager.isWifiEnabled()) {
            manager.setWifiEnabled(false);
        }
    }

    public void startScanWifi() {
//        如果API level >=23 (Android 6.0) 时
        if ( Build.VERSION.SDK_INT >= 23) {
//            判断是否具有权限
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                判断是否需要向用户解释为什么需要申请该权限
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
//                    showToast("自Android 6.0开始需要打开该权限");
                }
//                请求权限
                ActivityCompat.requestPermissions(this, new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
                Log.i(TAG, "User location NOT ENABLED, waiting for permission");

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                用户允许改权限，0表示允许，-1表示拒绝 PERMISSION_GRANTED = 0， PERMISSION_GRANTED = -1
//                    start scanning
                    manager.startScan();
                } else {
//                    Permission for location Denied
                    Toast.makeText( this,"Well can't help you then!" , Toast.LENGTH_SHORT).show();
                }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
