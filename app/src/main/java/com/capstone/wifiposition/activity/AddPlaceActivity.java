package com.capstone.wifiposition.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.capstone.wifiposition.R;
import com.capstone.wifiposition.model.Places;
import com.google.android.material.snackbar.Snackbar;

import java.util.Date;
import java.util.UUID;

import io.realm.Realm;

public class AddPlaceActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etPlaceName;
    private EditText etPlaceDetail;
    private Button button;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_place);
        etPlaceName = findViewById(R.id.et_place_name);
        etPlaceDetail = findViewById(R.id.et_place_detail);
        button = findViewById(R.id.bn_place_create);
        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == button.getId()) {
            final String name = etPlaceName.getText().toString().trim();
            final String detail = etPlaceDetail.getText().toString().trim();
            if (name.isEmpty()) {
                Snackbar.make(button, "Place name cannot be null.", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            } else {
                Places places = new Places(new Date(), name, detail);
//                Obtain a Realm instance
                Realm realm = Realm.getDefaultInstance();
                realm.executeTransactionAsync(realm1 -> {
                    Places place = realm1.createObject(Places.class, UUID.randomUUID().toString());
                    place.setDate(new Date());
                    place.setName(name);
                    place.setDetail(detail);

                }, AddPlaceActivity.this::finish, error -> {
//                        Transaction failed
                    System.out.print(error.getMessage());
                });
            }
        }

    }
}
