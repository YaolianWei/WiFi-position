package com.capstone.wifiposition.activity;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import androidx.annotation.Nullable;

import com.capstone.wifiposition.R;
import com.capstone.wifiposition.fragment.PreFragment;

public class NavigationActivity extends PreferenceActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction().replace(android.R.id.content, new PreFragment()).commit();
    }
}
