package com.capstone.wifiposition.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.wifiposition.R;
import com.capstone.wifiposition.adapter.AccessPointAdapter;
import com.capstone.wifiposition.model.AccessPoint;
import com.capstone.wifiposition.model.Places;
import com.capstone.wifiposition.model.ReferencePoint;
import com.capstone.wifiposition.utils.AppConfig;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmList;

public class ReferencePointActivity extends AppCompatActivity implements View.OnClickListener {

    private String TAG = "ReferencePointActivity";
    private String placeID;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private EditText rpName, rpX, rpY;
    private Button bnRpSave;
    private AccessPointAdapter pointAdapter = new AccessPointAdapter();
    private List<AccessPoint> accessPointList = new ArrayList<>();
    private Map<String, List<Integer>> list = new HashMap<>();
    private Map<String, AccessPoint> aps = new HashMap<>();

    private AvailableAPsReceiver receiverWifi;

    private boolean wifiEnabled;
    private WifiManager wifiManager;
    private final Handler handler = new Handler();
    private boolean isCalibrating = false;
    private int readingsCount = 0;
    private boolean isEdit = false;
    private String rpID;
    private ReferencePoint referencePoint;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reference_point);

        placeID = getIntent().getStringExtra("placeID");
        if (placeID == null) {
            Toast.makeText(this, "Reference point not found", Toast.LENGTH_LONG).show();
            this.finish();
        }

        if (getIntent().getStringExtra("rpID") != null) {
            isEdit = true;
            rpID = getIntent().getStringExtra("rpID");
        }

        initView();
        Realm realm = Realm.getDefaultInstance();
        if (isEdit) {
            referencePoint = realm.where(ReferencePoint.class).equalTo("id", rpID).findFirst();
            if (referencePoint == null) {
                Toast.makeText(this, "Reference point not found", Toast.LENGTH_LONG).show();
                this.finish();
            }
            RealmList<AccessPoint> apList = referencePoint.getScanAps();
            for (AccessPoint ap : apList) {
                pointAdapter.addAP(ap);
            }
            pointAdapter.notifyDataSetChanged();
            rpName.setText(referencePoint.getName());
            rpX.setText(String.valueOf(referencePoint.getX()));
            rpY.setText(String.valueOf(referencePoint.getY()));
        } else {
            wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            receiverWifi = new AvailableAPsReceiver();
            wifiEnabled = wifiManager.isWifiEnabled();

            Places place = realm.where(Places.class).equalTo("id", placeID).findFirst();
            RealmList<AccessPoint> points = place.getAps();
            for (AccessPoint accessPoint : points) {
                aps.put(accessPoint.getMac_address(), accessPoint);
            }
            if (aps.isEmpty()) {
                Toast.makeText(this, "No Access Points Found", Toast.LENGTH_SHORT).show();
            }
            if (!AppConfig.isLocationEnabled(this)) {
                Toast.makeText(this,"Please turn on the location", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onResume() {
        if (!isEdit) {
            registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
            Log.v(TAG, "calibrationStarted");
            if (!isCalibrating) {
                isCalibrating = true;
                refresh();
            }
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (!isEdit) {
            unregisterReceiver(receiverWifi);
            isCalibrating = false;
        }
        super.onPause();
    }

    public void refresh() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                wifiManager.startScan();
                if (readingsCount < AppConfig.READINGS_BATCH) {
                    refresh();
                } else {
                    caliberationCompleted();
                }
            }
        }, AppConfig.FETCH_INTERVAL);
    }

    private void caliberationCompleted() {
        isCalibrating = false;
        Log.v(TAG, "caliberationCompleted");
        Map<String, List<Integer>> values = list;
        Log.v(TAG, "values:"+values.toString());
        for (Map.Entry<String, List<Integer>> entry : values.entrySet()) {
            List<Integer> readingsOfAMac = entry.getValue();
            Double mean = calculateMeanValue(readingsOfAMac);
            Log.v(TAG, "entry.Key:"+entry.getKey()+" aps:"+aps);
            AccessPoint accessPoint = aps.get(entry.getKey());
            AccessPoint updatedPoint = new AccessPoint(accessPoint);
            updatedPoint.setMeanRss(mean);
            accessPointList.add(updatedPoint);
        }
        pointAdapter.setAccessPoints(accessPointList);
        pointAdapter.notifyDataSetChanged();
        bnRpSave.setEnabled(true);
        bnRpSave.setText("Save");
    }

    private Double calculateMeanValue(List<Integer> readings) {
        if (readings.isEmpty()) {
            return 0.0d;
        }
        Integer sum = 0;
        for (Integer integer : readings) {
            sum = sum + integer;
        }
        double mean = Double.valueOf(sum) / Double.valueOf(readings.size());
        return mean;
    }

    private void initView() {
        layoutManager = new LinearLayoutManager(this);
        recyclerView = findViewById(R.id.rv_points);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(pointAdapter);

        bnRpSave = findViewById(R.id.bn_rp_save);
        bnRpSave.setOnClickListener(this);

        if (!isEdit) {
            bnRpSave.setEnabled(false);
            bnRpSave.setText("Caliberating...");
        } else {
            bnRpSave.setEnabled(true);
            bnRpSave.setText("Save");
        }

        rpName = findViewById(R.id.rp_name);
        rpX = findViewById(R.id.rp_x);
        rpY = findViewById(R.id.rp_y);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == bnRpSave.getId() && !isEdit) {
            Realm realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            ReferencePoint referencePoint = new ReferencePoint();
            referencePoint = setValues(referencePoint);
            referencePoint.setDate(Calendar.getInstance().getTime());
            referencePoint.setDescription("");
//            apsWithReading = realm.copyToRealmOrUpdate(apsWithReading);
            if (referencePoint.getScanAps() == null) {
                RealmList<AccessPoint> readings = new RealmList<>();
                readings.addAll(accessPointList);
                referencePoint.setsSanAps(readings);
            } else {
                referencePoint.getScanAps().addAll(accessPointList);
            }

            referencePoint.setId(UUID.randomUUID().toString());

            Places place = realm.where(Places.class).equalTo("id", placeID).findFirst();
            if (place.getRps() == null) {
                RealmList<ReferencePoint> points = new RealmList<>();
                points.add(referencePoint);
                place.setRps(points);
            } else {
                place.getRps().add(referencePoint);
            }

            realm.commitTransaction();
            Toast.makeText(this,"Reference Point Added", Toast.LENGTH_SHORT).show();
            this.finish();
        } else if (view.getId() == bnRpSave.getId() && isEdit) {
            Realm realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            referencePoint = setValues(referencePoint);
            realm.commitTransaction();
            Toast.makeText(this,"Reference Point Updated", Toast.LENGTH_SHORT).show();
            this.finish();
        }
    }

    private ReferencePoint setValues(ReferencePoint referencePoint) {
        String x = rpX.getText().toString();
        String y = rpY.getText().toString();
        if (TextUtils.isEmpty(x)) {
            referencePoint.setX(0.0d);
        } else {
            referencePoint.setX(Double.valueOf(x));
        }

        if (TextUtils.isEmpty(y)) {
            referencePoint.setY(0.0d);
        } else {
            referencePoint.setY(Double.valueOf(y));
        }
        referencePoint.setLocId(referencePoint.getX() + " " + referencePoint.getY());
        referencePoint.setName(rpName.getText().toString());
        return referencePoint;
    }

    class AvailableAPsReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            List<ScanResult> scanResults = wifiManager.getScanResults();
            ++readingsCount;
            for (Map.Entry<String, AccessPoint> entry : aps.entrySet()) {
                String apMac = entry.getKey();
                for (ScanResult scanResult : scanResults) {
                    if (entry.getKey().equals(scanResult.BSSID)) {
                        checkAndAddApRSS(apMac, scanResult.level);
                        apMac = null;//do this after always :|
                        break;
                    }
                }
                if (apMac != null) {
                    checkAndAddApRSS(apMac, AppConfig.NaN.intValue());
                }
            }
//            results.put(Calendar.getInstance(), map);

            Log.v(TAG, "Count:" + readingsCount+" scanResult:"+ scanResults.toString()+" aps:"+aps.toString());
            for (int i = 0; i < readingsCount; ++i) {
//                Log.v(TAG, "  BSSID       =" + results.get(i).BSSID);
//                Log.v(TAG, "  SSID        =" + results.get(i).SSID);
//                Log.v(TAG, "  Capabilities=" + results.get(i).capabilities);
//                Log.v(TAG, "  Frequency   =" + results.get(i).frequency);
//                Log.v(TAG, "  Level       =" + results.get(i).level);
//                Log.v(TAG, "---------------");
            }
        }
    }

    private void checkAndAddApRSS(String apMac, Integer level) {
        if (list.containsKey(apMac)) {
            List<Integer> integers = list.get(apMac);
            integers.add(level);
        } else {
            List<Integer> integers = new ArrayList<>();
            integers.add(level);
            list.put(apMac, integers);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!wifiEnabled && !isEdit) {
            wifiManager.setWifiEnabled(false);
        }
    }
}
