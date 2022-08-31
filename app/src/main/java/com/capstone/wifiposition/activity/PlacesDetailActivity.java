package com.capstone.wifiposition.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.wifiposition.R;
import com.capstone.wifiposition.adapter.section.AccessPointSection;
import com.capstone.wifiposition.adapter.section.ReferencePointSection;
import com.capstone.wifiposition.model.AccessPoint;
import com.capstone.wifiposition.model.Places;
import com.capstone.wifiposition.model.ReferencePoint;
import com.capstone.wifiposition.utils.RecyclerItemClickListener;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class PlacesDetailActivity extends AppCompatActivity implements View.OnClickListener, RecyclerItemClickListener.OnItemClickListener {

    private RecyclerView recyclerView;
    private Button bnAddAp;
    private Button bnAddRp;
    private Button bnLocate;
    private Places place;
    private SectionedRecyclerViewAdapter sectionAdapter = new SectionedRecyclerViewAdapter();
    private AccessPointSection apSection;
    private ReferencePointSection rpSection;
    private LinearLayoutManager layoutManager;
    private String placeID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_detail);

        placeID = getIntent().getStringExtra("id");
        if (placeID == null) {
            Toast.makeText(getApplicationContext(), "Not Found.", Toast.LENGTH_LONG).show();
            this.finish();
        }
        Log.i("PlacesDetailActivity", "id > " + placeID);

        Realm realm = Realm.getDefaultInstance();
//        RealmConfiguration config = new RealmConfiguration.Builder()
//                .name("placeDetails.realm")
//                .schemaVersion(0)
//                .build();
//        Realm realm = Realm.getInstance(config);
        place = realm.where(Places.class).equalTo("id", placeID).findFirst();

        initView();
    }

    private void initView() {
        recyclerView = findViewById(R.id.rv_points);
        bnAddAp = findViewById(R.id.bn_add_ap);
        bnAddRp = findViewById(R.id.bn_add_rp);
        bnLocate = findViewById(R.id.bn_locate);

        bnAddAp.setOnClickListener(this);
        bnAddRp.setOnClickListener(this);
        bnLocate.setOnClickListener(this);

        calCounts();

        SectionParameters parameters = new SectionParameters.Builder(R.layout.item_point_details)
                .headerResourceId(R.layout.item_section_details).build();
        apSection = new AccessPointSection(parameters);
        apSection.setAccessPointList(place.getAps());
        sectionAdapter.addSection(apSection);

        rpSection = new ReferencePointSection(parameters);
        rpSection.setReferencePointList(place.getRps());
        sectionAdapter.addSection(rpSection);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(sectionAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerView, this));
    }

    private void calCounts() {
        String name = place.getName();
        int apCount = place.getAps().size();
        int rpCount = place.getRps().size();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(name);
        }
        if (apCount > 0) {
            ((TextView)findViewById(R.id.ap_count)).setText("Access Points:" + String.valueOf(apCount));
        }
        if (rpCount > 0) {
            ((TextView)findViewById(R.id.rp_count)).setText("Reference Points:" + String.valueOf(apCount));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        sectionAdapter.notifyDataSetChanged();
        calCounts();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == bnAddAp.getId()) {
            startApActivity("");
        } else if (view.getId() == bnAddRp.getId()) {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 198);
                //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overridden method
            } else{
                startRpActivity(null);
            }
        } else if (view.getId() == bnLocate.getId()) {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 197);
                //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overridden method
            } else{
                startLocateActivity();
            }
        }
    }

    private void startApActivity(String apID) {
        Intent intent = new Intent(this, AccessPointActivity.class);
        intent.putExtra("placeID", placeID);
        intent.putExtra("apID", apID);
        startActivity(intent);
    }

    private void startRpActivity(String rpID) {
        Intent intent = new Intent(this, ReferencePointActivity.class);
        intent.putExtra("placeID", placeID);
        intent.putExtra("rpID", rpID);
        startActivity(intent);
    }

    private void startLocateActivity() {
        Intent intent = new Intent(this, LocateActivity.class);
        intent.putExtra("placeID", placeID);
        startActivity(intent);
    }

    @Override
    public void onItemClick(View view, int position) {
        int apsCount = 0;
        if (place.getAps() != null) {
            apsCount = place.getAps().size();
        }
        if (position <= apsCount && position != 0) {//AP section event
            AccessPoint accessPoint = place.getAps().get(position - 1);
            startApActivity(accessPoint.getId());
        } else if (position > (apsCount + 1)) {//RP section event
            ReferencePoint referencePoint = place.getRps().get(position - apsCount - 1 - 1);
            startRpActivity(referencePoint.getId());
        }
    }

    @Override
    public void onItemLongClick(View view, int position) {
        int apsCount = 0;
        if (place.getAps() != null) {
            apsCount = place.getAps().size();
        }
        if (position <= apsCount && position != 0) {//AP section event
            AccessPoint accessPoint = place.getAps().get(position - 1);
            showDeleteDialog(accessPoint, null);
        } else if (position > (apsCount+1)) {//RP section event
            ReferencePoint referencePoint = place.getRps().get(position - apsCount - 1 - 1);
            showDeleteDialog(null, referencePoint);
        }
    }

    private void showDeleteDialog(final AccessPoint accessPoint,final ReferencePoint referencePoint) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Theme_AppCompat_DayNight_Dialog);
        if (accessPoint != null) {
            builder.setTitle("Delete this Access Point");
            builder.setMessage("Delete "+ accessPoint.getSsid());
        } else {
            builder.setTitle("Delete this Reference Point");
            builder.setMessage("Delete "+ referencePoint.getName());
        }

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Realm realm = Realm.getDefaultInstance();
                if (accessPoint != null) {
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            accessPoint.deleteFromRealm();
                            refreshList();
                        }
                    });
                } else {
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            referencePoint.deleteFromRealm();
//                            project.getRps().deleteAllFromRealm();
                            refreshList();
                        }
                    });
                }
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void refreshList() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                sectionAdapter.notifyDataSetChanged();
            }
        });
    }
}
