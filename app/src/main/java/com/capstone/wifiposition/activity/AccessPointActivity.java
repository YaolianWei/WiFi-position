package com.capstone.wifiposition.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.capstone.wifiposition.R;
import com.capstone.wifiposition.model.AccessPoint;
import com.capstone.wifiposition.model.Places;
import com.google.android.material.snackbar.Snackbar;

import java.util.Date;
import java.util.UUID;

import io.realm.Realm;

public class AccessPointActivity extends AppCompatActivity implements View.OnClickListener {

    private Button bnScanAp;
    private Button bnAddAp;
    private EditText name, desc, x, y, mac;
    private String placeID, apID;
    private boolean isEdit = false;
    private AccessPoint accessPoint;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_access_point);

        placeID = getIntent().getStringExtra("placeID");
        if (placeID == null) {
            Toast.makeText(this, "Access point not found.", Toast.LENGTH_LONG).show();
            this.finish();
        }

        apID = getIntent().getStringExtra("apID");
        initView();

        if (apID.equals("")) {
            isEdit = false;
        } else {
            isEdit = true;
            bnAddAp.setText("Save");
        }

        if (isEdit) {
            setEditMode();
        }
    }

    private void initView(){
        name = findViewById(R.id.ap_name);
        desc = findViewById(R.id.ap_desc);
        x = findViewById(R.id.ap_x);
        y = findViewById(R.id.ap_y);
        mac = findViewById(R.id.ap_mac);
        bnAddAp = findViewById(R.id.bn_ap_add);
        bnScanAp = findViewById(R.id.bn_ap_scan);

        bnAddAp.setOnClickListener(this);
        bnScanAp.setOnClickListener(this);

    }

    ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK) {
                Intent intent = result.getData();
                AccessPoint accessPoint = (AccessPoint) intent.getParcelableExtra("accessPoint");
                setValuesToFields(accessPoint);
            }
        }
    });

    private void setEditMode() {
        Realm realm = Realm.getDefaultInstance();
        accessPoint = realm.where(AccessPoint.class).equalTo("id", apID).findFirst();
        setValuesToFields(accessPoint);
    }

    private void setValuesToFields(AccessPoint accessPoint) {
        name.setText(accessPoint.getSsid());
        desc.setText(accessPoint.getDescription());
        x.setText(String.valueOf(accessPoint.getX()));
        y.setText(String.valueOf(accessPoint.getY()));
        mac.setText(accessPoint.getMac_address());
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == bnAddAp.getId()) {
            final String text = name.getText().toString().trim();
            final String description = desc.getText().toString().trim();
            final String apX = x.getText().toString().trim();
            final String apY = y.getText().toString().trim();
            final String apMac = mac.getText().toString().trim();
            final boolean isEditMode = isEdit;

            if (text.isEmpty()) {
                Snackbar.make(bnAddAp, "Provide Access Point Name", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            } else {
                // Obtain a Realm instance
                Realm realm = Realm.getDefaultInstance();
                realm.beginTransaction();
                Places place = realm.where(Places.class).equalTo("id", placeID).findFirst();
                if (isEditMode) {
                    accessPoint.setSsid(text);
                    accessPoint.setDescription(description);
                    accessPoint.setX(Double.valueOf(apX));
                    accessPoint.setY(Double.valueOf(apY));
                    accessPoint.setMac_address(apMac);
                } else {
                    AccessPoint ap = realm.createObject(AccessPoint.class, UUID.randomUUID().toString());
                    ap.setBssid(apMac);
                    ap.setDescription(description);
                    ap.setDate(new Date());
                    ap.setX(Double.valueOf(apX));
                    ap.setY(Double.valueOf(apY));
                    ap.setSsid(text);
                    ap.setMac_address(apMac);
                    place.getAps().add(ap);
                }
                realm.commitTransaction();
                this.finish();
            }
        } else if (view.getId() == bnScanAp.getId()) {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 199);
                //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overridden method

            } else{
                launcher.launch(new Intent(this, ScanWifiActivity.class));
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 199 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            launcher.launch(new Intent(this, ScanWifiActivity.class));
        }
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == 1212 && resultCode == RESULT_OK) {
//            AccessPoint accessPoint = (AccessPoint) data.getParcelableExtra("accessPoint");
//            setValuesToFields(accessPoint);
//        }
//    }
}
