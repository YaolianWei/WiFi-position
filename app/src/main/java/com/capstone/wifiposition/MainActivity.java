package com.capstone.wifiposition;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class MainActivity extends Application {

    @Override
    public void onCreate(){
        super.onCreate();
        Realm.init(this);
    }
}
