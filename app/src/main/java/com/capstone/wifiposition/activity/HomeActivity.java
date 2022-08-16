package com.capstone.wifiposition.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.wifiposition.R;
import com.capstone.wifiposition.adapter.PlacesAdapter;
import com.capstone.wifiposition.model.Places;
import com.capstone.wifiposition.utils.RecyclerItemClickListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener, RecyclerItemClickListener.OnItemClickListener {

    private Realm realm;
    private RealmResults<Places> places;
    private RecyclerView mRecyclerView;
    private PlacesAdapter mPlacesAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        initView();
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder().build();
//        Create a new empty instance of Realm
        realm = Realm.getInstance(realmConfiguration);

        places = realm.where(Places.class).findAll();
        if (places.isEmpty()) {
            Snackbar.make(fab, "Empty list.", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }

//        Specify an adapter
        mPlacesAdapter = new PlacesAdapter(places);
        mRecyclerView.setAdapter(mPlacesAdapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        mPlacesAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_algo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.settings:
                Intent intent = new Intent(this, NavigationActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initView(){
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(this);
        mRecyclerView = findViewById(R.id.places_recyclerview);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this,mRecyclerView, this));
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == fab.getId()) {
            Intent intent = new Intent(this, AddPlaceActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent(this, PlacesDetailActivity.class);
        Places place = places.get(position);
        intent.putExtra("id", place.getId());
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(View view, int position) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!realm.isClosed()) {
            realm.close();
        }
    }
}
