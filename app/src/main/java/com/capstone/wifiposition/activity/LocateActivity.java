package com.capstone.wifiposition.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.wifiposition.R;
import com.capstone.wifiposition.adapter.DistanceAdapter;
import com.capstone.wifiposition.algorithm.Algorithms;
import com.capstone.wifiposition.fragment.WifiService;
import com.capstone.wifiposition.model.Distance;
import com.capstone.wifiposition.model.Location;
import com.capstone.wifiposition.model.Places;
import com.capstone.wifiposition.model.WifiData;
import com.capstone.wifiposition.utils.AppConfig;

import io.realm.Realm;

public class LocateActivity extends AppCompatActivity {

    private WifiData mWifiData;
    private Algorithms algorithm = new Algorithms();
    private String placeID, defaultAlgo;
    private Places place;
    private MainActivityReceiver mReceiver = new MainActivityReceiver();
    private Intent wifiServiceIntent;
    private TextView tvLocation, tvNearestLocation, tvDistance;
    private RecyclerView rvPoints;
    private LinearLayoutManager layoutManager;
    private DistanceAdapter readingsAdapter = new DistanceAdapter();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mWifiData = null;

        // set receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, new IntentFilter(AppConfig.INTENT_FILTER));

        // launch WiFi service
        wifiServiceIntent = new Intent(this, WifiService.class);
        startService(wifiServiceIntent);

        // recover retained object
        mWifiData = (WifiData) getLastNonConfigurationInstance();

        // set layout
        setContentView(R.layout.activity_locate);
        initView();

        defaultAlgo = AppConfig.getDefaultAlgo(this);
        placeID = getIntent().getStringExtra("placeID");
        if (placeID == null) {
            Toast.makeText(getApplicationContext(), "Place Not Found", Toast.LENGTH_LONG).show();
            this.finish();
        }
        Realm realm = Realm.getDefaultInstance();
        place = realm.where(Places.class).equalTo("id", placeID).findFirst();
        Log.v("LocateActivity", "onCreate");
    }

    private void initView() {
        layoutManager = new LinearLayoutManager(this);
        tvLocation = findViewById(R.id.location);
        tvNearestLocation = findViewById(R.id.nearest_location);
        tvDistance = findViewById(R.id.distance_origin);
        rvPoints = findViewById(R.id.rv_nearby_points);
        rvPoints.setLayoutManager(layoutManager);
        rvPoints.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        rvPoints.setAdapter(readingsAdapter);
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return mWifiData;
    }

    public class MainActivityReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.v("LocateActivity", "MainActivityReceiver");
            mWifiData = (WifiData) intent.getParcelableExtra(AppConfig.WIFI_DATA);

            if (mWifiData != null) {
                Location loc = Algorithms.processingAlgorithms(mWifiData.getNetworks(), place, Integer.parseInt(defaultAlgo));
                Log.v("LocateActivity", "loc:" + loc);
                if (loc == null) {
                    tvLocation.setText("Location: NA\nNote:Please switch on your wifi and location services with permission provided to App");
                } else {
                    String locationValue = AppConfig.reduceDecimalPlaces(loc.getLocation());
                    tvLocation.setText("Location(x,y): (" + locationValue + ")");
                    String distanceFromOrigin = AppConfig.getDistanceFromOrigin(loc.getLocation());
                    tvDistance.setText("The distance from origin is: " + distanceFromOrigin + "m");
                    Distance theNearestPoint = AppConfig.getNearestPoint(loc);
                    if (theNearestPoint != null) {
                        tvNearestLocation.setText("You are near to: " + theNearestPoint.getName());
                    }
                    readingsAdapter.setDistances(loc.getDistances());
                    readingsAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
        stopService(wifiServiceIntent);
    }
}
